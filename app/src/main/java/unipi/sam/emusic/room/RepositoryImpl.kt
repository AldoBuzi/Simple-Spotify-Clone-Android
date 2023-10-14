package unipi.sam.emusic.room

import javax.inject.Inject

class TrackRepositoryImpl @Inject constructor(private val trackDao : TrackDao)
class PlaylistRepositoryImpl @Inject constructor(private val playlistDao: PlaylistDao)