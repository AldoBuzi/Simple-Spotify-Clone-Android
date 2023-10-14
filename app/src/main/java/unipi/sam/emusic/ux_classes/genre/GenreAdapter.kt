package unipi.sam.emusic.ux_classes.genre

import android.view.LayoutInflater
import android.view.ViewGroup
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseAdapter
import unipi.sam.emusic.base_components.BaseViewHolder
import unipi.sam.emusic.models.ModelViewType

class GenreAdapter(items: Collection<ModelViewType>, activity: MainActivity) : BaseAdapter<GenreView>(items, activity) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<GenreView> {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.genre_view,parent,false))
    }
}