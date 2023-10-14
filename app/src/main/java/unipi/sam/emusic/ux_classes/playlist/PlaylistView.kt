package unipi.sam.emusic.ux_classes.playlist

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import unipi.sam.emusic.R
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.PlaylistModel
import unipi.sam.emusic.utils.ImageUtils
import unipi.sam.emusic.ux_classes.ViewContentLayout

class PlaylistView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null): LinearLayout(context, attrs), ViewContentLayout {


    private var localItem: PlaylistModel? = null

    private lateinit var imageContent : ImageView
    private lateinit var mainTitleText : TextView
    private lateinit var subTitle: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        imageContent = findViewById(R.id.playlist_view_contentImage)
        mainTitleText = findViewById(R.id.playlist_view_titleText)
        subTitle = findViewById(R.id.playlist_view_playlistText)
    }

    override fun showLoadedContent(item: ModelViewType) {
        if (item !is PlaylistModel) return
        localItem = item
        if (item.picture != null)
            ImageUtils.loadImageFromUrl(item.picture, imageContent, imageContent)
        subTitle.text = "${item.type} • ${item.user?.name}"
        mainTitleText.text = item.title
    }

    override fun setListener(activity: MainActivity) {
        if(localItem == null) return
        activity.presentCustomPlaylist(localItem as PlaylistModel)
    }
}