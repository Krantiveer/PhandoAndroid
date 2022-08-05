package com.perseverance.phando.dialogs

import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import com.perseverance.phando.adapter.ListDialogAdapter
import com.perseverance.phando.home.dashboard.models.BrowseData
import com.perseverance.phando.home.dashboard.models.CategoryTab
import kotlinx.android.synthetic.main.dialog_list_category.*

class ListDialog (
    context: Context,
    themeResId: Int,
    val LayoutId: Int,
    mList: ArrayList<CategoryTab>,
    var mClick: ItemClick
) : BaseDialog(context, themeResId), ListDialogAdapter.AdapterClick {

    var mUserList = arrayListOf<CategoryTab>()
    var mUserListAdapter: ListDialogAdapter? = null

    init {
        val wmlp = this.window!!.attributes
        wmlp.gravity = Gravity.BOTTOM
        window!!.attributes = wmlp
        mUserList = mList
    }


    override fun onCreateStuff() {
        setCanceledOnTouchOutside(true)
        setUserListAdapter()
    }

    override fun initListeners() {


    }

    override val contentView: Int
        get() = LayoutId

    private fun setUserListAdapter() {
        mUserListAdapter = ListDialogAdapter(context, mUserList, this)
        rvList.layoutManager =
            LinearLayoutManager(context)
        rvList.adapter = mUserListAdapter
    }

    interface ItemClick {
        fun onItemClick(data: CategoryTab)
    }

    override fun onItemClick(data: CategoryTab) {
        mClick.onItemClick(data)
        dismissDialog()
    }


}
