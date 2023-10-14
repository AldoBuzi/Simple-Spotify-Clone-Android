package unipi.sam.emusic.main_module

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val model: MainModel): ViewModel() {

    //search view related
    val searchWidgets: MutableLiveData<MutableCollection<Widget>> by lazy {
        MutableLiveData<MutableCollection<Widget>>()
    }
    val isSuggestionVisible : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    var isSearching : MutableLiveData<Boolean> = MutableLiveData(false)

    private var SEARCH_MIN_WORDS_COUNT = 3
    private var lastSearch = ""

    fun handleSearchTextChange(newText: String?){
        if(newText == null) {
            isSearching.value = false
            return
        }
        if(newText.length < SEARCH_MIN_WORDS_COUNT) return
        if(lastSearch == newText) return
        lastSearch = newText
        isSearching.value = true
        isSuggestionVisible.value = false
        model.retrieveSearch(searchWidgets,newText)
        Handler(Looper.getMainLooper()).postDelayed({
            isSuggestionVisible.value = true
        },200)
        //isSuggestionVisible.value = true

    }
}