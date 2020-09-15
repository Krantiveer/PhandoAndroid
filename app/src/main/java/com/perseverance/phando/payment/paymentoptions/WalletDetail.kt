package com.perseverance.phando.payment.paymentoptions

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.perseverance.phando.db.dao.RoomDataTypeConvertor

@Entity
data class WalletDetail(
        @PrimaryKey
        val id: Int = 0,
        val balance: Int = 0,
        @TypeConverters(RoomDataTypeConvertor::class)
        var getWalletRechargePoints: List<String> = arrayListOf(),
        val is_active: Int = 0,
        val max_recharge_point: Int
)
