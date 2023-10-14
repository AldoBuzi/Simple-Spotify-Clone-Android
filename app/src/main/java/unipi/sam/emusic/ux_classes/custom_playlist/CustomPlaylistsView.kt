package unipi.sam.emusic.ux_classes.custom_playlist

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import unipi.sam.emusic.models.ArtistModel
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.ux_classes.BaseContainerLayout
import unipi.sam.emusic.ux_classes.ContainerLayout
import unipi.sam.emusic.ux_classes.playlist.PlaylistAdapter

class CustomPlaylistsView(context: Context, attrs: AttributeSet? = null) : BaseContainerLayout(context, attrs), ContainerLayout {


    var adapter: CustomPlaylistAdapter? = null


    override fun onFinishInflate() {
        super.onFinishInflate()
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
    }
    override fun setModels(value: Collection<ModelViewType>) {
        adapter = CustomPlaylistAdapter(value,activity!!)
        recyclerView.adapter = adapter
    }
}