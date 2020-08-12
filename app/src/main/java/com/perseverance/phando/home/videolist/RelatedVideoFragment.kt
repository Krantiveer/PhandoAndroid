package com.perseverance.phando.home.videolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.perseverance.phando.R
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.mediadetails.MediaDetailViewModel
import com.perseverance.phando.home.series.SeriesActivity
import kotlinx.android.synthetic.main.fragment_related_video.*


class RelatedVideoFragment : Fragment(), AdapterClickListener {

    private var adapter: BaseCategoryListAdapter? = null
    private lateinit var mActivityCompat: AppCompatActivity
    val playerViewModel by lazy {
        ViewModelProviders.of(mActivityCompat).get(MediaDetailViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppCompatActivity) {
            mActivityCompat = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_related_video, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = GridLayoutManager(mActivityCompat, 2)
        recyclerView.layoutManager = manager
        val videos = ArrayList<Video>()
        adapter = BaseCategoryListAdapter(mActivityCompat, this)
        adapter?.items = videos
        recyclerView.adapter = adapter

//        playerViewModel.getVideosMutableLiveData().observe(this, Observer {
//            it?.let {
//                progressBar.visible()
//                errorText.gone()
//                playerViewModel.callForRelatedVideos(it.entryId, false, it.type)
//            }
//
//        })

//        playerViewModel.getRelatedVideosMutableLiveData().observe(this, Observer { videosModel ->
//            if (videosModel!!.throwable == null) {
//                progressBar.gone()
//                adapter?.let {
//                    it.setItems(videosModel.videos)
//                    if (videosModel.videos.size==0){
//                        errorText.text = BaseConstants.RELATED_VIDEOS_NOT_FOUND_ERROR_RELATED
//                        errorText.visible()
//                        info.gone()
//                    }else{
//                        info.visible()
//                    }
//                }
//            } else {
//                errorText.text = Utils.getErrorMessage(videosModel!!.throwable)
//                errorText.visible()
//                info.gone()
//                progressBar.gone()
//            }
//        })


    }


    override fun onItemClick(data: Any) {
        data as Video
        if ("T".equals(data.type)) {
            val intent = Intent(activity, SeriesActivity::class.java)
            intent.putExtra(Key.CATEGORY, data)
            startActivity(intent)
        } else {
            //  playerViewModel.getVideosMutableLiveData().value = data
        }


    }
}
