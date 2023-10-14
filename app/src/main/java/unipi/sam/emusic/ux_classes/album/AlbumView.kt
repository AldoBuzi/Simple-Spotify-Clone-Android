package unipi.sam.emusic.ux_classes.album

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.models.AlbumModel
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.utils.ImageUtils
import unipi.sam.emusic.ux_classes.ViewContentLayout

class AlbumView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs),
    ViewContentLayout {

    var localItem : ModelViewType? = null
    private lateinit var contentImage : ImageView
    private lateinit var titleText: TextView
    private lateinit var subTitleText : TextView
    override fun onFinishInflate() {
        super.onFinishInflate()
        contentImage = findViewById(R.id.album_view_contentImage)
        titleText = findViewById(R.id.album_view_titleText)
        subTitleText = findViewById(R.id.album_view_AlbumText)
    }

    override fun showLoadedContent(item: ModelViewType) {
        if(item !is AlbumModel) return
        localItem = item
        titleText.text = item.title
        subTitleText.text = item.artist?.name?: "Album"
        if(item.cover != null)
            ImageUtils.loadImageFromUrl(item.cover,contentImage,contentImage)
    }

    override fun setListener(activity: MainActivity) {
        if(localItem != null) {
            ViewCompat.setTransitionName(contentImage, "${(localItem as AlbumModel).cover}")
            activity
                .withImageAnimation(contentImage)
                .presentAlbumDetailPage(localItem!!)
        }
    }
}