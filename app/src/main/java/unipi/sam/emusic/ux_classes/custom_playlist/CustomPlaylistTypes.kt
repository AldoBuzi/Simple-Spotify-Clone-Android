package unipi.sam.emusic.ux_classes.custom_playlist

import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.ux_classes.ViewContentLayout

enum class CustomPlaylistTypes {
    TRACK_LIST,
    ARTIST
}
interface CustomPlaylistViews : ViewContentLayout{
    var activity: MainActivity
}