package unipi.sam.emusic.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import unipi.sam.emusic.models.PlaylistModel
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.models.User

@Dao
interface TrackDao {
    @Transaction
    @Query("SELECT * FROM track WHERE id = (:id)")
    suspend fun getById(id: String) : TrackModel

    @Transaction
    @Query("DELETE FROM track WHERE id = :id")
    suspend fun deleteById(id:String) : Int


    @Transaction
    @Query("DELETE FROM track WHERE id = :id and playlistID = :playlistId ")
    suspend fun deleteByPrimaryKey(id:String, playlistId: Int) : Int
    @Insert
    suspend fun save(vararg tracks: TrackModel)

    @Delete
    suspend fun delete(vararg tracks: TrackModel) : Int
    @Transaction
    @Query("UPDATE track SET localUrl = :localUrl WHERE id = :trackID")
    suspend fun updateAll(localUrl:  String?, trackID: String) : Int

}

@Dao
interface PlaylistDao{

    @Query("SELECT * FROM playlist INNER JOIN track ON playlistID = playlist.id WHERE playlist.id = :id")
    suspend fun getAllTracks(id: String) : PlaylistModel

    @Insert
    suspend fun save(playlist: PlaylistModel): Long
    @Transaction
    @Query("SELECT * FROM playlist")
    suspend fun getAll(): List<PlaylistModel>

    @Transaction
    @Query("SELECT * FROM playlist WHERE id = :id")
    suspend fun getPlaylistById(id: String) : List<PlaylistModel>
    @Transaction
    @Query ("SELECT * from playlist")
    suspend fun getEverything() : List<PlaylistAndTrack>


    @Transaction
    @Query ("SELECT * from playlist WHERE id <= 0")
    suspend fun getLocalPlaylist() : List<PlaylistAndTrack>

    @Transaction
    @Query("SELECT * FROM playlist WHERE databaseId = 1")
    suspend fun getDefault() : PlaylistAndTrack

    @Query("SELECT * FROM playlist WHERE playlist.databaseId = :id ")
    suspend fun getTracksById(id:Int) : PlaylistAndTrack

    @Delete
    suspend fun delete(playlist: PlaylistModel) : Int

    @Query ("DELETE FROM playlist")
    suspend fun deleteAll() : Int

    @Query ("DELETE from playlist WHERE id = :id")
    suspend fun deleteById(id: Int) : Int

    @Query ("DELETE from playlist WHERE databaseId = :id")
    suspend fun deleteByDatabaseId(id: Int) : Int


    @Transaction
    @Insert
    suspend fun init(playlist: PlaylistModel = PlaylistModel(
        id = -1,
        title = "La tua libreria",
        tracklist = null,
        type = "Playlist",
        isFromDatabase = true,
        user = User()
    )
    ) : Long
}

data class PlaylistAndTrack(
    @Embedded
    val playlist: PlaylistModel,
    @Relation(
        parentColumn = "databaseId",
        entityColumn = "playlistID"
    )
    val tracks: List<TrackModel>
) : RoomComponent