package com.denchic45.binlist.data.mapper

import com.denchic45.binlist.data.api.bin.model.Bank
import com.denchic45.binlist.data.api.bin.model.BinDetailsResponse
import com.denchic45.binlist.data.api.bin.model.CardNumber
import com.denchic45.binlist.data.api.bin.model.Country
import com.denchic45.binlist.data.database.BinDetailsEntity

fun List<BinDetailsEntity>.toResponses() = map { it.toResponse() }

fun BinDetailsEntity.toResponse() = BinDetailsResponse(
    number = CardNumber(
        length = cardNumLength,
        luhn = cardNumLuhn
    ),
    scheme = scheme,
    type = type,
    brand = brand,
    prepaid = prepaid,
    country = Country(
        numeric = countryNumeric,
        alpha2 = countryAlpha2,
        name = countryName,
        emoji = countryEmoji,
        currency = countryCurrency,
        latitude = countryLatitude,
        longitude = countryLongitude
    ),
    bank = Bank(
        name = bankName,
        url = bankUrl,
        phone = bankPhone,
        city = bankCity
    ),
)