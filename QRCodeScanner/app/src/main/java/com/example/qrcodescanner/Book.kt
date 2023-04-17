package com.example.qrcodescanner

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Book(var bookId: Long, var title: String, var author: String, var amountOfPages: Int, var yearOfPublish: Int, var available: Boolean) : Parcelable