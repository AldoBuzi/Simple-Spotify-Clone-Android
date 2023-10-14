package unipi.sam.emusic.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import unipi.sam.emusic.room.RoomComponent

@Parcelize
@Entity(tableName = "track", foreignKeys = [ForeignKey(
    entity = PlaylistModel::class,
    parentColumns = arrayOf("databaseId"),
    childColumns = arrayOf("playlistID"),
    onDelete = ForeignKey.CASCADE
)],
    primaryKeys = ["id","playlistID"]
)
class TrackModel( val id: Int,
                 @ColumnInfo var readable: Boolean? = null,
                 @ColumnInfo var title: String? = null,
                  @ColumnInfo var titleShort: String? = null,
                  @ColumnInfo var duration : Int? = null,
                  @ColumnInfo var releaseDate: String? = null,
                  @ColumnInfo var rank: Int? = null,
                  @ColumnInfo var explicitLyrics : Boolean? = null,
                  @ColumnInfo var localUrl: String? = null,
                  @ColumnInfo @SerializedName("preview") var url: String? = null,
                  @ColumnInfo var type: String? = null,
                  @ColumnInfo var album: Album? = null,
                  @ColumnInfo var artist: Artist? = null,
                  @SerializedName("md5_image") val image: String? = null,
                  @ColumnInfo(index = true) var playlistID: Int = 0
) : ModelViewType, Parcelable, RoomComponent, Cloneable {

    public override fun clone(): Any {
        return super.clone()
    }
}
@Parcelize
data class Album(val id: Int? = null, @SerializedName("cover_xl") val coverImage: String? = null, var type: String? = null) : Parcelable

@Parcelize
data class Artist(val id:Int? = null,val name: String? = null, @SerializedName("picture_big") val picture: String? = null, val type: String? = null) : Parcelable