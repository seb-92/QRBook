package com.example.qrcodescanner

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseObject(var message: String) :
    Parcelable