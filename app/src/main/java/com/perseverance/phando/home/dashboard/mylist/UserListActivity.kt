/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.perseverance.phando.home.dashboard.mylist

import android.app.Activity.RESULT_OK
import android.app.AlertDialog.BUTTON_POSITIVE
import android.app.AlertDialog.THEME_HOLO_LIGHT
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.perseverance.phando.BaseFragment
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.R
import kotlinx.android.synthetic.main.activity_user_list.*
import java.util.*


/**
 * Shows a static leaderboard with multiple users.
 */
class UserListActivity : BaseScreenTrackingActivity() {
    override var screenName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        title = "My List"
        container.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        val tabPagerAdapter = TabPagerAdapter(arrayListOf("My List","My Subscriptions"))
        container.adapter = tabPagerAdapter
        TabLayoutMediator(tabs, container, true) { tab, position ->
            tab.text = tabPagerAdapter?.getPageTitle(position)
        }.attach()
    }
    inner class TabPagerAdapter( private val categoryList: ArrayList<String>) : FragmentStateAdapter(this) {

        fun getPageTitle(position: Int): CharSequence? {
            return categoryList.get(position)
        }
        override fun getItemCount(): Int {
            return categoryList.size
        }

        override fun createFragment(position: Int): Fragment {
            val rssCategoryData = categoryList[position]
            if (rssCategoryData=="My List") {
                return MyListFragment()
            }else{
                return MyPurchasedListFragment()
            }

        }

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

