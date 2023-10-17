package com.perseverance.phando.home.generes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.data.NotificationsData
import com.perseverance.phando.home.dashboard.viewmodel.DashboardViewModel
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.videolist.BaseVideoListActivity
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_genres_browse.*
import kotlinx.android.synthetic.main.activity_genres_browse.progressBar
import kotlinx.android.synthetic.main.activity_notifcation_settings.*
import kotlinx.android.synthetic.main.activity_payment_options.*
import kotlinx.android.synthetic.main.layout_header_new.*

class GenresBrowse : BaseScreenTrackingActivity(),
    GenresAdapter.AdapterClick {
    override var screenName: String = BaseConstants.GENRES_ACTIVITY

    var genresList = arrayListOf<GenresResponse>()
    var genresAdapter: GenresAdapter? = null


    private val homeActivityViewModel by lazy {
        ViewModelProvider(this@GenresBrowse).get(DashboardViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_genres_browse)
        txtTitle.text = "Browse by Genre"
        imgBack.setOnClickListener {
            finish()
        }

        homeActivityViewModel.callForGenresList()

        homeActivityViewModel.generesList.observe(this, Observer {
            progressBar.gone()
            rvGenres.visible()
            if (it != null) {
                setListAdapter(it)
            }

        })
    }

    override fun onItemClick(data: GenresResponse) {

        /* val intent = Intent(requireContext(), DashboardListActivity::class.java)
         intent.putExtra("FILTER", Gson().toJson(dataFilters))
         intent.putExtra("TITLE", data.displayName)
         requireContext().startActivity(intent)*/

        data.id.let {
            val intent1 = Intent(this, BaseVideoListActivity::class.java).apply {
                if (data.posterOrientation.equals("horizontal")){
                    putExtra("imageOrientation", 0)
                }  else {
                    putExtra("imageOrientation", 1)
                }
                putExtra("id", data.id.toString())
                putExtra("title", data.name)

                Log.e("@@posterorientation", data.posterOrientation.toString())

            }
            startActivity(intent1)

        }


    }

    private fun setListAdapter(mData: ArrayList<GenresResponse>) {
        Log.e("@@genres", mData[0].name.toString())
        genresAdapter = GenresAdapter(this, mData, this)
        rvGenres.adapter = genresAdapter
    }


}
