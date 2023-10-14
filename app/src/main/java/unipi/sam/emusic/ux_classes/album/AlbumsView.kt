package unipi.sam.emusic.ux_classes.album

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import unipi.sam.emusic.R
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.ux_classes.BaseContainerLayout
import unipi.sam.emusic.ux_classes.ContainerLayout

class AlbumsView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : BaseContainerLayout(context, attrs), ContainerLayout {

    lateinit var adapter: AlbumAdapter


    lateinit var title: TextView
    override fun onFinishInflate() {
        super.onFinishInflate()
        title = findViewById(R.id.album_view_titleText)
    }
    override fun setModels(value: Collection<ModelViewType>) {
        adapter = AlbumAdapter(value,activity!!)
        recyclerView.adapter = adapter
        title.text = widget?.heading
    }

}