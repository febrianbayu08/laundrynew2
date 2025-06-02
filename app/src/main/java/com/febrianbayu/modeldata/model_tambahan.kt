package com.febrianbayu.modeldata
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class model_tambahan (
    val idTambahan: String? = null,
    val namaTambahan: String? = null,
    val hargaTambahan: String? = null,
    val cabang: String? = null,
): Parcelable
