package com.thesubgraph.safenet.di

import android.content.Context
import com.google.gson.Gson
import com.thesubgraph.annotations.Authenticated
import com.thesubgraph.annotations.CustomHeaders
import com.thesubgraph.safenet.SessionState
import com.thesubgraph.safenet.data.common.RequestWrapper
import com.thesubgraph.safenet.data.common.WebServiceInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Invocation
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    @Singleton
    fun provideSessionState(): SessionState {
        return SessionState()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun providerRequestWrapper(
        gson: Gson,
        @ApplicationContext context: Context,
    ): RequestWrapper {
        return RequestWrapper(
            gson = gson,
            context = context
        )
    }

    @Provides
    @Singleton
    @Named("Authorization")
    fun provideAuthorizationInterceptor(
        sessionState: SessionState,
    ): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            request = if (request.tag(Invocation::class.java)?.method()
                    ?.isAnnotationPresent(Authenticated::class.java) == true
            ) {
                val requestBuilder = request.newBuilder()
                requestBuilder.header(
                    "Authorization",
                    (sessionState.accessToken?.tokenType
                        ?: "") + " " + sessionState.accessToken?.token
                )
                    .method(request.method, request.body)
                requestBuilder.build()
            } else {
                request
            }
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideCustomHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            val invocation = request.tag(Invocation::class.java)
            val method = invocation?.method()
            val serviceClass = invocation?.method()?.declaringClass

            val customHeaders = method?.getAnnotation(CustomHeaders::class.java)
                ?: serviceClass?.getAnnotation(CustomHeaders::class.java)
            request = customHeaders?.let { headers ->
                val newRequest = request.newBuilder()
                headers.headers.forEach { header ->
                    val parts = header.split(":")
                    if (parts.size == 2) {
                        newRequest.addHeader(parts[0].trim(), parts[1].trim())
                    }
                }
                newRequest.build()
            } ?: request
            chain.proceed(request)
        }
    }

    @Singleton
    @Provides
    fun provideHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @Named("Authorization") authorizationInterceptor: Interceptor,
        customHeadersInterceptor: Interceptor,
        @ApplicationContext context: Context,
    ): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(0, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authorizationInterceptor)
            .addInterceptor(customHeadersInterceptor)
        return okHttpClient.build()
    }

    fun <T : WebServiceInterface> provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
        baseUrl: String? = null,
        serviceInterface: Class<T>,
    ): T {
        baseUrl?.let {
            return retrofit2.Retrofit.Builder()
                .baseUrl(it)
                .client(okHttpClient)
                .addConverterFactory(gsonConverterFactory)
                .build()
                .create(serviceInterface)
        } ?: throw IllegalArgumentException("Base URL not set for ${serviceInterface.name}")
    }

}
