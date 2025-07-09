package com.denchic45.binlist.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bin_details")
data class BinDetailsEntity(
    @PrimaryKey(true)
    @ColumnInfo("bind_details_id")
    val id: Long = 0,
    val bin: String,
    @ColumnInfo(name = "requested_at", defaultValue = "CURRENT_TIMESTAMP" )
    val requestedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "card_num_length")
    val cardNumLength: Long?,
    @ColumnInfo(name = "card_num_luhn")
    val cardNumLuhn: Boolean?,
    @ColumnInfo(name = "scheme")
    val scheme: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "brand")
    val brand: String?,
    @ColumnInfo(name = "prepaid")
    val prepaid: Boolean?,

    @ColumnInfo(name = "country_numeric")
    val countryNumeric: String,
    @ColumnInfo(name = "country_alpha2")
    val countryAlpha2: String,
    @ColumnInfo(name = "country_name")
    val countryName: String,
    @ColumnInfo(name = "country_emoji")
    val countryEmoji: String,
    @ColumnInfo(name = "country_currency")
    val countryCurrency: String,
    @ColumnInfo(name = "country_latitude2")
    val countryLatitude: Long,
    @ColumnInfo(name = "country_longitude")
    val countryLongitude: Long,

    @ColumnInfo(name = "bank_name")
    val bankName: String?,
    @ColumnInfo(name = "bank_url")
    val bankUrl: String?,
    @ColumnInfo(name = "bank_phone")
    val bankPhone: String?,
    @ColumnInfo(name = "bank_city")
    val bankCity: String?,
)