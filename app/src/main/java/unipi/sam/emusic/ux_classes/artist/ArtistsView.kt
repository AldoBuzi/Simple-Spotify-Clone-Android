package unipi.sam.emusic.ux_classes.artist

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import unipi.sam.emusic.R
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.ux_classes.BaseContainerLayout
import unipi.sam.emusic.ux_classes.ContainerLayout

class ArtistsView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : BaseContainerLayout(context, attrs), ContainerLayout {

    lateinit var adapter: ArtistAdapter


    lateinit var title: TextView
    override fun onFinishInflate() {
        super.onFinishInflate()
        title = findViewById(R.id.artists_view_titleText)
    }
    override fun setModels(value: Collection<ModelViewType>) {
        adapter = ArtistAdapter(value,activity!!)
        recyclerView.adapter = adapter
        title.text = widget?.heading
    }
}

