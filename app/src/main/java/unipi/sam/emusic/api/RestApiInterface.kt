package unipi.sam.emusic.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface RestApiInterface {


   @GET("${PATHS.search}{page}")
   fun getSearch(@Path("page" , encoded = true) page: String, @Query (value = "q") query:String) : Call<JsonObject>

   @GET("${PATHS.playlist}/{path}")
   fun getPlaylist(@Path ("path") path: String) : Call<JsonObject>
   @GET(PATHS.topSongs)
   fun getChart() : Call<JsonObject>
   @GET("${PATHS.album}/{path}")
   fun getAlbum(@Path ("path") path: String = "302127") : Call<JsonObject>

   @GET(PATHS.genre)
   fun getGenres() : Call<JsonObject>


   //specific content

   @GET("${PATHS.artist}/{artist_id}${PAGES.top}")
   fun getArtistTopTracks(@Path("artist_id") artistId : String) : Call<JsonObject>

   @GET("${PATHS.artist}/{artist_id}${PAGES.albums}")
   fun getArtistTopAlbums(@Path("artist_id") artistId : String) : Call<JsonObject>
   @GET("${PATHS.artist}/{artist_id}${PAGES.related}")
   fun getArtistRelated(@Path("artist_id") artistId : String) : Call<JsonObject>
   @GET("${PATHS.artist}/{artist_id}${PAGES.playlists}")
   fun getArtistTopPlaylist(@Path("artist_id") artistId : String) : Call<JsonObject>

   @GET
   fun getTrackList(@Url url: String) : Call<JsonObject>

   @GET("${PATHS.genre}/{id}${PAGES.artists}")
   fun getSpecificGenre(@Path("id") id: Int) : Call<JsonObject>


}