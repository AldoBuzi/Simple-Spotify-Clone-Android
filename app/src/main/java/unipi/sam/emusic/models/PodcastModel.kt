package unipi.sam.emusic.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PodcastModel(
    val id: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val available : Boolean? = null,
    @SerializedName ("picture_xl") val picture: String? = null,
    val type: String? = null
) : Parcelable, ModelViewType {
}