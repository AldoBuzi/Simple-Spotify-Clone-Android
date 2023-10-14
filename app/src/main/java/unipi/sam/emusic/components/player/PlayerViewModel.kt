package unipi.sam.emusic.components.player

import android.content.ComponentName
import android.graphics.Bitmap
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.palette.graphics.Palette
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import unipi.sam.emusic.R
import unipi.sam.emusic.api.API
import unipi.sam.emusic.commons.Application
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.room.AppDatabase
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor (private val model: PlayerModel): ViewModel() {


    @Inject lateinit var database: AppDatabase
    init {
        isInitialized = true
    }

    //UI Related
    val id: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val coverImage: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val title : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val subTitle : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val trackUrl : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val artistName: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    //playback data
    val duration : MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val position: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val isPlaying : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isLoading : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private var list : MutableList<MediaItem> = mutableListOf()
    private val MAX_PROGRESS = 100
    private var isSeeking = false
    private var playFrom = 0
    private var trackListSize = -1

    //ui related
    private val isAnimationComplete : MutableLiveData<Boolean> = MutableLiveData(false)
    fun setupUI(){
        if (item == null) return
        if (item!!.size == 0) return

        if (item?.elementAt(playFrom)?.id != null) id.postValue(item!!.elementAt(playFrom).id)
        if (!item?.elementAt(playFrom)?.album?.coverImage.isNullOrEmpty()) coverImage.postValue(item!!.elementAt(playFrom).album!!.coverImage!!)
        else if(!item?.elementAt(playFrom)?.image.isNullOrEmpty()) coverImage.postValue("${API.imageBaseUrl}/${item!!.elementAt(playFrom).image}/${API.defaultDimension}")
        if (!item?.elementAt(playFrom)?.title.isNullOrEmpty()) title.postValue(item!!.elementAt(playFrom).title!!)
        if (!item?.elementAt(playFrom)?.titleShort.isNullOrEmpty()) subTitle.postValue(item!!.elementAt(playFrom).titleShort!!)
        if (!item?.elementAt(playFrom)?.url.isNullOrEmpty()) trackUrl.postValue(item!!.elementAt(playFrom).url!!)
        if(item?.elementAt(playFrom)?.artist != null) artistName.postValue(item!!.elementAt(playFrom).artist!!.name)
    }

    fun setUpMediaController(){
        if(item == null) return
        trackListSize = item!!.size

        //Creates a token for MediaController or MediaBrowser to connect to one of MediaSessionService, MediaLibraryService, or MediaBrowserServiceCompat.
        val sessionToken = SessionToken(Application.appContext!!, ComponentName(Application.appContext!!, AudioBackgroundService::class.java))


        /* Instantiating our MediaController and linking it to the service using the session token */
        val mediacontrollerFuture = MediaController.Builder(Application.appContext!!, sessionToken)
            .buildAsync()
        mediacontrollerFuture.addListener({
            player = mediacontrollerFuture.get()
            mainHandler.post(runnable)
            player!!.addListener(object: Player.Listener{
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    this@PlayerViewModel.isPlaying.value = isPlaying
                }

                override fun onIsLoadingChanged(isLoading: Boolean) {
                    super.onIsLoadingChanged(isLoading)
                    this@PlayerViewModel.isLoading.postValue(isLoading)
                }

                override fun onTracksChanged(tracks: Tracks) {
                    super.onTracksChanged(tracks)
                }
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    //skip to next/previous song
                    playFrom =  player!!.currentMediaItemIndex % list.size
                    currentPlaying.value = item!![playFrom].id!!
                    setupUI()
                    super.onMediaItemTransition(mediaItem, reason)
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                }

            })

            loadMediaItem()
        }, MoreExecutors.directExecutor())

    }
    suspend fun findTrackById(id:String){

    }

    fun seekTrackToTime(value: Int){
        player?.seekTo(value * ( duration.value!!.toLong() / MAX_PROGRESS ))
        isSeeking = true
    }

    private fun loadMediaItem() {
        /* We use setMediaId as a unique identifier for the media (which is needed for mediasession and we do NOT use setUri because we're gonna do
           something like setUri(mediaItem.mediaId) when we need to load the media like we did above in the MusicPlayerService and more precisely when we were building the session */
        if(item == null) return
        for(key in item!!){
            list.add(MediaItem.Builder()
                .setMediaId("${key.localUrl ?: key.url}") /* setMediaId and NOT setUri */
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle("${key.title}")
                        .setSubtitle("${key.id}")
                        .setArtist("${key.artist?.name}")
                        .setAlbumArtist("${key.artist?.name}")
                        .setArtworkUri(Uri.parse("${key.album?.coverImage}"))
                        .build()
                )
                .build())
        }

        /* Load it into our activity's MediaController */
        player?.setMediaItems(list)
        playFrom = seekIndex
        setupUI()
        player?.seekTo(seekIndex, C.INDEX_UNSET.toLong())
        player?.prepare()
        player?.play()
    }
    val mainHandler = Handler(Looper.getMainLooper())
    val runnable = object : Runnable {
        override fun run() {
            if (player!!.bufferedPosition != 0L && !player!!.isLoading && !isSeeking) {
                val current = player!!.currentPosition.toInt() * MAX_PROGRESS / player!!.bufferedPosition.toInt()
                if (MAX_PROGRESS > current) {
                    duration.postValue(player!!.bufferedPosition.toInt())
                    position.postValue(current)
                }
            }
            isSeeking = false
            mainHandler.postDelayed(this, 1000)
        }
    }

    suspend fun getLikeIconStatus(): Boolean{
        val query = database.playlistDao().getDefault()
        if(query.tracks.isNotEmpty()){
            if(query.tracks.firstOrNull { x -> x.id.toInt() == id.value } != null)
                return true
        }
        return false
    }
    suspend fun saveTrack() : Boolean?{
        val query = database.playlistDao().getDefault()
        if(query.tracks.find { x -> x.id == id.value } == null){
            database.trackDao().save(item?.get(playFrom)!!.apply { playlistID = 1})
            return true
        }
        if( database.trackDao().deleteById(deleteTrackRequested()) == 1)
            return false

        return null

    }
    private fun deleteTrackRequested() : String{
        return id.value.toString()
    }

    fun handlePlayButton(){
        if(isPlaying.value == true)
            player?.pause()
        else player?.play()
    }
    fun handleNextTrackButton(){
        if(player?.hasNextMediaItem() != true) return
        player?.seekToNextMediaItem()
    }
    fun handlePreviousTrackButton(){
        if(player?.hasPreviousMediaItem() != true) return
        player?.seekToPreviousMediaItem()
    }
    val shouldHide : MutableLiveData<Boolean>  =   MutableLiveData(true)
    fun handleAnimationComplete(progress: Float){
        isAnimationComplete.value = true
        isSmall.value = progress == 1f
        shouldHide.value = progress != 1f
    }

    fun handleAnimationStart(){
        isAnimationComplete.value = false
        if(isSmall.value != null) shouldHide.value = isSmall.value
    }

    fun handleColorChange(bitmap: Bitmap, callback: (value:Int)->Unit) {
         Palette.from(bitmap).generate(Palette.PaletteAsyncListener() {
             var value = -1
             if(it != null && it.darkMutedSwatch?.rgb != null)  value =  it.darkMutedSwatch!!.rgb
             else if(it != null && it.vibrantSwatch?.rgb != null) value = it.vibrantSwatch!!.rgb
             if(value == -1) value =  Application.appContext?.resources!!.getColor(R.color.primary)
             callback.invoke(value)
        })

    }
    fun onDestroy(){
        requestMiniPlayer.value = false
        isSmall.value = false
        releaseEverything()
    }
    private fun releaseEverything(){
        player?.stop()
        player?.release()
        player = null
        //remove everything
        mainHandler.removeCallbacksAndMessages(null)
    }
    fun isReloadRequired(){
        if(isReloadRequired.value == true){
            list = mutableListOf()
            releaseEverything()
            setUpMediaController()
        }
    }

    private var player: Player? = null

    companion object{
        //if true means that the play is in "small view mode"
        val isSmall : MutableLiveData<Boolean> = MutableLiveData(false)
        //this will be true after first initialization of fragment
        var isInitialized = false
        //track list to play
        var item : MutableList<TrackModel>? = null
        //tell which item in the list play first
        var seekIndex = 0
        val requestMiniPlayer : MutableLiveData<Boolean>  = MutableLiveData(false)
        //called when fragment instance already exist and we need to reload the data
        val isReloadRequired : MutableLiveData<Boolean> = MutableLiveData(false)
        //sometimes layout refresh can reset play small view mode
        val grabPosition : MutableLiveData<Boolean> = MutableLiveData(false)

        val currentPlaying : MutableStateFlow<Int>  = MutableStateFlow(-1)
    }
}