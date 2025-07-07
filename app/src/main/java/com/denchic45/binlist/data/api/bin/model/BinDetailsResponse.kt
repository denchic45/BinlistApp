package com.denchic45.binlist.data.api.bin.model

import com.denchic45.binlist.data.api.util.OptionalProperty
import com.denchic45.binlist.data.api.util.OptionalPropertySerializer
import kotlinx.serialization.Serializable

@Serializable
data class BinDetailsResponse(
    val number: CardNumber,
    val scheme: String,
    val type: String,
    val brand: String,
    val prepaid: Boolean? = null,
    val country: Country,
    val bank: Bank,
)

@Serializable
data class CardNumber(
    val length: Long? = null,
    val luhn: Boolean? = null,
)

@Serializable
data class Country(
    val numeric: String,
    val alpha2: String,
    val name: String,
    val emoji: String,
    val currency: String,
    val latitude: Long,
    val longitude: Long,
)

@Serializable
data class Bank(
    val name: String,
    val url: String? = null,
    val phone: String? = null,
    val city: String? = null,
)
