package unipi.sam.emusic.ux_classes.track_list

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputCustom
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.api.API
import unipi.sam.emusic.components.player.PlayerViewModel
import unipi.sam.emusic.custom.PlaylistBottomSheet
import unipi.sam.emusic.custom.TrackOptionBottomSheet
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.PlaylistModel
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.utils.ImageUtils
import unipi.sam.emusic.utils.toPx
import unipi.sam.emusic.ux_classes.ViewContentLayout

class TrackListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), ViewContentLayout {


    var activity: MainActivity? = null
    var localItem: Pair<MutableList<TrackModel>,Int>? = null

    var job: Job? = null
    var firstTime = true
    private lateinit var contentImage : ImageView
    private lateinit var title : TextView
    private lateinit var artist : TextView
    private lateinit var animationView: LottieAnimationView
    private lateinit var optionBtn: ImageButton
    private lateinit var downloadedImage: ImageView
    override fun onFinishInflate() {
        super.onFinishInflate()
        contentImage = findViewById(R.id.trackList_contentImage)
        title = findViewById(R.id.trackList_titleText)
        artist = findViewById(R.id.trackList_artistText)
        animationView = findViewById(R.id.animationView)
        firstTime = true
        optionBtn = findViewById(R.id.playerOptionBtn)
        downloadedImage = findViewById(R.id.trackList_downloadedImage)
    }

    @SuppressLint("SetTextI18n")
    override fun showLoadedContent(item: ModelViewType) {
        resetUI()
        if (item !is TrackModel) return
        downloadedImage.visibility = GONE
        if(item.localUrl != null) downloadedImage.visibility = VISIBLE
        if(item.album?.coverImage != null)
            ImageUtils.loadImageFromUrl(item.album!!.coverImage!!,contentImage,contentImage)
        else if(item.image != null)
            ImageUtils.loadImageFromUrl(API.buildPathFromMd5(item.image),contentImage,contentImage)
        artist.text = "${item.type} • ${item.artist?.name}"
        title.text = item.title
        optionBtn.setOnClickListener {
            showOptionSheet(item)
        }
        if(activity != null)
            activity!!.lifecycleScope.launch {
                job = this.coroutineContext.job
                PlayerViewModel.currentPlaying.collectLatest {
                    if ( it != localItem?.first?.elementAt(localItem!!.second )?.id) {
                        animationView.visibility = GONE
                        title.setTextColor(activity!!.resources.getColor(R.color.white))
                        return@collectLatest
                    }
                    animationView.visibility = VISIBLE
                    title.setTextColor(activity!!.resources.getColor(R.color.music_playing))
                }
            }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job?.cancel()
    }
    override fun setListener(activity: MainActivity) {
        if(localItem != null && localItem?.first?.elementAt(localItem!!.second)?.id != NO_ID)
            activity.presentPlayer(localItem!!.first,localItem!!.second)
    }

    private fun showOptionSheet(item: TrackModel){
        val trackSheet = TrackOptionBottomSheet.newInstance(activity!!,item)
        trackSheet.show(activity!!.supportFragmentManager,"Track bottom sheet")
    }

    fun hideUI(){
        this.visibility = INVISIBLE
    }

    private fun resetUI(){
       this.visibility = VISIBLE
    }
}