package com.perseverance.phando.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.BaseVideo
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.home.videolist.BaseCategoryListAdapter
import com.perseverance.phando.ui.WaitingDialog
import com.perseverance.phando.utils.*
import com.perseverance.phando.videoplayer.VideoSelectedListener
import kotlinx.android.synthetic.main.content_search_result.*
import java.util.*

class SearchResultActivity : AppCompatActivity(), VideoSelectedListener, SearchView, AdapterClickListener {

    private var endlessScrollListener: EndlessScrollListener? = null
    private var pageCount: Int = 0
    private var adapter: BaseCategoryListAdapter? = null
    private var query: String? = null
    private var waitingDialog: WaitingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        query = intent.extras!!.getString(Key.SEARCH_QUERY, "")
        title = query

        val manager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = manager
        val decoration = BaseRecycleMarginDecoration(this@SearchResultActivity)
        recyclerView.addItemDecoration(decoration)
        pageCount = 0
        endlessScrollListener = object : EndlessScrollListener(manager) {
            override fun onLoadMore(currentPage: Int) {
                //footerProgress.setVisibility(View.VISIBLE);
                pageCount = currentPage
                footerProgress.visibility = View.VISIBLE
                loadSearchResult(query, false)
            }
        }
        recyclerView.addOnScrollListener(endlessScrollListener!!)
        val videos = ArrayList<Video>()
        adapter = BaseCategoryListAdapter(this, this)
        adapter!!.items = videos
        recyclerView.adapter = adapter

        loadSearchResult(query, true)

        val helper = SadhnaDBHelper(this)
        helper.addSearchHistory(query)

        TrackingUtils.sendScreenTracker( "SearchView")
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_search_result, menu)
        return true
    }

    private fun showSearchScreen() {
        val intent = Intent(this, SearchActivity::class.java)
        //intent.putExtra(Key.SEARCH_QUERY, query)
        startActivityForResult(intent, BaseConstants.REQUEST_CODE_SEARCH)
        Utils.animateActivity(this, "zero")
    }

    private fun loadSearchResult(query: String?, showProgress: Boolean) {
        val presenter = SearchPresenterImpl(this)
        presenter.search(pageCount, query, showProgress,null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        if (item.itemId == R.id.action_search) {
            showSearchScreen()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onVideoSelected(item: Video) {
        if (Utils.isNetworkAvailable(this)) {
            /*Intent intent = new Intent(this, PlayerListActivity.class);
            intent.putExtra(Key.VIDEO, item);
            startActivityForResult(intent, BaseConstants.REQUEST_CODE_PLAYER);
        */
            /*startActivity(MediaDetailActivity.getDetailIntent(this, item))
            Utils.animateActivity(this, "next")*/
            if("T".equals(item.mediaType)){
                val intent = Intent(this@SearchResultActivity, SeriesActivity::class.java)
                intent.putExtra(Key.CATEGORY, item)
                startActivity(intent)
            } else {
                startActivity(MediaDetailActivity.getDetailIntent(this@SearchResultActivity as Context, item))
                Utils.animateActivity(this@SearchResultActivity, "next")
            }

        } else {
            DialogUtils.showMessage(this, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
        }
    }

    override fun finish() {
        super.finish()
        Utils.animateActivity(this, "back")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        adapter!!.notifyDataSetChanged()
        if (requestCode == BaseConstants.REQUEST_CODE_SEARCH && resultCode == Activity.RESULT_OK) {
            query = data!!.extras!!.getString(Key.SEARCH_QUERY)
            query?.let {
                title = query
            }
            MyLog.e("Query: " + query!!)
            adapter!!.clear()
            endlessScrollListener!!.init()
            pageCount = 0
            loadSearchResult(query, true)
            val helper = SadhnaDBHelper(this)
            helper.addSearchHistory(query)
        } else if (requestCode == BaseConstants.REQUEST_CODE_SEARCH && resultCode == Activity.RESULT_CANCELED) {
            if (adapter!!.itemCount <= 0) {
                finish()
            }
        }
    }


    override fun showProgress(message: String) {
        if (waitingDialog == null) {
            waitingDialog = WaitingDialog(this)
            waitingDialog!!.setMessage(message)
        }
        if (!isFinishing) {
            waitingDialog!!.show()
        }
    }

    override fun dismissProgress() {
        if (waitingDialog != null && waitingDialog!!.isShowing) {
            waitingDialog!!.dismiss()
            waitingDialog = null
        }
    }

    override fun onSearchResultSuccess(videos: List<Video>) {
        if (pageCount == 0 && videos.size <= 0) {
           // DialogUtils.showMessage(this, "No result found for \"$query\"", Toast.LENGTH_SHORT) { showSearchScreen() }
            return
        }

        if (pageCount == 0) {
            adapter!!.clear()
        }
        adapter!!.addAll(videos)
        footerProgress.visibility = View.GONE
       // adapter!!.notifyDataSetChanged()
    }

    override fun onSearchResultError(errorMessage: String) {
        if (pageCount > 0) {
            pageCount -= BaseConstants.LIMIT_VIDEOS
            endlessScrollListener!!.rollback(pageCount)
        } else {
            DialogUtils.showMessage(this, errorMessage, Toast.LENGTH_SHORT, true)
        }
    }

    override fun onItemClick(data: Any) {
        if (Utils.isNetworkAvailable(this)) {
            /*Intent intent =new  Intent(this, PlayerListActivity.class);
            intent.putExtra(Key.VIDEO,(Video) data );
            startActivity(intent);*/
            /*startActivity(MediaDetailActivity.getDetailIntent(this, data as Video))
            Utils.animateActivity(this, "next")*/
            if(data is BaseVideo) {
                val video = data
                if("T".equals(video.mediaType)){
                    val intent = Intent(this@SearchResultActivity, SeriesActivity::class.java)
                    intent.putExtra(Key.CATEGORY, video)
                    startActivity(intent)
                } else {
                    startActivity(MediaDetailActivity.getDetailIntent(this@SearchResultActivity as Context, video))
                    Utils.animateActivity(this@SearchResultActivity, "next")
                }
            } else {
                DialogUtils.showMessage(this@SearchResultActivity, "BASE VIDEO CAST", Toast.LENGTH_SHORT, false)
            }
        } else {
            DialogUtils.showMessage(this, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
        }
    }
}
