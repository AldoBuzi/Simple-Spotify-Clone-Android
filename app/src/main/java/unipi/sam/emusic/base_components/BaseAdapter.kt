package unipi.sam.emusic.base_components

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.ux_classes.ViewContentLayout

abstract class BaseAdapter<T : ViewContentLayout>(open val items: Collection<ModelViewType>, open val activity: MainActivity) : RecyclerView.Adapter<BaseViewHolder<T>>() {

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T>
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        val item = items.elementAt(position)
        holder.maintainer.showLoadedContent(item = item)
        (holder.maintainer as View).setOnClickListener{
            holder.maintainer.setListener(activity)
        }

    }


}