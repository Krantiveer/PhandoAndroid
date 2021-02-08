package com.perseverance.phando.payment.paymentoptions

import android.os.Bundle
import android.view.MenuItem
import com.perseverance.phando.BaseScreenTrackingActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import kotlinx.android.synthetic.main.activity_wallet_history.*

class WalletHistoryActivity : BaseScreenTrackingActivity() {
    override var screenName= BaseConstants.WALLWET_HISTORY_SCREEN
    private val walletDetailViewModel by lazy {
        ViewModelProvider(this).get(WalletDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_history)
        setSupportActionBar(toolbar)
        title = "History"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val walletRechargeHistoryListAdapter = WalletRechargeHistoryListAdapter(this@WalletHistoryActivity)
        rechargeHistory.adapter=walletRechargeHistoryListAdapter
        val dividerItemDecoration = DividerItemDecoration(this@WalletHistoryActivity, LinearLayoutManager.VERTICAL)
        rechargeHistory.addItemDecoration(dividerItemDecoration)
        walletDetailViewModel.walletHistoryLiveData.observe(this, Observer {
            it ?: return@Observer
            progressBar.gone()
            walletRechargeHistoryListAdapter.items = it.data
        })
        progressBar.visible()
        walletDetailViewModel.getWalletHistory()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}