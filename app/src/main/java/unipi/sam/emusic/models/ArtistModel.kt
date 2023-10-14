package unipi.sam.emusic.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import unipi.sam.emusic.models.ModelViewType

@Parcelize
data class ArtistModel(val id:Int ? = null,
                       val name: String? = null,
                       @SerializedName ("picture_xl") val picture : String? = null,
                       val tracklist: String? = null
) : Parcelable,ModelViewType {
    companion object{
        fun getRoom (item : ArtistModel) : PlaylistModel{
            return PlaylistModel(
                id = item.id,
                title = item.name,
                picture = item.picture,
                type = "Artist",
                isFromDatabase = true,
                user = User()
            )
        }

        fun parseRoom (item: PlaylistModel) : ArtistModel{
            return ArtistModel(
                id = item.id,
                name = item.title,
                picture = item.picture
            )
        }
    }
}