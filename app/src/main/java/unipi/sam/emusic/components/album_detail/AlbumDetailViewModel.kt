package unipi.sam.emusic.components.album_detail

import android.graphics.Bitmap
import android.graphics.LinearGradient
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ScaleDrawable
import android.view.Gravity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseViewModel
import unipi.sam.emusic.commons.Application
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.models.AlbumModel
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
@HiltViewModel
class AlbumDetailViewModel @Inject constructor(private val model : AlbumDetailModel) : BaseViewModel() {



    var item : AlbumModel? = null
        set(value) {
            field = value
            setupUI()
        }

    var hasDataLoaded = false

    //ui data related
    val picture : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val title : MutableLiveData<String> by lazy { MutableLiveData<String>() }

    override suspend fun onCreate() {

    }
    fun loadTrackList(){
        model.retrieveTrackList(_widgets,item!!.tracklist)
        hasDataLoaded = true
    }

    private fun setupUI(){
        if(!item?.cover.isNullOrEmpty()) picture.value = item!!.cover
        if(!item?.title.isNullOrEmpty()) title.value = item!!.title
    }

    fun handlePlayButton() : MutableList<TrackModel>{
        if(widgets.value?.isEmpty() == true) return mutableListOf()
        return widgets.value?.elementAt(0)?.items as MutableList<TrackModel>
    }
    fun handleColorChange(bitmap: Bitmap, callback: (value:GradientDrawable)->Unit) {
        Palette.from(bitmap).generate(Palette.PaletteAsyncListener() {
            var value = -1
            if(it != null && it.darkMutedSwatch?.rgb != null)  value =  it.darkMutedSwatch!!.rgb
            else if(it != null && it.vibrantSwatch?.rgb != null) value = it.vibrantSwatch!!.rgb
            if(value!=-1) {
                val gd = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(value,Application.appContext!!.resources.getColor(R.color.black),Application.appContext!!.resources.getColor(R.color.black))

                )
                gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                gd.setGradientRadius(80.0f);
                gd.setGradientCenter(0.0f, 0.80f);
                gd.cornerRadius = 0f
                callback.invoke(gd)
            }
        })

    }
}