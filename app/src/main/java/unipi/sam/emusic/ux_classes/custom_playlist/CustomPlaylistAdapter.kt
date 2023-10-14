package unipi.sam.emusic.ux_classes.custom_playlist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseAdapter
import unipi.sam.emusic.base_components.BaseViewHolder
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.PlaylistModel

class CustomPlaylistAdapter(items: Collection<ModelViewType>, activity: MainActivity) : BaseAdapter<CustomPlaylistViews>(items, activity) {


    override fun getItemViewType(position: Int): Int {
        return if((items.elementAt(position) as PlaylistModel).type == "Artist")
            CustomPlaylistTypes.ARTIST.ordinal
        else CustomPlaylistTypes.TRACK_LIST.ordinal
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<CustomPlaylistViews> {
        return when(viewType){
            CustomPlaylistTypes.TRACK_LIST.ordinal -> BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_playlist_view,parent,false))
            CustomPlaylistTypes.ARTIST.ordinal -> BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_artist_view,parent,false))
            else -> BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_playlist_view,parent,false))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<CustomPlaylistViews>, position: Int) {
        holder.maintainer.activity = activity
        super.onBindViewHolder(holder, position)
    }
}