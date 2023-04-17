package com.example.qrcodescanner

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.Instant
import java.util.*


@Parcelize
data class HistoryBook(var dateOfOrder: String, var orderType: String, var book: Book) : Parcelable