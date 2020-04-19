package com.supersamin.firebasemessanger

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profileUrl: String): Parcelable{
    constructor() : this("","","")
}