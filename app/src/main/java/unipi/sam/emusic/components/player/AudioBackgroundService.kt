package unipi.sam.emusic.components.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import unipi.sam.emusic.R
import unipi.sam.emusic.commons.Application


const val channelID = "EMUSIC_CHANNEL_ID"
const val notificationID = 1
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class AudioBackgroundService : MediaLibraryService() {

    private val TAG = "AudioBackgroundService"


    lateinit var player: Player

    /* This is the session which will delegate everything you need about audio playback such as notifications, pausing player, resuming player, listening to states, etc */
    lateinit var session: MediaLibraryService.MediaLibrarySession



    override fun  onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(applicationContext)
            .setRenderersFactory(
                DefaultRenderersFactory(this).setExtensionRendererMode(
                    DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER /* We prefer extensions, such as FFmpeg */
                )
            )
            .build()
        session = MediaLibrarySession.Builder(this, player,
            object: MediaLibrarySession.Callback {
                override fun onAddMediaItems(
                    mediaSession: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    mediaItems: MutableList<MediaItem>
                ): ListenableFuture<MutableList<MediaItem>> {

                    /* This is the trickiest part, if you don't do this here, nothing will play */
                    val updatedMediaItems = mediaItems.map { it.buildUpon().setUri(it.mediaId).build() }.toMutableList()
                    return Futures.immediateFuture(updatedMediaItems)
                }
            })
            .build()
        createNotificationChannel()
        setMediaNotificationProvider(NotificationProvider().apply { this.player = this@AudioBackgroundService.player })


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStartCommand $startId")
        return super.onStartCommand(intent, flags, startId)
    }
    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel.
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
    override fun onDestroy() {
        session.release()
        player.stop()
        player.release()
        super.onDestroy()

    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return session
    }
    class NotificationProvider : MediaNotification.Provider, MediaNotification.Provider.Callback{
        val id = 1
        val CHANNEL_ID = "emusic_1_channel"
        var isPause = false
        var player: Player? = null
        override fun createNotification(
            mediaSession: MediaSession,
            customLayout: ImmutableList<CommandButton>,
            actionFactory: MediaNotification.ActionFactory,
            onNotificationChangedCallback: MediaNotification.Provider.Callback
        ): MediaNotification {
            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(Application.appContext!!, CHANNEL_ID)

            builder.setChannelId(CHANNEL_ID)
            builder.setSound(null)
            builder.setDefaults(0)
            builder.priority = NotificationCompat.PRIORITY_LOW
            builder.setSilent(true)
            //enable option to open app on notification click
            builder.setContentIntent(mediaSession.sessionActivity)
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            builder.setAutoCancel(true)
            builder.addAction(actionFactory.createMediaAction(mediaSession,IconCompat.createWithResource(Application.appContext!!, R.drawable.skip_previous),"Skip_previous",Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM))
            if(player?.isPlaying == true)
                builder.addAction(actionFactory.createMediaAction(mediaSession,IconCompat.createWithResource(Application.appContext!!, R.drawable.pause_small),"Play",Player.COMMAND_PLAY_PAUSE))
            else
                builder.addAction(actionFactory.createMediaAction(mediaSession,IconCompat.createWithResource(Application.appContext!!, R.drawable.play_small),"Play",Player.COMMAND_PLAY_PAUSE))

            builder.addAction(actionFactory.createMediaAction(mediaSession,IconCompat.createWithResource(Application.appContext!!, R.drawable.skip_next),"Skip_next",Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM))
            builder.setSmallIcon(R.drawable.music_placeholder)
            builder.setLargeIcon(BitmapFactory.decodeResource(Application.appContext!!.resources,R.drawable.music_placeholder))


            //this will automatically recover the metadata from the viewmodel
            builder.setStyle( androidx.media.app.NotificationCompat.MediaStyle())


            return MediaNotification(notificationID, builder.build())
        }

        override fun handleCustomCommand(
            session: MediaSession,
            action: String,
            extras: Bundle
        ): Boolean {
            return false
        }
        override fun onNotificationChanged(notification: MediaNotification) {
        }

    }
    val id = 1
    val CHANNEL_ID = "emusic_1_channel"
}