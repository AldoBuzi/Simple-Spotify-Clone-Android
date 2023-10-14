package unipi.sam.emusic.models

import android.os.Parcelable
import android.view.Display.Mode
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import unipi.sam.emusic.R
//import unipi.sam.emusic.room.Playlist
import unipi.sam.emusic.room.RoomComponent

@Parcelize
@Entity(tableName = "playlist")
data class PlaylistModel (@ColumnInfo val id : Int? = null,
                          @ColumnInfo val title: String? = null,
                          @ColumnInfo @SerializedName ("picture_xl") val picture : String? = null,
                          @ColumnInfo val tracklist : String? = null,
                          @ColumnInfo val type: String? = null,
                          @ColumnInfo val user : User,
                          @ColumnInfo var isFromDatabase : Boolean = false,
                          @PrimaryKey(autoGenerate = true) val databaseId : Int = 0,
): Parcelable, ModelViewType, RoomComponent {
}
@Parcelize
data class User(val id: Int? = null, val name: String? = null) : Parcelable