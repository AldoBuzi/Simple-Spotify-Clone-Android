package unipi.sam.emusic.ux_classes.artist

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.models.ArtistModel
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.utils.ImageUtils
import unipi.sam.emusic.ux_classes.ViewContentLayout

class ArtistView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), ViewContentLayout {


    private var localItem: ModelViewType? = null

    lateinit var mainImage : ImageView
    lateinit var mainTitleTextView: TextView
    override fun onFinishInflate() {
        super.onFinishInflate()
        mainImage = findViewById(R.id.artist_mainImage)
        mainTitleTextView = findViewById(R.id.artist_titleText)
    }

    override fun showLoadedContent(item: ModelViewType) {
        if (item !is ArtistModel) return
        localItem = item
        if(item.picture != null)
            ImageUtils.loadImageFromUrl(item.picture, mainImage,mainImage)
        mainTitleTextView.text = item.name
    }

    override fun setListener(activity: MainActivity) {
        ViewCompat.setTransitionName(mainImage, "${(localItem as ArtistModel).picture}")
        if(localItem != null)
            activity
                .withImageAnimation(mainImage)
                .presentArtistDetailPage(localItem!!)
    }
}