package unipi.sam.emusic.ux_classes.track_list

import android.view.LayoutInflater
import android.view.View.NO_ID
import android.view.ViewGroup
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseAdapter
import unipi.sam.emusic.base_components.BaseViewHolder
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.TrackModel

class TrackListAdapter(items: Collection<ModelViewType>, activity: MainActivity) : BaseAdapter<TrackListView>(items, activity) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<TrackListView> {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.track_list_view,parent,false))
    }
    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: BaseViewHolder<TrackListView>, position: Int) {
        if(items.elementAt(0) is TrackModel) {
            holder.maintainer.activity = activity
            holder.maintainer.localItem = Pair(items as MutableList<TrackModel>,position)
        }
        super.onBindViewHolder(holder, position)
        if((items.elementAt(position) as TrackModel).id == NO_ID) holder.maintainer.hideUI()
    }
}