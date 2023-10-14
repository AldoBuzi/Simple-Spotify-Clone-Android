package unipi.sam.emusic.ux_classes.track_list

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import unipi.sam.emusic.R
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.ux_classes.BaseContainerLayout
import unipi.sam.emusic.ux_classes.ContainerLayout
import unipi.sam.emusic.ux_classes.track_view.TrackViewAdapter

class TrackListsView(context: Context, attrs: AttributeSet? = null) : BaseContainerLayout(context, attrs), ContainerLayout {


    lateinit var adapter: TrackListAdapter

    lateinit var title: TextView
    override fun onFinishInflate() {
        super.onFinishInflate()
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        title = findViewById(R.id.tracklist_view_titleText)
    }

    override fun setModels(value: Collection<ModelViewType>) {
        adapter = TrackListAdapter(value,activity!!)
        recyclerView.adapter = adapter
        title.text = widget?.heading
    }
}