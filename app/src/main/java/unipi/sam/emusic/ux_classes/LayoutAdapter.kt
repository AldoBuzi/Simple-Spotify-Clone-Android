package unipi.sam.emusic.ux_classes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseViewHolder
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.ux_classes.album.AlbumViewHolder
import unipi.sam.emusic.ux_classes.artist.ArtistViewHolder
import unipi.sam.emusic.ux_classes.custom_playlist.CustomPlaylistViewHolder
import unipi.sam.emusic.ux_classes.genre.GenreViewHolder
import unipi.sam.emusic.ux_classes.playlist.PlaylistViewHolder
import unipi.sam.emusic.ux_classes.podcast.PodcastViewHolder
import unipi.sam.emusic.ux_classes.track_list.TrackListViewHolder
import unipi.sam.emusic.ux_classes.track_view.TrackViewHolder

class LayoutAdapter(val collection: Collection<Widget>,val activity: MainActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _collection: Collection<Widget> = collection
    override fun getItemViewType(position: Int): Int {
        return _collection.elementAt(position).viewType!!.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            LayoutType.TRACKS.value -> TrackViewHolder(inflater.inflate(R.layout.tracks_view,parent,false))
            LayoutType.ALBUMS.value -> AlbumViewHolder(inflater.inflate(R.layout.albums_view,parent,false))
            LayoutType.ARTIST.value -> ArtistViewHolder(inflater.inflate(R.layout.artists_view,parent,false))
            LayoutType.PLAYLIST.value ->  PlaylistViewHolder(inflater.inflate(R.layout.playlists_view,parent,false))
            LayoutType.PODCAST.value -> PodcastViewHolder(inflater.inflate(R.layout.podcasts_view,parent,false))
            LayoutType.TRACKS_LIST.value -> TrackListViewHolder(inflater.inflate(R.layout.track_lists_view,parent,false))
            LayoutType.GENRES.value -> GenreViewHolder(inflater.inflate(R.layout.genres_view,parent,false))
            LayoutType.CUSTOM_PLAYLIST.value -> CustomPlaylistViewHolder(inflater.inflate(R.layout.custom_playlists_view,parent,false))
            else -> TrackViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.tracks_view,parent,false))
        }
    }

    override fun getItemCount(): Int {
        return _collection.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is BaseContainerViewHolder<*>) {
            holder.card.activity = activity
            holder.card.widget = _collection.elementAt(position)
        }
    }


    fun setNewItems(collection: Collection<Widget>){
        _collection = collection
    }
}