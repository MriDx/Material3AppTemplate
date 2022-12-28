package com.mridx.androidtemplate.di

import com.mridx.androidtemplate.data.remote.repository.home.HomePageRepositoryImpl
import com.mridx.androidtemplate.data.remote.repository.upload.UploadRepositoryImpl
import com.mridx.androidtemplate.data.remote.repository.user.UserRepositoryImpl
import com.mridx.androidtemplate.domain.repository.home.HomePageRepository
import com.mridx.androidtemplate.domain.repository.upload.UploadRepository
import com.mridx.androidtemplate.domain.repository.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {


    /* @Provides
     @ViewModelScoped
     fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
         return LocationServices.getFusedLocationProviderClient(context)
     }*/

    @Provides
    @ViewModelScoped
    fun provideUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository =
        userRepositoryImpl


    @Provides
    @ViewModelScoped
    fun provideHomePageRepository(homePageRepositoryImpl: HomePageRepositoryImpl): HomePageRepository =
        homePageRepositoryImpl


    @Provides
    @ViewModelScoped
    fun provideUploadRepository(uploadRepositoryImpl: UploadRepositoryImpl): UploadRepository =
        uploadRepositoryImpl



}