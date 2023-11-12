package com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UploadPhotoViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private var drawable: Drawable? = null

    fun setImageContent(drawable: Drawable) {
        this.drawable = drawable
    }

    fun getImageContent(): Drawable? {
        return drawable
    }
}