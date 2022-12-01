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

package com.hadiyarajesh.compose_app.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.transform.RoundedCornersTransformation
import com.hadiyarajesh.compose_app.R
import com.hadiyarajesh.compose_app.database.entity.Image
import com.hadiyarajesh.compose_app.ui.component.LoadingProgressBar
import com.hadiyarajesh.compose_app.ui.component.SubComposeImageItem
import com.hadiyarajesh.compose_app.utility.LoadResourceFrom
import com.hadiyarajesh.compose_app.utility.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailsScreen(
    navController: NavController,
    imageDetailsViewModel: ImageDetailsViewModel,
    imageId: Long?
) {
    imageId?.let { id ->
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val snackbarHostState = remember { SnackbarHostState() }
        val loadFrom by remember { imageDetailsViewModel.loadFrom }.collectAsState()
        val image by remember { imageDetailsViewModel.image }.collectAsState()

        LaunchedEffect(Unit) {
            // Initially, fetch data from local database
            imageDetailsViewModel.getImage(imageId = id, loadFrom = LoadResourceFrom.Db)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.image_details)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(4.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                when (image) {
                    is UiState.Loading -> {
                        LoadingProgressBar(modifier = Modifier.fillMaxSize())
                    }

                    is UiState.Success -> {
                        ImageDetailsContent(
                            modifier = Modifier.fillMaxSize(),
                            image = (image as UiState.Success).data,
                            loadFrom = loadFrom,
                            onLoadFromNetworkClick = { image ->
                                scope.launch {
                                    // If user explicitly ask, fetch data from network
                                    imageDetailsViewModel.getImage(
                                        imageId = image.id.toLong(),
                                        loadFrom = LoadResourceFrom.Network
                                    )
                                }
                            }
                        )
                    }

                    is UiState.Error -> {
                        LaunchedEffect(image) {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.swipe_down_to_refresh)
                            )
                        }
                    }

                    is UiState.Empty -> {}
                }
            }
        }
    }
}

@Composable
private fun ImageDetailsContent(
    modifier: Modifier = Modifier,
    image: Image,
    loadFrom: LoadResourceFrom,
    onLoadFromNetworkClick: (Image) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Column(modifier = modifier) {
        SubComposeImageItem(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp)),
            data = image.downloadUrl,
            contentScale = ContentScale.FillBounds,
            transformation = RoundedCornersTransformation(8f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.author),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = image.author,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.url),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            modifier = Modifier.clickable { uriHandler.openUri(image.url) },
            text = image.url,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Blue,
            textDecoration = TextDecoration.Underline
        )

        if (loadFrom == LoadResourceFrom.Db) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.data_fetched_from_local_db),
                    style = MaterialTheme.typography.titleMedium
                )

                Button(onClick = { onLoadFromNetworkClick(image) }) {
                    Text(text = stringResource(id = R.string.load_from_network))
                }
            }
        }
    }
}
