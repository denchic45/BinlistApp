package com.denchic45.binlist.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


data class BinDetailsRequest(
    val bin: String,
    val requestedAt: LocalDateTime,
    val number: CardNumber?,
    val scheme: String,
    val type: String,
    val brand: String?,
    val prepaid: PrepaidCard,
    val country: Country,
    val bank: Bank?,
) {

    private fun formatTime() = requestedAt.format(DateTimeFormatter.ofPattern("HH:mm"))

    val requestedDateText: String = when (ChronoUnit.DAYS.between(requestedAt, LocalDateTime.now())) {
        0L -> "Сегодня, ${formatTime()}"
        1L -> "Вчера,  ${formatTime()}"
        else -> {
            requestedAt.format(DateTimeFormatter.ofPattern(buildString {
                append("dd MMM")
                append(
                    if (requestedAt.year < LocalDate.now().year) " yyyy, HH:mm"
                    else ", HH:mm"
                )
            }))
        }
    }
}

data class CardNumber(val length: Long, val luhn: Boolean)

enum class PrepaidCard { YES, NO, UNKNOWN }


data class Country(
    val numeric: String,
    val alpha2: String,
    val name: String,
    val emoji: String,
    val currency: String,
    val latitude: Long,
    val longitude: Long,
)

data class Bank(
    val name: String,
    val details: BankDetails? = null
)

data class BankDetails(
    val url: String,
    val phone: String,
    val city: String,
)