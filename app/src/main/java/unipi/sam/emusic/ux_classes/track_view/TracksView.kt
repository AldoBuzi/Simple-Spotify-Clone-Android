package unipi.sam.emusic.ux_classes.track_view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import unipi.sam.emusic.R
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.ux_classes.BaseContainerLayout
import unipi.sam.emusic.ux_classes.ContainerLayout

class TracksView: BaseContainerLayout, ContainerLayout {

    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context,attributeSet)

    lateinit var adapter: TrackViewAdapter

    lateinit var title : TextView
    override fun onFinishInflate() {
        super.onFinishInflate()
        title = findViewById(R.id.tracks_view_titleText)
    }

    override fun setModels(value: Collection<ModelViewType>) {
        adapter = TrackViewAdapter(value,activity!!)
        recyclerView.adapter = adapter
        title.text = widget?.heading
    }



}