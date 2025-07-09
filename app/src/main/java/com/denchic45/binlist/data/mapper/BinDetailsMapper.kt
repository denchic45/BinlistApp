package com.denchic45.binlist.data.mapper

import com.denchic45.binlist.data.api.bin.model.BinDetailsResponse
import com.denchic45.binlist.data.database.BinDetailsEntity
import com.denchic45.binlist.domain.model.Bank
import com.denchic45.binlist.domain.model.BankDetails
import com.denchic45.binlist.domain.model.BinDetailsRequest
import com.denchic45.binlist.domain.model.CardNumber
import com.denchic45.binlist.domain.model.Country
import com.denchic45.binlist.domain.model.PrepaidCard
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone

fun List<BinDetailsEntity>.toBinsDetails() = map { it.toBinDetails() }


fun BinDetailsEntity.toBinDetails() = BinDetailsRequest(
    bin = bin,
    requestedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(requestedAt), TimeZone.getDefault().toZoneId()),
    number = cardNumLength?.let { length ->
        CardNumber(
            length = length,
            luhn = cardNumLuhn!!
        )
    },
    scheme = scheme,
    type = type,
    brand = brand,
    prepaid = when (prepaid) {
        true -> PrepaidCard.YES
        false -> PrepaidCard.NO
        null -> PrepaidCard.UNKNOWN
    },
    country = Country(
        numeric = countryNumeric,
        alpha2 = countryAlpha2,
        name = countryName,
        emoji = countryEmoji,
        currency = countryCurrency,
        latitude = countryLatitude,
        longitude = countryLongitude
    ),
    bank = bankName?.let {
        Bank(
            name = bankName,
            details = bankUrl?.let {
                BankDetails(
                    url = bankUrl,
                    phone = bankPhone!!,
                    city = bankCity!!
                )
            }
        )
    } ,
)

fun BinDetailsResponse.toBinDetailsEntity(bin: String) = BinDetailsEntity(
    bin = bin,
    cardNumLength = number.length,
    cardNumLuhn = number.luhn,
    scheme = scheme,
    type = type,
    brand = brand,
    prepaid = prepaid,
    countryNumeric = country.numeric,
    countryAlpha2 = country.alpha2,
    countryName = country.name,
    countryEmoji = country.emoji,
    countryCurrency = country.currency,
    countryLatitude = country.latitude,
    countryLongitude = country.longitude,
    bankName = bank.name,
    bankUrl = bank.url,
    bankPhone = bank.phone,
    bankCity = bank.city
)

//fun BinDetailsResponse.toBinDetails() = BinDetails(
//    number = number.length?.let { length ->
//        CardNumber(
//            length = length,
//            luhn = number.luhn!!
//        )
//    },
//    scheme = scheme,
//    type = type,
//    brand = brand,
//    prepaid = when (prepaid) {
//        true -> PrepaidCard.YES
//        false -> PrepaidCard.NO
//        null -> PrepaidCard.UNKNOWN
//    },
//    country = with(country) {
//        Country(
//            numeric = numeric,
//            alpha2 = alpha2,
//            name = name,
//            emoji = emoji,
//            currency = currency,
//            latitude = latitude,
//            longitude = longitude
//        )
//    },
//    bank = Bank(
//        name = bank.name,
//        details = bank.url?.let {
//            BankDetails(
//                url = bank.url,
//                phone = bank.phone!!,
//                city = bank.city!!
//            )
//        }
//    ),
//)