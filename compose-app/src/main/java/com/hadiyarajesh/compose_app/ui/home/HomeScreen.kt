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

//import androidx.compose.material3.rememberScaffoldState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hadiyarajesh.compose_app.R
import com.hadiyarajesh.compose_app.database.entity.Image
import com.hadiyarajesh.compose_app.navigation.Screens
import com.hadiyarajesh.compose_app.ui.component.LoadingProgressBar
import com.hadiyarajesh.compose_app.ui.component.RetryItem
import com.hadiyarajesh.compose_app.ui.component.SubComposeImageItem
import com.hadiyarajesh.compose_app.utility.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by remember { homeViewModel.isLoading }.collectAsState()
    val images by remember { homeViewModel.images }.collectAsState()
    var showToast by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(showToast) {
        if (showToast) {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.swipe_down_to_refresh)
            )
//            Toast.makeText(
//                context,
//                context.getString(R.string.swipe_down_to_refresh),
//                Toast.LENGTH_SHORT
//            ).show()

            showToast = !showToast
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (images) {
                is UiState.Loading -> {
                    LoadingProgressBar(modifier = Modifier.fillMaxSize())
                }

                is UiState.Success -> {
                    (images as UiState.Success).data.let {
                        SwipeRefresh(
                            state = rememberSwipeRefreshState(isRefreshing = isLoading),
                            onRefresh = { homeViewModel.refreshImages() }
                        ) {
                            AllImageView(
                                images = it,
                                onClick = { image ->
                                    navController.navigate(Screens.ImageDetails.withArgs(image.id))
                                }
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    LaunchedEffect(images) {
                        snackbarHostState.showSnackbar(context.getString(R.string.something_went_wrong))
                    }

                    RetryItem(
                        modifier = Modifier.fillMaxSize(),
                        onRetryClick = { homeViewModel.refreshImages() }
                    )
                }

                is UiState.Empty -> {}
            }
        }
    }
}

@Composable
private fun AllImageView(
    modifier: Modifier = Modifier,
    images: List<Image>,
    onClick: (Image) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier.padding(4.dp),
        columns = GridCells.Adaptive(150.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            items = images,
            key = { it.id }
        ) { image ->
            ImageView(
                modifier = Modifier
                    .size(150.dp)
                    .clickable { onClick(image) },
                image = image
            )
        }
    }
}

@Composable
private fun ImageView(
    modifier: Modifier = Modifier,
    image: Image
) {
    SubComposeImageItem(
        modifier = modifier.clip(RoundedCornerShape(4.dp)),
        data = image.downloadUrl,
        contentScale = ContentScale.FillBounds,
        transformation = RoundedCornersTransformation(4f)
    )
}
