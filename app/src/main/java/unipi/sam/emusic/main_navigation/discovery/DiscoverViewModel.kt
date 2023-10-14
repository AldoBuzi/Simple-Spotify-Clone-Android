package unipi.sam.emusic.main_navigation.discovery

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import unipi.sam.emusic.base_components.BaseViewModel
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(private var discoverModel: DiscoverModel): BaseViewModel() {

    var isDataLoaded : Boolean = false
        private set

    override suspend fun onCreate() {
    }
    fun loadGenres(){
        discoverModel.retrieveGenres(_widgets)
        isDataLoaded = true
    }


}