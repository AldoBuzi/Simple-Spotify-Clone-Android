package unipi.sam.emusic.api

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import unipi.sam.emusic.BuildConfig
import unipi.sam.emusic.commons.Application
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ApiClient {

     @Singleton
     @Provides
     fun provideRetrofit (): RestApiInterface{
          val logging = HttpLoggingInterceptor()
          //
          //logging.level = HttpLoggingInterceptor.Level.BODY

          val build = BuildConfig.DEBUG
          Log.i("ApiClient", "ApiClient get method -> build=$build ${BuildConfig.BUILD_TYPE}")
          if ( build ) {
               logging.level = HttpLoggingInterceptor.Level.BODY
          }else {
               logging.level = HttpLoggingInterceptor.Level.NONE
          }
          val httpClient = OkHttpClient.Builder()
          httpClient.addInterceptor { chain ->
               val original = chain.request()
               val request = original.newBuilder()
                    //.addHeader("X-RapidAPI-Key", API.TOKEN)
                    //.addHeader("X-RapidAPI-Host", API.HOST)
                    .method(original.method(), original.body())
                    .build()
               chain.proceed(request)
          }
          //httpClient.callTimeout(5000L, TimeUnit.MILLISECONDS)
          httpClient.addInterceptor(logging)

          return Retrofit.Builder().baseUrl( API.baseUrl )
               .client(httpClient.build())
               .addConverterFactory(GsonConverterFactory.create())
               .build()
               .create(RestApiInterface::class.java)

     }
}