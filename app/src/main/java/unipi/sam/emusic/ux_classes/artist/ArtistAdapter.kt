package unipi.sam.emusic.ux_classes.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseAdapter
import unipi.sam.emusic.base_components.BaseViewHolder
import unipi.sam.emusic.models.ModelViewType

class ArtistAdapter(override val items: Collection<ModelViewType>, override val activity: MainActivity) : BaseAdapter<ArtistView>(items,activity) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ArtistView> {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.artist_view,parent,false))
    }


}