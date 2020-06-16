package com.perseverance.phando.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.utils.MyLog
import com.perseverance.phando.utils.SadhnaDBHelper
import com.perseverance.phando.utils.TrackingUtils
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*


class SearchActivity : AppCompatActivity(), FetchSuggestionsListener {


    private var adapter: SuggestionAdapter? = null
    private var searchView: SearchView? = null
    private val searchInputFilter: InputFilter? = null
    private var sadhnaDBHelper: SadhnaDBHelper? = null
    private var searchHistoryList: ArrayList<String>? = null

    override fun onCreate(arg0: Bundle?) {

        super.onCreate(arg0)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        title = ""

        sadhnaDBHelper = SadhnaDBHelper(this)

        searchHistoryList = ArrayList(sadhnaDBHelper!!.getSearchHistory(""))
        btnClearHistory.visibility = if (searchHistoryList!!.size > 0) View.VISIBLE else View.GONE
        adapter = SuggestionAdapter(this, searchHistoryList)
        listView!!.adapter = adapter
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { arg0, arg1, position, arg3 -> onQueryCompleted(adapter!!.getItem(position)) }
        TrackingUtils.sendScreenTracker( BaseConstants.SEARCH)
        btnClearHistory.setOnClickListener {
            sadhnaDBHelper!!.deleteSearchHistory()
            searchHistoryList!!.clear()
            searchHistoryList!!.addAll(sadhnaDBHelper!!.getSearchHistory(""))
            adapter!!.notifyDataSetChanged()
            btnClearHistory.visibility = if (searchHistoryList!!.size > 0) View.VISIBLE else View.GONE

        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView!!.onActionViewExpanded()
        searchView!!.setOnQueryTextListener(SuggestorQueryListener(this, this))
        searchView!!.queryHint = getString(R.string.action_search)
        searchView!!.maxWidth = Integer.MAX_VALUE
        searchView!!.isMeasureWithLargestChildEnabled = true
        if (intent.extras != null && intent.extras!!.containsKey(Key.SEARCH_QUERY)) {
            searchView!!.setQuery(intent.extras!!.getString(Key.SEARCH_QUERY), false)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /* @Override
    public void finish() {
        //super.finish();
        Utils.animateActivity(this, "zero");
    }
*/
    /*private void onOkButtonClick(String locality) {
        hideSoftKeyBoard();
        delay(200);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("key", locality);
        setResult(Activity.RESULT_OK, returnIntent);
        finishActivityTransition();
    }*/

    /*private void finishActivityTransition() {
        this.finish();
        this.overridePendingTransition(0, 0);
    }*/

    /*private void delay(int time) {
        //Inserting delay here
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }*/


    fun clearAutoSuggestor() {
        if (adapter != null) {
            adapter!!.clearData()
        }
    }

    override fun onFetchedSuggestor(result: SuggestionModel?) {
        if (null != result && result.suggestions.size > 0) {
            adapter!!.setmItems(result.suggestions)
        } else {
            clearAutoSuggestor()
        }
    }

    override fun onFetchedSuggestor(result: List<String>?) {
        if (null != result && result.size > 0) {
            adapter!!.setmItems(result)
        } else {
            clearAutoSuggestor()
        }
    }

    override fun onErrorFetchedSuggestor(errorMessage: String) {
        adapter!!.clearData()
    }

    override fun onQueryCompleted(query: String) {
        MyLog.e("Query: $query")
        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/
        searchView!!.setQuery(query, false)
        Utils.hideKeyboard(this)
        if ("" == query.trim { it <= ' ' }) {
            setResult(Activity.RESULT_CANCELED)
        } else {
            val intent = Intent()
            intent.putExtra(Key.SEARCH_QUERY, query)
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }

}
