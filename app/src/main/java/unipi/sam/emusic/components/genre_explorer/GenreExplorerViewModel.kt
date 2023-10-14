package unipi.sam.emusic.components.genre_explorer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import unipi.sam.emusic.base_components.BaseViewModel
import unipi.sam.emusic.models.AlbumModel
import unipi.sam.emusic.models.ArtistModel
import unipi.sam.emusic.models.GenreModel
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject

@HiltViewModel
class GenreExplorerViewModel @Inject constructor(val model: GenreExplorerModel) : BaseViewModel() {


    var item : GenreModel? = null
        set(value) {
            field = value
            setupUI()
        }

    var isDataLoaded = false

    override suspend fun onCreate() {}
    fun loadGenre(){
        if(item?.id == null) return
        model.retrieveGenre(_widgets,item!!.id!!)
        isDataLoaded = true
    }

    private fun setupUI(){

    }
}