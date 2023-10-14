package unipi.sam.emusic.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import unipi.sam.emusic.models.Album
import unipi.sam.emusic.models.Artist
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.User

class Converters {

    @TypeConverter
    fun artistToJson(type: Artist): String {
       return Gson().toJson(type)
    }
    @TypeConverter
    fun artistFromJson(json: String) : Artist{
        return  Gson().fromJson(json, Artist::class.java)
    }

    @TypeConverter
    fun albumToJson(type: Album?): String? {
        return if(type == null) null else Gson().toJson(type)
    }
    @TypeConverter
    fun albumFromJson(json: String?) : Album?{
        return  if(json == null) null else Gson().fromJson(json, Album::class.java)
    }

    @TypeConverter
    fun userToJson(type: User): String {
        return Gson().toJson(type)
    }
    @TypeConverter
    fun userFromJson(json: String) : User{
        return  Gson().fromJson(json, User::class.java)
    }
}