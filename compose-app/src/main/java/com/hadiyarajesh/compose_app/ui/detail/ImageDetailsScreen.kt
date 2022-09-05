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

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
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
import com.hadiyarajesh.compose_app.utility.UiState

@androidx.compose.runtime.Composable
fun ImageDetailsScreen(
    navController: NavController,
    imageDetailsViewModel: ImageDetailsViewModel,
    imageId: Long?
) {
    imageId?.let { id ->
        val context = LocalContext.current
        val image by remember { imageDetailsViewModel.image }.collectAsState()

        LaunchedEffect(Unit) {
            imageDetailsViewModel.getImage(imageId = id)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.image_details)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(4.dp)
            ) {
                when (image) {
                    is UiState.Loading -> {
                        LoadingProgressBar(modifier = Modifier.fillMaxSize())
                    }

                    is UiState.Success -> {
                        (image as UiState.Success).data?.let {
                            ImageContent(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.5f),
                                image = it
                            )
                        }
                    }

                    is UiState.Error -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is UiState.Empty -> {}
                }
            }
        }
    }
}

@Composable
private fun ImageContent(
    modifier: Modifier = Modifier,
    image: Image
) {
    val uriHandler = LocalUriHandler.current

    Column {
        SubComposeImageItem(
            modifier = modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp)),
            data = image.downloadUrl,
            contentScale = ContentScale.FillBounds,
            transformation = RoundedCornersTransformation(8f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.author),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = image.author,
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.url),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            modifier = Modifier.clickable { uriHandler.openUri(image.url) },
            text = image.url,
            style = MaterialTheme.typography.subtitle1,
            color = Color.Blue,
            textDecoration = TextDecoration.Underline
        )
    }

}
