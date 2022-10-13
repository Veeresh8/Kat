package com.droid.kat

import android.content.Context
import android.graphics.BitmapFactory
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageSaver @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @ApplicationContext val appContext: Context,
) {

    suspend fun saveImage(name: String, file: File, callback: () -> Unit) {
        withContext(ioDispatcher) {
            val bitmap = BitmapFactory.decodeFile(file.path)
            MediaStore.Images.Media.insertImage(appContext.contentResolver, bitmap, name, "")

            withContext(mainDispatcher) {
                callback.invoke()
            }
        }
    }
}