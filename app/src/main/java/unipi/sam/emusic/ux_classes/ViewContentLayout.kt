package unipi.sam.emusic.ux_classes

import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.models.ModelViewType

interface ViewContentLayout {



    fun showLoadedContent(item: ModelViewType)

    fun setListener(activity: MainActivity) {}
}