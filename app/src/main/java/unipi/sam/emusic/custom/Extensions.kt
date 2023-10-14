package unipi.sam.emusic.custom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

val <T> MutableLiveData<T>.readOnly: LiveData<T>
    get() = this