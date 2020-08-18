package com.example.testplayersample

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.ImageView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.*

fun creatBitmapFromUrl(videoUrl: String): Flow<Bitmap> = flow {
    emit(ThumbnailUtils.createVideoThumbnail(videoUrl, MediaStore.Video.Thumbnails.MICRO_KIND))
}

fun setBitmapToView(view: ImageView, videoUrl: String) = runBlocking<Unit> {
    launch {
        println("Set bitmap")
    }
    if (view != null && !TextUtils.isEmpty(videoUrl)) creatBitmapFromUrl(videoUrl).take(1).collect { value -> view.setImageBitmap(value) }
}