package com.example.prashantadvaitdemo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import com.unsplash.pickerandroid.photopicker.presentation.UnsplashPickerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                val photosUrlState by viewModel.splashImageUrls.collectAsState()

                val imageUrls = remember(photosUrlState) { photosUrlState.toMutableList() }
                val imageBitmaps = remember { mutableStateListOf<Bitmap>() }
                val isLoading = remember { mutableStateOf(true) }
                val loadError = remember { mutableStateOf<String?>(null) }
                val snackbarHostState = SnackbarHostState()
                val coroutineScope = rememberCoroutineScope()

                if (imageUrls.isEmpty()) {
                    Text(
                        text = "No images selected",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    imageUrls.forEachIndexed { index, url ->
                        ImageLoader.loadImage(url,
                                              onSuccess = { bitmap ->
                                                  imageBitmaps.add(bitmap)
                                                  if (index == imageUrls.lastIndex) {
                                                      isLoading.value = false
                                                  }
                                                  loadError.value = null
                                              },
                                              onFailure = { error ->
                                                  loadError.value = error
                                                  isLoading.value = false
                                              }
                        )
                    }

                    if (isLoading.value) {
                        // Display loading indicator
                        CircularProgressIndicator()
                    } else if (loadError.value != null) {
                        // Display error message
                        showErrorMessage(coroutineScope, snackbarHostState, loadError.value?:"Unknown error")
                    }
                    else {
                        //Load image grid
                        LazyVerticalImageGrid(imageBitmaps)
                    }
                }
                FloatingActionButton(
                    onClick = { pickPhotos() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Text("Photos")
                }


                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { data ->
                        Snackbar(
                            snackbarData = data,
                              modifier = Modifier.padding(16.dp)
                        )
                    }
                )
            }

        }
    }

    private fun showErrorMessage(coroutineScope: CoroutineScope, snackbarHostState: SnackbarHostState, message: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    private fun pickPhotos() {
        startActivityForResult(
            UnsplashPickerActivity.getStartingIntent(
                this, // context
                isMultipleSelection = true, // enable multiple selection
            ), UNSPLASH_REQUEST_CODE
        )
    }

    @Composable
    fun LazyVerticalImageGrid(imageBitmaps: List<Bitmap>) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 16.dp,
                end = 12.dp,
                bottom = 16.dp
            ),
            content = {
                items(imageBitmaps.size) { index ->
                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                    ) {
                        Image(bitmap = imageBitmaps[index].asImageBitmap(), contentDescription = "Image $index")
                    }
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UNSPLASH_REQUEST_CODE) {
            val unsplashPhotos: ArrayList<UnsplashPhoto>? = data?.getParcelableArrayListExtra(UnsplashPickerActivity.EXTRA_PHOTOS)
            val splashImageUrls = unsplashPhotos?.map { it.urls.regular } ?: emptyList()
            viewModel.updateSplashImageUrls(splashImageUrls.filterNotNull())
        }
    }

    companion object {
        private const val UNSPLASH_REQUEST_CODE = 3000
    }
}
