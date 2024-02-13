package com.example.myburrito

import android.os.Parcel
import android.os.Parcelable

data class Burrito(
    val name: String?,
    var price: Double,
    val description: String?,
    val image: String?,
    var quantity: Int = 1,
    var customizedIngredients: HashMap<String, Boolean>? = null,
    val inCart: Boolean = false,
    val isFavorite: Boolean = false,

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        readMap(parcel)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeInt(quantity)
        writeMap(customizedIngredients, parcel)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Burrito> {
        override fun createFromParcel(parcel: Parcel): Burrito {
            return Burrito(parcel)
        }

        override fun newArray(size: Int): Array<Burrito?> {
            return arrayOfNulls(size)
        }

        private fun readMap(parcel: Parcel): HashMap<String, Boolean>? {
            val size = parcel.readInt()
            if (size < 0) return null

            val map = HashMap<String, Boolean>()
            repeat(size) {
                val key = parcel.readString() ?: ""
                val value = parcel.readByte() != 0.toByte()
                map[key] = value
            }
            return map
        }

        private fun writeMap(map: HashMap<String, Boolean>?, parcel: Parcel) {
            if (map == null) {
                parcel.writeInt(-1)
                return
            }

            parcel.writeInt(map.size)
            map.forEach {
                parcel.writeString(it.key)
                parcel.writeByte(if (it.value) 1 else 0)
            }
        }
    }
}
