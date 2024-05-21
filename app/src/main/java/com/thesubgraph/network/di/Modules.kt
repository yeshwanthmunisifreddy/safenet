package com.thesubgraph.network.di

import com.thesubgraph.safenet.di.AppModule
import com.thesubgraph.safenet.di.DispatcherModule
import com.thesubgraph.safenet.di.RetrofitModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [RetrofitModule::class, AppModule::class,DispatcherModule::class])
@InstallIn(SingletonComponent::class)
object Modules