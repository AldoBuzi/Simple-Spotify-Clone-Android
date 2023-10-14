package unipi.sam.emusic.api

class API {
     val TAG: String = "API";

     companion object{

          final val baseUrl = "https://api.deezer.com"

          final val imageBaseUrl = "https://e-cdns-images.dzcdn.net/images/artist"
          final val defaultDimension = "250x250-000000-80-0-0.jpg"


          fun buildPathFromMd5(md5: String) : String{
               return "${imageBaseUrl}/${md5}/${defaultDimension}"
          }
     }
}