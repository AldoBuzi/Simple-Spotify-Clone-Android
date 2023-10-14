package unipi.sam.emusic.commons

import unipi.sam.emusic.ux_classes.LayoutAdapter
import unipi.sam.emusic.ux_classes.Widget

interface FragmentInterface {

    var adapter: LayoutAdapter?

    fun showLoadedData(data: Collection<Widget>)
}