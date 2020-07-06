package com.perseverance.phando.home.dashboard.mylist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.dashboard.BaseHomeFragment
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.utils.*
import com.qait.sadhna.LoginActivity
import kotlinx.android.synthetic.main.app_toolbar.*
import kotlinx.android.synthetic.main.fragment_mylist.*

class MyListFragment : BaseHomeFragment(), SwipeRefreshLayout.OnRefreshListener, AdapterClickListener {


    private var adapter: MyListAdapter? = null

    private val myListViewModel by lazy {
        ViewModelProviders.of(this).get(MyListViewModel::class.java)
    }

    private val videoListViewModelObserver = Observer<DataLoadingStatus<List<Video>>> {
        message.gone()
        when(it.status){

            LoadingStatus.LOADING ->{
                progressBar.visible()

            }
            LoadingStatus.ERROR ->{
                progressBar.gone()
                swipetorefresh_base.isRefreshing = false
                it.message?.let {
                    message.text=it
                }?: kotlin.run {
                    message.text = "Unable to fetch data"
                }

                message.visible()
            }
            LoadingStatus.SUCCESS ->{
                progressBar.gone()
                if (swipetorefresh_base.isRefreshing) {
                    swipetorefresh_base.isRefreshing = false
                }
                adapter?.clear()
                adapter?.addAll(it.data)
                if (it.data!!.isEmpty()){
                    message.text = "Add videos to your list to save \nand watch them later"
                    message.visible()
                }


            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mylist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarTitle.text="My List"
        myListViewModel.getMyList().observe(viewLifecycleOwner, videoListViewModelObserver)
        myListViewModel.message.observe(viewLifecycleOwner, Observer {
            Toast.makeText(appCompatActivity,it,Toast.LENGTH_LONG).show()
        })

        val manager = LinearLayoutManager(activity)
        recyclerView.layoutManager = manager
        recyclerView.setHasFixedSize(true)
        val decoration = BaseRecycleMarginDecoration(activity)
        recyclerView.addItemDecoration(decoration)
        adapter = MyListAdapter(appCompatActivity, this)
        val videos = ArrayList<Video>()
        adapter?.items = videos
        recyclerView.adapter = adapter

        swipetorefresh_base.setOnRefreshListener(this)
        val token = PreferencesUtils.getLoggedStatus()
        if (token.isEmpty()) {
            message.setOnClickListener {
                val intent = Intent(context, LoginActivity::class.java)
                startActivityForResult(intent,LoginActivity.REQUEST_CODE_LOGIN)
            }

        } else {
            loadVideos(0, true)
        }


        TrackingUtils.sendScreenTracker(BaseConstants.MY_LIST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==LoginActivity.REQUEST_CODE_LOGIN && resultCode== Activity.RESULT_OK){
            loadVideos(0, true)
        }
    }

    private fun loadVideos(pageCount: Int, showProgress: Boolean) {

        myListViewModel.refreshMyList("$pageCount, ${BaseConstants.LIMIT_VIDEOS}")
    }



    override fun onRefresh() {
        loadVideos(0, false)
    }



    override fun onItemClick(data: Any) {
        when(data){
            is Video ->{
                if (Utils.isNetworkAvailable(activity)) {
                    startActivity(MediaDetailActivity.getDetailIntent(activity as Context, data))
                    Utils.animateActivity(activity, "next")
                } else {
                    DialogUtils.showMessage(activity, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
                }
            }
            is String ->{
                val tempData = data.split(",")
                myListViewModel.removeFromMyList(tempData[0],tempData[1])
            }
        }

    }

}
