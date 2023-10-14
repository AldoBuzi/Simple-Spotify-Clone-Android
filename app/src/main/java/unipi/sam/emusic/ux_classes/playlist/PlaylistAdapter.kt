package unipi.sam.emusic.ux_classes.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseAdapter
import unipi.sam.emusic.base_components.BaseViewHolder
import unipi.sam.emusic.models.ModelViewType

class PlaylistAdapter(override val items: Collection<ModelViewType>, override val activity: MainActivity) : BaseAdapter<PlaylistView>(items, activity) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PlaylistView> {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.playlist_view,parent,false))
    }
}