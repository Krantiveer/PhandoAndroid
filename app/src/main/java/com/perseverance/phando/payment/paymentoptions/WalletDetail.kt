package com.perseverance.phando.payment.paymentoptions

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.perseverance.phando.db.dao.RoomDataTypeConverter

@Entity
data class WalletDetail(
        @PrimaryKey
        val id: Int = 0,
        val balance: Int = 0,
        @TypeConverters(RoomDataTypeConverter::class)
        var getWalletRechargePoints: List<String> = arrayListOf(),
        val is_active: Int = 0,
        val max_recharge_point: Int,
        val min_recharge_point: Int,
        var currency_symbol: String,
        var currency_code: String,
        var deactivate_wallet_msg: String = "",
        var hint1: String = "",
        var hint2: String = "",
        var wallet_conversion_points: Int = 1
)
