package unipi.sam.emusic.ux_classes

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import unipi.sam.emusic.ux_classes.track_view.TracksView

@Suppress("UNCHECKED_CAST")
open class BaseContainerViewHolder<T : ContainerLayout> internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), GenericViewHolder {
    internal var card: T = itemView as T
}