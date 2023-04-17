package com.example.qrcodescanner

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Client(var address: String, var firstName: String, var lastName: String, var login: String, var password: String, var userId: Long) :
    Parcelable