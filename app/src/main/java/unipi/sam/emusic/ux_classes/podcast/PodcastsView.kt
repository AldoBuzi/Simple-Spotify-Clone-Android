package unipi.sam.emusic.ux_classes.podcast

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import unipi.sam.emusic.R
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.ux_classes.BaseContainerLayout
import unipi.sam.emusic.ux_classes.ContainerLayout
import unipi.sam.emusic.ux_classes.track_view.TrackViewAdapter

class PodcastsView(context: Context, attrs: AttributeSet? = null) : BaseContainerLayout(context, attrs), ContainerLayout {



    lateinit var adapter: PodcastAdapter


    lateinit var title : TextView
    override fun onFinishInflate() {
        super.onFinishInflate()
        title = findViewById(R.id.playlist_view_titleText)
    }
    override fun setModels(value: Collection<ModelViewType>) {
        adapter = PodcastAdapter(value,activity!!)
        recyclerView.adapter = adapter
        title.text = widget?.heading
    }
}