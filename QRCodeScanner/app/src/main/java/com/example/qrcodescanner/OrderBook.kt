package com.example.qrcodescanner

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderBook(var bookIdToOrder: Long, var userId: Long) : Parcelable