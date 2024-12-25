package com.example.stylishadmin.model.brands

import android.os.Parcel
import android.os.Parcelable

data class Brand(
    val brandName: String = "",
    val imgIcon: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    // No-argument constructor for Firebase
    constructor() : this("", "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(brandName)
        parcel.writeString(imgIcon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Brand> {
        override fun createFromParcel(parcel: Parcel): Brand {
            return Brand(parcel)
        }

        override fun newArray(size: Int): Array<Brand?> {
            return arrayOfNulls(size)
        }
    }
}
