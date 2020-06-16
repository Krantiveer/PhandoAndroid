package com.perseverance.phando.search;

import android.app.Activity;

import androidx.appcompat.widget.SearchView;

import com.perseverance.phando.utils.SadhnaDBHelper;

import java.util.Timer;
import java.util.TimerTask;

public class SuggestorQueryListener implements SearchView.OnQueryTextListener {

    private final long DELAY = 300;
    private Activity activity;
    private Timer timer = new Timer();
    private FetchSuggestionsListener listener;
    private int suggestor_text_length_limit = 1;
    private SadhnaDBHelper dbHelper;

    public SuggestorQueryListener(Activity activity, FetchSuggestionsListener listener) {

        this.activity = activity;
        this.listener = listener;
        dbHelper = new SadhnaDBHelper(activity);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        listener.onQueryCompleted(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {

        final Runnable getSuggestedKeywords = new Runnable() {
            public void run() {

                listener.onFetchedSuggestor(dbHelper.getSearchHistory(newText));
                /*if (newText.trim().length() >= suggestor_text_length_limit) {
                    String url = null;
                    try {
                        url = Config.getSuggestionUrl(newText);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Utils.showErrorToast(activity, Constants.ERROR_UNSUPPORTED_CHARACTER, Toast.LENGTH_SHORT);
                    }
                    GsonRequest<SuggestionModel> request =
                            new GsonRequest<SuggestionModel>(activity, url, null,
                                    SuggestorQueryListener.this, SuggestionModel.class);
                    VolleyManager.addToQueue(request, false);

                    // Fetch suggestion from history table

                } else {
                    ((SearchActivity) activity).clearAutoSuggestor();
                }*/
            }
        };

        TimerTask task = new TimerTask() {
            public void run() {
                activity.runOnUiThread(getSuggestedKeywords);
            }
        };

        timer.cancel();
        timer = new Timer();
        timer.schedule(task, DELAY);
        return true;
    }
}

