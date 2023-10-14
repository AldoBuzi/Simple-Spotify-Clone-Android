package unipi.sam.emusic.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Parcelize
data class AlbumModel(val id: Int ? = null, val title : String? = null, @SerializedName("cover_xl") val cover : String? = null, val tracklist: String? = null, val artist: ArtistModel?) : Parcelable, ModelViewType{



}