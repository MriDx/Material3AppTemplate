package com.mridx.androidtemplate.di

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.core.os.bundleOf
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.undabot.izzy.parser.GsonParser
import com.undabot.izzy.parser.IzzyConfiguration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.mridx.androidtemplate.izzy_parser_wrapper.MyIzzy
import com.mridx.androidtemplate.izzy_parser_wrapper.MyIzzyConverter
import com.mridx.androidtemplate.utils.AnnotatedConverter
import com.mridx.androidtemplate.utils.GsonInterface
import com.mridx.androidtemplate.utils.IzzyInterface
import com.mridx.androidtemplate.utils.TOKEN
import com.mridx.androidtemplate.BuildConfig
import com.mridx.androidtemplate.data.local.constants.settings_fragment.SettingsItemModel
import com.mridx.androidtemplate.data.local.constants.settings_fragment.getSettingsItemsList
import com.mridx.androidtemplate.data.remote.model.misc.ImageModel
import com.mridx.androidtemplate.data.remote.model.misc.ImageModelParserAdapter
import com.mridx.androidtemplate.data.remote.web_service.ApiHelper
import com.mridx.androidtemplate.data.remote.web_service.ApiHelperImpl
import com.mridx.androidtemplate.data.remote.web_service.ApiService
import com.mridx.androidtemplate.di.qualifier.*
import com.mridx.androidtemplate.presentation.splash.activity.SplashActivity
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @BaseUrl
    fun provideBaseUrl(): String =
        if (BuildConfig.DEBUG) "http://inostaging.sumato.tech/" else "https://irrigationassam.in/"
    //if (BuildConfig.DEBUG) "https://irrigationassam.in/" else "https://irrigationassam.in/"


    @Provides
    @Singleton
    @ApiBaseUrl
    fun provideApiBaseUrl(@BaseUrl baseUrl: String): String = "${baseUrl}api/v1/"


    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(ImageModel::class.java, ImageModelParserAdapter())
            .create()
    }


    @Provides
    @Singleton
    fun provideGsonParser(gson: Gson): GsonParser {
        return GsonParser(
            izzyConfiguration = IzzyConfiguration(
                resourceTypes = arrayOf(
                )
            ),
            gson = gson
        )
    }


    @Provides
    @Singleton
    fun provideAnnotatedConverter(
        gson: Gson,
        gsonParser: GsonParser,
    ): AnnotatedConverter {
        return AnnotatedConverter(
            factoryMap = mapOf(
                GsonInterface::class.java to GsonConverterFactory.create(gson),
                IzzyInterface::class.java to MyIzzyConverter(izzy = MyIzzy(izzyJsonParser = gsonParser)),
            )
        )
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    @AuthInterceptor
    fun provideAuthInterceptor(@AppPreference sharedPreferences: SharedPreferences): Interceptor {
        return Interceptor { chain ->
            val token = sharedPreferences.getString(TOKEN, "") ?: ""
            val request = chain.request()
            val authRequest = request.newBuilder()
                .addHeader("Authorization", token)
                .build()
            chain.proceed(authRequest)
        }
    }


    @Singleton
    @Provides
    @CommonInterceptor
    fun provideCommonInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val commonRequest = request.newBuilder()
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(commonRequest)
        }
    }


    @Singleton
    @Provides
    @ResponseInterceptor
    fun provideResponseInterceptor(
        @ApplicationContext context: Context,
        @AppPreference sharedPreferences: SharedPreferences
    ): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            if (response.code == 401) {
                handleUnAuthResponse(context, sharedPreferences)
            }
            response
        }
    }

    private fun handleUnAuthResponse(context: Context, sharedPreferences: SharedPreferences) {
        sharedPreferences.edit {
            clear()
        }
        val i = Intent(context, SplashActivity::class.java)
        i.putExtras(
            bundleOf(
                "message" to "You are currently logged out. Please login to proceed."
            )
        )
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(i)
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @AuthInterceptor authInterceptor: Interceptor,
        @CommonInterceptor commonInterceptor: Interceptor,
        @ResponseInterceptor responseInterceptor: Interceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG)
                addInterceptor(loggingInterceptor)
            addInterceptor(authInterceptor)
            addInterceptor(commonInterceptor)
            addInterceptor(responseInterceptor)
            callTimeout(600, TimeUnit.SECONDS)
            readTimeout(600, TimeUnit.SECONDS)
            connectTimeout(10000, TimeUnit.SECONDS)
            build()
        }.build()

    }


    @Provides
    @Singleton
    fun provideRetrofit(
        @ApiBaseUrl apiBaseUrl: String,
        okHttpClient: OkHttpClient,
        annotatedConverter: AnnotatedConverter,
    ): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(apiBaseUrl)
            client(okHttpClient)
            addConverterFactory(annotatedConverter)
        }.build()
    }


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper


    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }


    /*@Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.build(context)


    @Singleton
    @Provides
    fun provideAppDao(appDatabase: AppDatabase): AppDao =
        appDatabase.appDao()*/

    @Singleton
    @Provides
    @AppPreference
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE)

    @Singleton
    @Provides
    @PermissionPreference
    fun providePermissionPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("PERMISSION_PREF", Context.MODE_PRIVATE)

    /*@Provides
    fun provideProfileActionItems(): List<ProfileFragmentActionModel> = getProfileActionItems()*/

    @Provides
    @SettingsItems
    fun provideSettingsItems(): List<SettingsItemModel> = getSettingsItemsList()



}