package com.perseverance.phando.home.videolist

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.utils.BaseRecycleMarginDecoration
import kotlinx.android.synthetic.main.activity_base_list.*
import kotlinx.android.synthetic.main.fragment_base.*

abstract class BaseListActivity : AppCompatActivity(), AdapterClickListener {

    var manager: LinearLayoutManager = GridLayoutManager(this@BaseListActivity, 2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_list)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val decoration = BaseRecycleMarginDecoration(this@BaseListActivity)
        recycler_view_base.addItemDecoration(decoration)


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
