package unipi.sam.emusic.ux_classes.playlist

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import unipi.sam.emusic.R
import unipi.sam.emusic.models.ArtistModel
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.PlaylistModel
import unipi.sam.emusic.models.User
import unipi.sam.emusic.ux_classes.BaseContainerLayout
import unipi.sam.emusic.ux_classes.ContainerLayout

class PlaylistsView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : BaseContainerLayout(context, attrs), ContainerLayout {


    lateinit var adapter: PlaylistAdapter


    lateinit var title: TextView
    override fun onFinishInflate() {
        super.onFinishInflate()
        title = findViewById(R.id.playlist_view_titleText)
    }
    override fun setModels(value: Collection<ModelViewType>) {
        adapter = PlaylistAdapter(value,activity!!)
        recyclerView.adapter = adapter
        title.text = widget?.heading
    }


}