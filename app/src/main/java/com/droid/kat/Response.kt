package com.droid.kat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class CatData(
    val id: String,
    val url: String? = null,
    val breeds: List<BreedData>? = null,
): Parcelable {
    @Serializable
    @Parcelize
    data class BreedData(
        val name: String? = null,
        val temperament: String? = null,
        val description: String? = null,
        val origin: String? = null,
        @SerialName("wikipedia_url")
        val wikiUrl: String? = null,
        @SerialName("child_friendly")
        val childFriendlyRating: Int? = null,
    ): Parcelable

    fun getBreedName(): String {
        return breeds?.firstOrNull()?.name ?: "Unknown breed"
    }
}