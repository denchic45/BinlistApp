package com.denchic45.binlist.data.api.bin.model

import kotlinx.serialization.Serializable

@Serializable
data class BinDetailsResponse(
    val number: CardNumberResponse,
    val scheme: String,
    val type: String,
    val brand: String? = null,
    val prepaid: Boolean? = null,
    val country: CountryResponse,
    val bank: BankResponse,
)

@Serializable
data class CardNumberResponse(
    val length: Long? = null,
    val luhn: Boolean? = null,
)

@Serializable
data class CountryResponse(
    val numeric: String,
    val alpha2: String,
    val name: String,
    val emoji: String,
    val currency: String,
    val latitude: Long,
    val longitude: Long,
)

@Serializable
data class BankResponse(
    val name: String? = null,
    val url: String? = null,
    val phone: String? = null,
    val city: String? = null,
)
