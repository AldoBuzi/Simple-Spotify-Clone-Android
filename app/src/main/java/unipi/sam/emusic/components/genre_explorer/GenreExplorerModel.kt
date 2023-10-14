package unipi.sam.emusic.components.genre_explorer

import androidx.lifecycle.MutableLiveData
import unipi.sam.emusic.base_components.BaseRemote
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject

class GenreExplorerModel @Inject constructor() : BaseRemote() {


    fun retrieveGenre(widgets: MutableLiveData<MutableCollection<Widget>> , id:Int){
        val call = apiService.getSpecificGenre(id)
        call.enqueue(baseCallback(LayoutType.ARTIST,widgets))
    }
}