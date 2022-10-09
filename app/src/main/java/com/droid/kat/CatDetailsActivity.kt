package com.droid.kat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.load
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoilApi::class)
@AndroidEntryPoint
class CatDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var imageSaver: ImageSaver

    companion object {
        const val ARG_CAT_DATA = "ARG_CAT_DATA"

        fun launch(context: Context, catData: CatData) {
            val intent = Intent(context, CatDetailsActivity::class.java)
            intent.putExtra(ARG_CAT_DATA, catData)
            context.startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cat_details)

        val catData = intent.getParcelableExtra<CatData>(ARG_CAT_DATA)
        catData?.let { it ->

            findViewById<ImageView>(R.id.ivCatImage).load(it.url)

            findViewById<TextView>(R.id.tvCatName).text =
                "${it.getBreedName()} (${it.breeds?.firstOrNull()?.origin})"

            findViewById<TextView>(R.id.tvDescription).text =
                it.breeds?.firstOrNull()?.description

            findViewById<RatingBar>(R.id.ratingBar).rating =
                it.breeds?.firstOrNull()?.childFriendlyRating?.toFloat() ?: 0F

            findViewById<ImageView>(R.id.ivDownloadImage).setOnClickListener { view ->
                saveImage(it)
            }
        }
    }

    private fun saveImage(catData: CatData) {
        catData.url?.let {
            imageLoader.diskCache?.get(catData.url)?.use { snapshot ->
                lifecycleScope.launch {
                    val imageFile = snapshot.data.toFile()

                    imageSaver.saveImage("${catData.getBreedName()}.${catData.id}", imageFile) {
                        snapshot.close()
                        openGallery()
                    }
                }
            }
        }
    }

    private fun openGallery() {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), "Saved Image Successfully", Snackbar.LENGTH_LONG)
            .setAction("Open") {
                val galleryIntent = Intent(Intent.ACTION_VIEW)
                galleryIntent.type = "image/*"
                startActivity(galleryIntent)
            }


        snackBar.setActionTextColor(Color.YELLOW)
        snackBar.show()
    }
}