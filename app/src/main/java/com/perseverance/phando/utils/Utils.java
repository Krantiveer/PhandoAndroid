package com.perseverance.phando.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.perseverance.phando.R;
import com.perseverance.phando.constants.BaseConstants;
import com.perseverance.phando.db.FavoriteVideo;
import com.perseverance.phando.db.Video;
import com.perseverance.phando.db.WatchLaterVideo;
import com.perseverance.phando.resize.ListItemThumbnail;
import com.perseverance.phando.resize.ThumbnailResizer;
import com.perseverance.phando.retrofit.NullResponseError;
import com.perseverance.phando.retrofit.ServerResponseError;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Years;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

/**
 * Created by TrilokiNath on 03-08-2016.
 */
public class Utils {


    public static String createHeaderThumbnailUrl(Video item) {

        String thumbNailUrl;
        if (BaseConstants.MEDIA_TYPE_M3U8.equals(item.getMediaType())) {
            thumbNailUrl = String.format("%s%s/m_%s_4.png", item.getCdnUrl(), item.getEntryId(), item.getEntryId());
            MyLog.e("Header thumbnail Url: (M3U8)" + thumbNailUrl);
            return thumbNailUrl;
        }

        thumbNailUrl = String.format("%sm_%s_4.png", item.getCdnUrl(), item.getEntryId());
        MyLog.e("Header thumbnail Url: (MP4)" + thumbNailUrl);
        return thumbNailUrl;
    }

    public static String createHeaderThumbnailUrl(FavoriteVideo item) {

        String thumbNailUrl;
        if (BaseConstants.MEDIA_TYPE_M3U8.equals(item.getMediaType())) {
            thumbNailUrl = String.format("%s%s/m_%s_4.png", item.getCdnUrl(), item.getEntryId(), item.getEntryId());
            MyLog.e("Header thumbnail Url: (M3U8)" + thumbNailUrl);
            return thumbNailUrl;
        }

        thumbNailUrl = String.format("%sm_%s_4.png", item.getCdnUrl(), item.getEntryId());
        MyLog.e("Header thumbnail Url: (MP4)" + thumbNailUrl);
        return thumbNailUrl;
    }

    public static String createHeaderThumbnailUrl(WatchLaterVideo item) {

        String thumbNailUrl;
        if (BaseConstants.MEDIA_TYPE_M3U8.equals(item.getMediaType())) {
            thumbNailUrl = String.format("%s%s/m_%s_4.png", item.getCdnUrl(), item.getEntryId(), item.getEntryId());
            MyLog.e("Header thumbnail Url: (M3U8)" + thumbNailUrl);
            return thumbNailUrl;
        }

        thumbNailUrl = String.format("%sm_%s_4.png", item.getCdnUrl(), item.getEntryId());
        MyLog.e("Header thumbnail Url: (MP4)" + thumbNailUrl);
        return thumbNailUrl;
    }

    public static void displayRoundedImage(final Context activity, final String url, final int placeHolder, final int errorPlaceholder, final ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholderOf(R.drawable.video_placeholder)
                .transforms(new CenterCrop(), new RoundedCorners(10));
        if (activity != null)
            Glide.with(activity)
                    .asBitmap()
                    .apply(requestOptions)
                    .apply(RequestOptions.placeholderOf(R.drawable.video_placeholder))
                    .load(url)
                    .thumbnail(.05f)
                    .into(imageView);
    }


    public static void hideKeyboard(Activity activity) {

        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }


    public static void showErrorToast(Context context, String text, int duration) {

        if (TextUtils.isEmpty(text)) return;

        Toast toast = Toast.makeText(context, text, duration);
        View layout = LayoutInflater.from(context).inflate(R.layout.toast_message, null);
        DisplayMetrics dm = new DisplayMetrics();
        if (context instanceof Activity) {
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        } else if (context instanceof FragmentActivity) {
            ((FragmentActivity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        }

        ((TextView) layout.findViewById(R.id.toastMsg)).setText(text);
        toast.setView(layout);
        toast.show();
        return;
    }

    public static void animateActivity(Activity activity, String action) {
        if (action.equalsIgnoreCase("next")) {
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (action.equalsIgnoreCase("back")) {
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (action.equalsIgnoreCase("up")) {
            activity.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        } else if (action.equalsIgnoreCase("down")) {
            //activity.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
            activity.overridePendingTransition(0, R.anim.push_down_out);
        } else if (action.equalsIgnoreCase("fadein")) {
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (action.equalsIgnoreCase("zero")) {
            activity.overridePendingTransition(R.anim.zero_duration, R.anim.zero_duration);
        }
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getProportionalHeight(Context context) {
        return (getScreenWidth(context) * 3) / 4;
    }

    public static int getSeriesBannerProportionalHeight(Context context) {
        return (int) (getScreenHeight(context) * 0.30);
    }

    public static int getProportionalBannerHeight(Activity activity) {
        return (getScreenWidth(activity) * 15) / 53;
    }

    public static void displayImage(final Context activity, final String url, final int placeHolder, final int errorPlaceholder, final ImageView imageView) {
        ThumbnailResizer thumbnailResizer = new ListItemThumbnail();
        double imageWidth = thumbnailResizer.getWidth() * 1.0;
        String imageUrl = "https://imstool.phando.com/?image_url=" + url + "&width=" + imageWidth + "&service=resize&aspect_ratio=true";
        MyLog.e("imageUrl", imageUrl);
        if (activity != null) {
            Glide.with(activity)
                    .setDefaultRequestOptions(new RequestOptions().timeout(30000))
                    .load(url)
                    .error(errorPlaceholder)
                    .placeholder(placeHolder)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);
        }
    }



    public static void displayCircularImage(final Context activity, final String url, final int placeHolder, final int errorPlaceholder, final ImageView imageView) {
        // String imageUrl1 = "https://phandoott.phando.com/images/movies/thumbnails/thumb_15710397243d-wallpaper-1.jpg";
        ThumbnailResizer thumbnailResizer = new ListItemThumbnail();
        double imageWidth = thumbnailResizer.getWidth() * 1.0;
        String imageUrl = "https://imstool.phando.com/?image_url=" + url + "&width=" + imageWidth + "&service=resize&aspect_ratio=true";
        // MyLog.e("imageUrl",imageUrl);
        if (activity != null) {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholderOf(placeHolder)
                    .apply(RequestOptions.circleCropTransform());
//            Glide.with(activity)
//                    .asBitmap()
//                    .apply(requestOptions)
//                    .apply(RequestOptions.placeholderOf(placeHolder))
//                    .apply(RequestOptions.errorOf(errorPlaceholder))
//                    .load(imageUrl)
//                    .transition(withCrossFade(factory))
//                    .into(imageView);

            Glide.with(activity)
                    .load(url)
                    .apply(requestOptions)
                    .error(errorPlaceholder)
                    .placeholder(placeHolder)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);
        }
    }

    public static void displayCircularProfileImage(final Context activity, final String url, final int placeHolder, final int errorPlaceholder, final ImageView imageView) {
        if (activity != null) {
            int cornerPx = (int) dpToPixel(activity, activity.getResources().getDimension(com.intuit.sdp.R.dimen._1sdp));
            DrawableCrossFadeFactory factory =
                    new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
//            CircularProgressDrawable circularProgressDrawable =new CircularProgressDrawable(activity);
//            circularProgressDrawable.setStrokeWidth( 5f);
//            circularProgressDrawable.setCenterRadius(30f);
//            circularProgressDrawable.start();

            RequestOptions requestOptions = new RequestOptions()
                    .placeholderOf(placeHolder)
                    // .transforms(new CenterCrop())
                    .apply(RequestOptions.circleCropTransform());
            Glide.with(activity)
                    .asBitmap()
                    .apply(requestOptions)
                    .apply(RequestOptions.placeholderOf(placeHolder))
                    .apply(RequestOptions.errorOf(errorPlaceholder))
                    .load(url)
                    .transition(withCrossFade(factory))
                    .into(imageView);
        }
    }




    public static String calculateAge(long timestamp) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        try {
            date = output.parse(String.valueOf(new Timestamp(timestamp * 1000)));
            //MyLog.e("Date: " + output.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(date);
        return calculateAge(cal);
    }

    public static String calculateAge(String publishedAt) {

        if (publishedAt == null) {
            return "";
        }

        //2016-08-04 15:31:11
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        try {
            date = output.parse(publishedAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cal.setTime(date);
        return calculateAge(cal);
    }

    public static String getScheduleDate(String publishedAt) {
        if (publishedAt == null) {
            return "";
        }
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = input.parse(publishedAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String convertedDate = output.format(date);
        return convertedDate;
    }

    public static String getScheduleTime(String publishedAt) {

        if (publishedAt == null) {
            return "";
        }
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        try {
            date = input.parse(publishedAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String convertedDate = output.format(date);
        return convertedDate;
    }

    public static String calculateAge(Calendar cal) {

        String age = "";
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);

        LocalDate birthdate = new LocalDate(year, month + 1, day);
        LocalDate now = new LocalDate();
        Years years = Years.yearsBetween(birthdate, now);
        Months months = Months.monthsBetween(birthdate, now);
        Days days = Days.daysBetween(birthdate, now);

        Hours hours = Hours.hoursBetween(birthdate, now);

        //MyLog.e("Hours: " + hours.getHours());
        Minutes minutes = Minutes.minutesBetween(birthdate, now);

        if (years.getYears() > 0) {
            if (years.getYears() == 1) {
                age = years.getYears() + " year ago";
            } else {
                age = years.getYears() + " years ago";
            }
        } else if (months.getMonths() > 0) {
            if (months.getMonths() == 1) {
                age = months.getMonths() + " month ago";
            } else {
                age = months.getMonths() + " months ago";
            }
        } else if (days.getDays() > 0) {
            if (days.getDays() == 1) {
                age = days.getDays() + " day ago";
            } else {
                age = days.getDays() + " days ago";
            }
        } else if (hours.getHours() > 0) {
            if (hours.getHours() == 1) {
                age = hours.getHours() + " hour ago";
            } else {
                age = hours.getHours() + " hours ago";
            }
        } else if (minutes.getMinutes() > 0) {
            if (minutes.getMinutes() == 1) {
                age = minutes.getMinutes() + " minute ago";
            } else {
                age = minutes.getMinutes() + " minutes ago";
            }
        } else {
            age = "Today";
        }

        return age;
    }

    public static boolean isNetworkAvailable(Context context) {

        if (null == context) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return null != activeNetwork;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getErrorMessage(Throwable error) {

        if (error == null) {
            return BaseConstants.APPLICATION_ERROR;
        } else if (error instanceof UnknownHostException) {
            return BaseConstants.NETWORK_ERROR;
        } else if (error instanceof ConnectException) {
            return BaseConstants.SERVER_CONNECTION_ERROR;
        } else if (error instanceof IllegalStateException
                || error instanceof JsonSyntaxException
                || error instanceof MalformedJsonException) {
            return BaseConstants.SERVER_ERROR_JSON_EXCEPTION;
        } else if (error instanceof NullResponseError) {
            return BaseConstants.SERVER_ERROR;
        } else if (error instanceof ServerResponseError) {
            return error.getMessage();
        } else if (error instanceof IOException && error.getMessage() != null
                && error.getMessage().toLowerCase().contains("canceled")) {
            //return BaseConstants.USER_CANCELLED_ERROR;

            // Removed crash while api call is in background and parent fragment get replaced
            return "";
        } else {
            return BaseConstants.SERVER_TIMEOUT_ERROR;
        }
    }

    /**
     * Change image color bitmap.
     *
     * @param sourceBitmap the source bitmap
     * @param color        the color
     * @return the bitmap
     */
    public static Bitmap changeImageColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }

    /**
     * Change image color bitmap.
     *
     * @param sourceBitmap the source bitmap
     * @param color        the color
     * @return the bitmap
     */
    public static Bitmap changeImageColor(Bitmap sourceBitmap, String color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(Color.parseColor(color), 1);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }

    /**
     * Convert drawable to bitmap bitmap.
     *
     * @param drawable the drawable
     * @return the bitmap
     */
    public static Bitmap convertDrawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
//    public static int getScreenWidthForHomeItem(Context context) {
//        int width  = context.getResources().getDisplayMetrics().widthPixels;
//        //return (int) (width * 0.28);
//        return (int) (width * 0.42);
//}
//
//    public static int getScreenHeightForHomeItem(int width) {
//        return (int) (width * 0.56);
//    }


    public static int getWidthForGridItem(Context context) {
        int width = context.getResources().getDisplayMetrics().widthPixels;
        return (int) (width * 0.45);
    }

    public static int getHeightForGridItem(Context context) {
        int height = getWidthForGridItem(context);
        return (int) (height * 0.3);
    }

    public static float dpToPixel(Context context, float someDpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        float value = someDpValue * density;
        return value;
    }


    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

//        if (tv.getTag() == null) {
//            tv.setTag(tv.getText());
//        }
        tv.setTag(tv.getText());
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                }

                if (tv.getLineCount() == 1) {
                    tv.setText(tv.getTag().toString());
                    return;
                }
                tv.setText(text);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                SpannableStringBuilder tempText = addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                        viewMore);
                tv.setText(tempText, TextView.BufferType.SPANNABLE);
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new MySpannable(true) {

                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "View Less", false);
                    } else {
                        makeTextViewResizable(tv, 3, "View More", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }


}
