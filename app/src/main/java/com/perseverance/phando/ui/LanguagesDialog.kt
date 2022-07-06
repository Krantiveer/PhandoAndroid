package com.perseverance.phando.ui

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.perseverance.phando.db.Language
import com.perseverance.phando.dialogs.BaseDialog
import kotlinx.android.synthetic.main.dialog_language.*

class LanguagesDialog(
    context: Context,
    themeResId: Int,
    val LayoutId: Int,
    mList: ArrayList<Language>,
    var mClick: ItemClick,
) : BaseDialog(context, themeResId), LanguageListAdapter.AdapterClick {

    companion object {
        var isOpen = false
    }

    var mUserList = arrayListOf<Language>()
    var mSelectedList = arrayListOf<Language>()
    var mUserListAdapter: LanguageListAdapter? = null
    val languageId = StringBuilder()

    init {
        val wmlp = this.window!!.attributes
        wmlp.gravity = Gravity.BOTTOM
        window!!.attributes = wmlp
        mUserList = mList
    }

    override fun onCreateStuff() {
        isOpen = true
        setCanceledOnTouchOutside(false)
        setUserListAdapter()
        for (data in mUserList) {
            if (data.isSelected) {
                mSelectedList.add(data)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        isOpen = false
    }

    override fun initListeners() {
        btnSave.setOnClickListener {
            if (mSelectedList.size >= 1) {
                for (data in mSelectedList) {
                    if (languageId.isEmpty()) {
                        languageId.append(data.id)
                    } else {
                        languageId.append(",${data.id}")
                    }
                }
                mClick.onItemClick(languageId)
                dismissDialog()
            } else {
                Toast.makeText(mContext,
                    "Please select at least one language!!",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override val contentView: Int
        get() = LayoutId

    private fun setUserListAdapter() {
        mUserListAdapter = LanguageListAdapter(context, mUserList, this)
        rvList.layoutManager = GridLayoutManager(context, 2)
        rvList.adapter = mUserListAdapter
    }

    interface ItemClick {
        fun onItemClick(data: StringBuilder)
    }

    override fun onItemClick(data: Language) {
        if (data.isSelected) {
            mSelectedList.add(data)
        } else {
            mSelectedList.remove(data)
        }

    }


}