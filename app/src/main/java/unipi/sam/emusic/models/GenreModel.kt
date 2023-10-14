package unipi.sam.emusic.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenreModel (val id:Int? = null,
                       val name: String? = null,
                       @SerializedName("picture_small") val picture: String? = null,
                       val type: String? = null
) : Parcelable, ModelViewType