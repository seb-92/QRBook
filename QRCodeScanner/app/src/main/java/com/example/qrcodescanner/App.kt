package com.example.qrcodescanner

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {
    companion object {
        private lateinit var instance: App
        fun getInstance() = instance
    }

    private var retrofit:Retrofit? = null
    override fun onCreate() {
        super.onCreate()
        instance = this

        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.136:8090/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getApiServices() = retrofit!!.create(APIServices::class.java)
}