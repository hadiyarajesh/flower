/*
 *  Copyright (C) 2022 Rajesh Hadiya
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.hadiyarajesh.compose_app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.hadiyarajesh.compose_app.database.entity.Image
import com.hadiyarajesh.compose_app.ui.component.ImageItem
import com.hadiyarajesh.compose_app.ui.component.LoadingProgressBar
import com.hadiyarajesh.compose_app.ui.component.RetryItem
import com.hadiyarajesh.compose_app.ui.component.items
import com.hadiyarajesh.compose_app.viewmodel.HomeViewModel

@ExperimentalPagingApi
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val images = remember { homeViewModel.images }.collectAsLazyPagingItems()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = images,
                    key = { it.id }
                ) { item ->
                    item?.let {
                        Image(it)
                    }
                }
            }

            images.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        LoadingProgressBar(modifier = Modifier.fillMaxSize())
                    }

                    loadState.append is LoadState.Loading -> {
                        LoadingProgressBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }

                    loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && images.itemCount < 1 -> {

                    }

                    loadState.refresh is LoadState.Error -> {
                        RetryItem(
                            modifier = Modifier.fillMaxSize(),
                            onRetryClick = { retry() }
                        )
                    }

                    loadState.append is LoadState.Error -> {
                        RetryItem(
                            modifier = Modifier.fillMaxSize(),
                            onRetryClick = { retry() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Image(image: Image) {
    ImageItem(
        modifier = Modifier,
        data = image.downloadUrl
    )
}
