package unipi.sam.emusic.base_components

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import unipi.sam.emusic.ux_classes.ViewContentLayout

@Suppress("UNCHECKED_CAST")
open class BaseViewHolder<T : ViewContentLayout>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val maintainer :  T = itemView as T
}