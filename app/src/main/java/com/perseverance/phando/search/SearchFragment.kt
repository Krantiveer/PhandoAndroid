package com.perseverance.phando.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseFragment
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.*
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.dashboard.filter.GenresFilterAdapter
import com.perseverance.phando.home.dashboard.filter.LanguageFilterAdapter
import com.perseverance.phando.home.dashboard.models.DataFilters
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.home.videolist.BaseCategoryListAdapter
import com.perseverance.phando.utils.*
import com.perseverance.phando.videoplayer.VideoSelectedListener
import kotlinx.android.synthetic.main.app_toolbar.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_search_result.*
import java.util.*


class SearchFragment : BaseFragment(), VideoSelectedListener, SearchView, AdapterClickListener {
    override var screenName= BaseConstants.SEARCH_SCREEN
    private var endlessScrollListener: EndlessScrollListener? = null
    private var pageCount: Int = 0
    private var adapter: BaseCategoryListAdapter? = null
    private var query: String? = null
    private var isGenresFilter = false
    private var sheetBehavior: BottomSheetBehavior<*>? = null
    val dataFilters = DataFilters()
    var timer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = GridLayoutManager(appCompatActivity, 2)
        recyclerView.layoutManager = manager
        val decoration = BaseRecycleMarginDecoration(appCompatActivity)
        recyclerView.addItemDecoration(decoration)
        toolbarTitle.text = "Search"
        pageCount = 0
        endlessScrollListener = object : EndlessScrollListener(manager) {
            override fun onLoadMore(currentPage: Int) {
                //footerProgress.setVisibility(View.VISIBLE);
                pageCount = currentPage
                // footerProgress.setVisibility(View.VISIBLE)
                loadSearchResult(query, false)
            }
        }
        recyclerView.addOnScrollListener(endlessScrollListener!!)
        val videos = ArrayList<Video>()
        adapter = BaseCategoryListAdapter(requireActivity(), this)
        adapter!!.items = videos
        recyclerView.adapter = adapter
//        searchView.setOnQueryTextFocusChangeListener(object : View.OnFocusChangeListener{
//            override fun onFocusChange(view: View?, hasFocus: Boolean) {
//
//                if (hasFocus){
//                    view?.let {
//                         view.postDelayed(Runnable {
//                         val imm = appCompatActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                        imm.showSoftInput(view.findFocus(), 0)
//                         },1000)
//                    }
//                }
//            }
//
//        })


        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {


            override fun onQueryTextSubmit(newText: String?): Boolean {

                newText?.let {
                    if (it.length > 0) {
                        query = newText
                        loadSearchResult(query, true)
                    }
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                timer?.cancel()
                timer = object : CountDownTimer(1000, 2000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        newText?.let {
                            if (TextUtils.isEmpty(newText)) {
                                query = ""
                                error.text = ""
                                error.gone()

                            }


                            if (it.length > 0) {
//                        error.text=""
//                        error.gone()
                                query = newText
                                loadSearchResult(query, true)
                            } else {
                                query = ""
                                error.text = ""
                                error.gone()
                            }
                        }
                    }
                }.start()


                return true
            }

        })
        searchView.setOnCloseListener {
            Toast.makeText(appCompatActivity, "Clodes", Toast.LENGTH_SHORT).show()
            false
        }


        filter.setOnClickListener {
            filterContainer.apply {
                if (visibility == View.VISIBLE) {
                    dataFilters.apply {
                        type = ""
                        genre_id = ""
                        filter = ""

                    }
                    loadSearchResult(query, false)
                    gone()
                    filter.postDelayed(Runnable {
                        globleFilter.text = "Language"
                        genresFilter.text = "All Genres"

                    }, 500)

                } else {
                    visible()
                }
            }

        }

        genresFilter.setOnClickListener {
            isGenresFilter = true
            if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }

        globleFilter.setOnClickListener {
            isGenresFilter = false
            if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }

        // click event for show-dismiss bottom sheet
        closeButton.setOnClickListener(View.OnClickListener {
            if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        })
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        // callback for do something
        sheetBehavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {


                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                        filters.adapter = null
                        if (!isGenresFilter) {
                            filters.layoutManager = LinearLayoutManager(appCompatActivity)
                            val allFilterAdapter = LanguageFilterAdapter(appCompatActivity as Context, this@SearchFragment)
                            filters.adapter = allFilterAdapter
                            val filterList = AppDatabase.getInstance(appCompatActivity!!.applicationContext)?.languageDao()?.allLanguage()
                            allFilterAdapter.addAll(filterList)
                        } else {
                            filters.layoutManager = LinearLayoutManager(appCompatActivity)
                            val filterAdapter = GenresFilterAdapter(appCompatActivity as Context, this@SearchFragment)
                            filters.adapter = filterAdapter
                            val filterList = AppDatabase.getInstance(appCompatActivity!!.applicationContext)?.categoryDao()?.allGenres()
                            filterAdapter.addAll(filterList)
                        }

                    }
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })

    }


    private fun loadSearchResult(query: String?, showProgress: Boolean) {
        if (query.isNullOrBlank()) {
            return
        }
        if (!Utils.isNetworkAvailable(appCompatActivity)) {
            DialogUtils.showNetworkErrorToast()
            return
        }
        progressBar.visible()
        val presenter = SearchPresenterImpl(this)
        presenter.search(pageCount, query, showProgress, dataFilters)
    }

    override fun onVideoSelected(item: Video) {
        if (Utils.isNetworkAvailable(appCompatActivity)) {
            /*Intent intent = new Intent(this, PlayerListActivity.class);
            intent.putExtra(Key.VIDEO, item);
            startActivityForResult(intent, BaseConstants.REQUEST_CODE_PLAYER);
        */
            /*startActivity(MediaDetailActivity.getDetailIntent(this, item))
            Utils.animateActivity(this, "next")*/
            if ("T".equals(item.type)) {
                val intent = Intent(appCompatActivity, SeriesActivity::class.java)
                intent.putExtra(Key.CATEGORY, item)
                startActivity(intent)
            } else {
                startActivity(MediaDetailActivity.getDetailIntent(this@SearchFragment as Context, item))
                Utils.animateActivity(appCompatActivity, "next")
            }

        } else {
            DialogUtils.showMessage(appCompatActivity, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
        }
    }


    override fun onSearchResultSuccess(videos: List<Video>) {
        try {
            progressBar.gone()
        } catch (e: Exception) {
        }
        if (videos.size > 0) {
            error.text = ""
            error.gone()
            if (pageCount == 0) {
                adapter!!.clear()
            }
            adapter!!.clear()
            adapter!!.addAll(videos)
        } else {
            adapter!!.clear()
            error.visible()
            error.text = "No result found for \"$query\""
        }


        // footerProgress.setVisibility(View.GONE)
        // adapter!!.notifyDataSetChanged()
    }

    override fun onSearchResultError(errorMessage: String) {
        try {
            progressBar.gone()
        } catch (e: Exception) {
        }
        if (pageCount > 0) {
            pageCount -= BaseConstants.LIMIT_VIDEOS
            endlessScrollListener!!.rollback(pageCount)
        } else {
            adapter!!.clear()
            //error.visible()
            // error.text=errorMessage
            //DialogUtils.showMessage(appCompatActivity, errorMessage, Toast.LENGTH_SHORT, false)
        }
    }

    override fun onItemClick(data: Any) {
        when (data) {

            is Language -> {
                dataFilters.apply {

                    filter = data.id

                }
                loadSearchResult(query, false)
                globleFilter.text = data.language
                if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
                } else {
                    sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }
            }
            is Category -> {
                dataFilters.apply {
                    genre_id = data.id


                }
                loadSearchResult(query, false)
                genresFilter.text = data.name
                if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
                } else {
                    sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }
            }

            is Video -> {
                if (Utils.isNetworkAvailable(appCompatActivity)) {
                    val video = data
                    if ("T".equals(video.type)) {
                        val intent = Intent(appCompatActivity, SeriesActivity::class.java)
                        intent.putExtra(Key.CATEGORY, video)
                        startActivity(intent)
                    } else {
                        startActivity(MediaDetailActivity.getDetailIntent(appCompatActivity, video))
                        Utils.animateActivity(appCompatActivity, "next")
                    }
                }
            }
        }

    }

    override fun showProgress(message: String) {

    }

    override fun dismissProgress() {

    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
    }
}
