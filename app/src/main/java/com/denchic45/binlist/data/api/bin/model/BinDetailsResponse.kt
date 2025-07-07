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
    @Serializable(OptionalPropertySerializer::class)
    val prepaid: OptionalProperty<Boolean> = OptionalProperty.NotPresent,
    val country: Country,
    val bank: Bank,
)

@Serializable
data class CardNumber(
    @Serializable(OptionalPropertySerializer::class)
    val length: OptionalProperty<Long> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val luhn: OptionalProperty<Boolean> = OptionalProperty.NotPresent,
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
