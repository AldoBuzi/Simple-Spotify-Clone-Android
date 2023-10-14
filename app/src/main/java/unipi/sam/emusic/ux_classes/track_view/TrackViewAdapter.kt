package unipi.sam.emusic.ux_classes.track_view

import android.view.LayoutInflater
import android.view.ViewGroup
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseAdapter
import unipi.sam.emusic.base_components.BaseViewHolder
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.TrackModel

class TrackViewAdapter(override val items: Collection<ModelViewType>,override val activity: MainActivity) : BaseAdapter<TrackView>(items, activity) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<TrackView> {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false))
    }
    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: BaseViewHolder<TrackView>, position: Int) {
        super.onBindViewHolder(holder, position)
        if(items.elementAt(0) is TrackModel) holder.maintainer.localItem = Pair(items as MutableList<TrackModel>,position)
    }

}