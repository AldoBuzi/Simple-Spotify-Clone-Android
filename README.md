# EMusic - Android App ![Build status](https://github.com/wallabag/android-app/workflows/CI/badge.svg?branch=master)

<img src="https://github.com/AldoBuzi/emusic_sam/blob/main/app/src/main/ic_launcher-playstore.png" align="left"
width="200" hspace="10" vspace="10">

EMusic is an app made for a university project, it was made in just under two weeks, so for any problems or improvements I am available. Emusic simulates a music app, allowing you to listen to songs, create playlists, view artists/albums, and finally do searches.


Emusic is available on the Google Play Store.

<p align="left">
<a href="https://play.google.com/store/apps/details?id=unipi.sam.emusic">
    <img alt="Get it on Google Play"
        height="80"
        src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" />
</a>  

    
## About

Emusic has been made as a university project.
You can download Emusic from play store.
Alternatively, you can use the direct link [emusic](https://play.google.com/store/apps/details?id=unipi.sam.emusic).
Landing page here: https://emusic-landing-page.web.app/


This application was originally created by Donaldo Buzi and released under the CC BY-NC-SA 4.0.

## Features

The android app lets you:
- Play a track list
- Save tracks and play them in background.
- Download tracks.
- Search for artist/tracks/album and playlist
- Navigate by genre
- And so many more!

## Screenshots

[<img src="https://github.com/AldoBuzi/emusic_sam/blob/main/screenshots/image1.jpeg" align="center"
width="200"
    hspace="0" vspace="10">](https://github.com/AldoBuzi/emusic_sam/blob/main/screenshots/image1.jpeg)
[<img src="https://github.com/AldoBuzi/emusic_sam/blob/main/screenshots/image2.jpeg" align="center"
width="200"
    hspace="0" vspace="10">](https://github.com/AldoBuzi/emusic_sam/blob/main/screenshots/image2.jpeg)
[<img src="https://github.com/AldoBuzi/emusic_sam/blob/main/screenshots/image3.jpeg" align="center"
width="200"
    hspace="0" vspace="10">](https://github.com/AldoBuzi/emusic_sam/blob/main/screenshots/image3.jpeg)
[<img src="https://github.com/AldoBuzi/emusic_sam/blob/main/screenshots/image4.jpeg" align="center"
width="200"
    hspace="0" vspace="10">](https://github.com/AldoBuzi/emusic_sam/blob/main/screenshots/image4.jpeg)

## Database and data
The data is retrieved from the api provided by deezer (https://developers.deezer.com/api), it is
subsequently mapped remotely by the various classes under /models, specifically, the
AlbumModel, ArtistModel, GenreModel, PodcastModel and TrackModel classes. Each class implements
the ModelViewType interface, this is to allow for easier parsing as each model is
passed with the apparent ModelViewType type. Remote parsing of the data is done via serialization
provided by gson.
The PlaylistModel and TrackModel classes also represent (room) database entities, so in addition to
play the role of container for the remote data they also play the role of database tables, following
the structure:

In PlaylistModel the primary key is given databaseID, which is a separate key from id. In the
specific, the id key is populated with the remote id of the playlist while the databaseId key is auto-generated
by the database whenever a new entry is created, by default if the PlaylistModel is
generated from remote data it will have that databaseId will be worth 0 and isFromDatabase will be worth false.
As for the TrackModel, its primary key is given by the id and the playlistID. The key
we create then is a key formed by the remote id of the song and the databaseId provided by
PlaylistModel(this is because playlistId is a foreign key). With this pair we are able to
identify whether a song belongs to a local playlist. By default if the model is populated with the
remote data you will have that databaseId will be worth 0.
Finally still regarding TrackModel and PlaylistModel we have that both of them also implement
the RoomComponent interface, an interface dedicated always to abstraction as for ModelViewType.

## How are layout generated
Layouts are generated dynamically, specifically we have the Widget class (under /ux_classes)
which takes care of parsing the data into the corresponding templates. Each widget will have its own
viewType (of type LayoutType) to identify which layout should generate the LayoutAdapter adapter in addition to
having the items that represent the actual entries of a "section."
Getting more specific, LayoutAdapter extends BaseAdapter (under /base_components), which in turn
in turn extends RecyclerView.Adapter. This whole system therefore relies on the use of RecyclerView for
a matter of flexibility but also efficiency. A single widget will then define its own viewHolder,
Adapter, etc., to later populate the main recyclerView with the various contents.

## Architecture And Patterns
I integrated my app according to the pattern one activity many fragments and as architecture the MVVM, which
corresponds to Model - View - ViewModel making use of LiveData and Flow. Specifically, the viewModels take care of
take care of maintaining any instances to the database, the data affecting the view and manage all the
business logic affecting the latter, finally retrieving the necessary data via the Model. The view will in
fact will only observe data changes via the Observers and send any events to the
viewModel. As with the database and the DownloadManager singleton, viewModels are also created
via injection.
The base_components folder contains all the "base" classes such as BaseFragment,
BaseAdapter, BaseViewHolder etc..
In this folder is the BaseRemote class, each model extends this class to have access
to the apiService and utility methods such as updateWidgets (updates the data container) and baseCallback
which essentially allows you to have a ready-to-use callback that is good for almost any use case.
In the /api folder are all the data involved in retrieving remote data and the various paths that are
used. The RestApiInterface class is the one that implements the abstraction through the various methods

## Contributing

Emusic app is an open source project developed by a student. Any contributions are welcome. Here are a few ways you can help:
 * [Report bugs and make suggestions.](https://github.com/AldoBuzi/emusic_sam/issues)
 * Write some code. Please follow the code style used in the project to make a review process faster.

## License

Shield: [![CC BY-NC-SA 4.0][cc-by-nc-sa-shield]][cc-by-nc-sa]

This work is licensed under a [Creative Commons Attribution-NonCommercial-ShareAlike 4.0
International License][cc-by-nc-sa].

[![CC BY-NC-SA 4.0][cc-by-nc-sa-image]][cc-by-nc-sa]

[cc-by-nc-sa]: http://creativecommons.org/licenses/by-nc-sa/4.0/
[cc-by-nc-sa-image]: https://licensebuttons.net/l/by-nc-sa/4.0/88x31.png
[cc-by-nc-sa-shield]: https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg
