package com.perseverance.phando.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.perseverance.phando.db.APIData
import com.perseverance.phando.payment.paymentoptions.WalletDetail

/**
 * Created by trilokinathyadav on 2/15/2018.
 */

@Dao
interface WalletDetailDao {

    @Query("SELECT * FROM WalletDetail WHERE id =0")
    fun getWalletDetailLiveData(): LiveData<WalletDetail?>

    @Query("SELECT * FROM WalletDetail WHERE id =0")
    fun getWalletDetail(): WalletDetail?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(apiData: WalletDetail)

    @Query("DELETE FROM WalletDetail")
    fun deleteAll()

}
