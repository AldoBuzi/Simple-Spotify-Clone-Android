package unipi.sam.emusic.base_components

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import unipi.sam.emusic.custom.readOnly
import unipi.sam.emusic.ux_classes.Widget

abstract class BaseViewModel : ViewModel(), ViewModelInterface {

    protected val _widgets: MutableLiveData<MutableCollection<Widget>> by lazy {
        MutableLiveData<MutableCollection<Widget>>()
    }
    val widgets: LiveData<MutableCollection<Widget>>  = _widgets.readOnly
}
private interface ViewModelInterface{
    suspend fun onCreate()
}