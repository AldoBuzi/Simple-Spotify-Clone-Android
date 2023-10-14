package unipi.sam.emusic.room

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import unipi.sam.emusic.models.PlaylistModel
import unipi.sam.emusic.models.TrackModel
import javax.inject.Inject
import javax.inject.Singleton

@Database(entities = [TrackModel::class, PlaylistModel::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
}

