package unipi.sam.emusic.main_navigation.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import unipi.sam.emusic.base_components.BaseViewModel
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val homeModel: HomeModel): BaseViewModel() {



    override suspend fun onCreate() {

    }
    var isDataLoaded : Boolean = false
        private set

    suspend fun loadData() {
        //widgets.value = mutableListOf()
        homeModel.retrieveHome(_widgets)
        isDataLoaded = true
        //return widgets.value!!.toList()

    }

}