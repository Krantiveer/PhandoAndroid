package com.perseverance.phando.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.perseverance.phando.BaseScreenTrackingActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.newgendroid.news.utils.AppDialogListener
import com.perseverance.phando.R
import com.perseverance.phando.Session
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.splash.BaseSplashActivity

/**
 * Created by TrilokiNath on 03-02-2016.
 */
object DialogUtils {
    fun showMessage(context: Context?, text: String?, duration: Int, finishOnDismiss: Boolean) {
        if (context == null || TextUtils.isEmpty(text)) return
        if (context is Activity) {
            if (!context.isFinishing) {
                getToastDialog(context, text, finishOnDismiss).show()
                return
            }
        } else {
            val toast = Toast.makeText(context, text, duration)
            val layout = LayoutInflater.from(context).inflate(R.layout.toast, null)
            val dm = DisplayMetrics()
            if (context is Activity) {
                context.windowManager.defaultDisplay.getMetrics(dm)
            } else if (context is FragmentActivity) {
                context.windowManager.defaultDisplay.getMetrics(dm)
            }
            (layout.findViewById<View>(R.id.toastMsg) as TextView).width = dm.widthPixels - 50
            (layout.findViewById<View>(R.id.toastMsg) as TextView).height = dm.heightPixels / 10
            (layout.findViewById<View>(R.id.toastMsg) as TextView).text = text
            toast.view = layout
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }
    }

    fun showMessage(context: Context?, text: String?, duration: Int, listener: DialogDismissListener) {
        if (context is Activity) {
            if (!context.isFinishing) {
                getToastDialog(context, text, listener).show()
                return
            }
        } else {
            val toast = Toast.makeText(context, text, duration)
            val layout = LayoutInflater.from(context).inflate(R.layout.toast, null)
            val dm = DisplayMetrics()
            if (context is Activity) {
                context.windowManager.defaultDisplay.getMetrics(dm)
            } else if (context is FragmentActivity) {
                context.windowManager.defaultDisplay.getMetrics(dm)
            }
            (layout.findViewById<View>(R.id.toastMsg) as TextView).width = dm.widthPixels - 50
            (layout.findViewById<View>(R.id.toastMsg) as TextView).height = dm.heightPixels / 10
            (layout.findViewById<View>(R.id.toastMsg) as TextView).text = text
            toast.view = layout
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }
    }

    fun getToastDialog(context: Context?, dialogTitle: String?, exit: Boolean): Dialog {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.toast)
        dialog.findViewById<View>(R.id.toastLayout).setOnClickListener { dialog.dismiss() }
        dialog.setOnDismissListener {
            if (context is Activity && exit) {
                context.finish()
            } else if (context is BaseSplashActivity) {
                context.finish()
            }
        }
        val dialogTitleTV = dialog.findViewById<View>(R.id.toastMsg) as TextView
        dialogTitleTV.text = dialogTitle
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    /*public static void showMessage(Context context, String text, int duration) {
        if (context instanceof Activity) {
            if (!((Activity) context).isFinishing()) {
                getToastDialog(context, text, false).show();
                return;
            }
        } else {
            Toast toast = Toast.makeText(context, text, duration);
            View layout = LayoutInflater.from(context).inflate(R.layout.toast, null);
            DisplayMetrics dm = new DisplayMetrics();
            if (context instanceof Activity) {
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            } else if (context instanceof FragmentActivity) {
                ((FragmentActivity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            }
            ((TextView) layout.findViewById(R.id.toastMsg)).setWidth(dm.widthPixels - 50);
            ((TextView) layout.findViewById(R.id.toastMsg)).setHeight(dm.heightPixels / 10);
            ((TextView) layout.findViewById(R.id.toastMsg)).setText(text);
            toast.setView(layout);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
    }*/
    fun getToastDialog(context: Context?, dialogTitle: String?, listener: DialogDismissListener): Dialog {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.toast)
        dialog.findViewById<View>(R.id.toastLayout).setOnClickListener { dialog.dismiss() }
        dialog.setOnDismissListener { listener.dismiss() }
        val dialogTitleTV = dialog.findViewById<View>(R.id.toastMsg) as TextView
        dialogTitleTV.text = dialogTitle
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    fun showListViewDialog(activity: Activity?, listener: BasicItemSelectedListener?, title: String?, items: List<BasicItem?>?, selectedItem: BasicItem?, type: Int) { //        final Dialog dialog = getBasicDialog(activity, title);
//        BasicRecyclerAdapter basicAdapter = new BasicRecyclerAdapter(activity, dialog, items, selectedItem, listener, type);
//        final RecyclerView listView = (RecyclerView) dialog.findViewById(R.id.dropDownListInfoType);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(listView.getContext());
//        listView.setLayoutManager(linearLayoutManager);
//        listView.setAdapter(basicAdapter);
//
//        if (activity != null && !activity.isFinishing() && !dialog.isShowing()) {
//            dialog.show();
//        }
//        final int selectedPosition = basicAdapter.getSelectedPosition();
//        if (selectedPosition >= 7) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if ((activity != null && !activity.isFinishing()) && dialog.isShowing()) {
//                        listView.smoothScrollToPosition(selectedPosition + 5);
//                    }
//                }
//            }, 500);
//        }
    }

    fun showAppCloseDialog(activity: Activity) {
        val alertDialog = MaterialAlertDialogBuilder(activity, R.style.AlertDialogTheme).create()
        alertDialog.setIcon(R.mipmap.ic_launcher)
        alertDialog.setTitle(activity.resources.getString(R.string.app_name))
        alertDialog.setMessage(activity.resources.getString(R.string.close_app_message))
        alertDialog.setCancelable(false)

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.resources.getString(R.string.close)
        ) { dialog, which ->
            // finish()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, activity.resources.getString(R.string.no)
        ) { dialog, which ->
        }
        if (!activity.isFinishing) {
            alertDialog.show()
        }

    }


    fun showDialog(activity: AppCompatActivity, title: String, message: String, positiveButtonTitle: String?, negativeButtonTitle: String?, listener: AppDialogListener?) {
        val alertDialog = MaterialAlertDialogBuilder(activity, R.style.AlertDialogTheme).create()
        alertDialog.setIcon(R.mipmap.ic_launcher)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setCancelable(false)

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveButtonTitle
        ) { dialog, which ->
            listener?.onPositiveButtonPressed()
            //  dialog.dismiss();
        }
        if (!TextUtils.isEmpty(negativeButtonTitle)) {
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeButtonTitle
            ) { dialog, which ->
                listener?.onNegativeButtonPressed()
                //dialog.dismiss();
            }
        }
        if (!activity.isFinishing) {
            alertDialog.show()
        }

    }

    fun showDialog(activity: AppCompatActivity, title: String, message: String, positiveButtonTitle: String?, negativeButtonTitle: String?, listener: AppDialogListener?, cancelable: Boolean) {
        val alertDialog = MaterialAlertDialogBuilder(activity, R.style.AlertDialogTheme).create()
        alertDialog.setIcon(R.mipmap.ic_launcher)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setCancelable(cancelable)

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveButtonTitle
        ) { dialog, which ->
            listener?.onPositiveButtonPressed()
            //  dialog.dismiss();
        }
        if (!TextUtils.isEmpty(negativeButtonTitle)) {
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeButtonTitle
            ) { dialog, which ->
                listener?.onNegativeButtonPressed()
                //dialog.dismiss();
            }
        }
        if (!activity.isFinishing) {
            alertDialog.show()
        }

    }

    fun showDialog(activity: Activity, listener: AppDialogListener?) {
        val alertDialog = MaterialAlertDialogBuilder(activity, R.style.AlertDialogTheme).create()
        alertDialog.setIcon(R.mipmap.ic_launcher)
        alertDialog.setTitle(activity.resources.getString(R.string.app_name))
        alertDialog.setMessage(activity.resources.getString(R.string.close_app_message))
        alertDialog.setCancelable(false)

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.resources.getString(R.string.close)
        ) { dialog, which ->
            listener?.onPositiveButtonPressed()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, activity.resources.getString(R.string.no)
        ) { dialog, which ->
            listener?.onNegativeButtonPressed()
        }
        if (!activity.isFinishing) {
            alertDialog.show()
        }

    }

    fun showDialog(activity: AppCompatActivity, listener: AppDialogListener?, view: View?) {
        val alertDialog = MaterialAlertDialogBuilder(activity, R.style.AlertDialogTheme).create()
        alertDialog.setIcon(R.mipmap.ic_launcher)
        // alertDialog.setTitle(activity.getResources().getString(R.string.app_name));
        if (view != null) {
            try {
                val parent = view.parent as ViewGroup
                parent.removeAllViews()
            } catch (e: Exception) {

            }

            alertDialog.setCustomTitle(view)
        }
        alertDialog.setMessage(activity.resources.getString(R.string.close_app_message))
        alertDialog.setCancelable(false)

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.resources.getString(R.string.close)
        ) { dialog, which ->
            listener?.onPositiveButtonPressed()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, activity.resources.getString(R.string.no)
        ) { dialog, which ->
            listener?.onNegativeButtonPressed()
        }
        if (!activity.isFinishing) {
            alertDialog.show()
        }

    }

    fun showNetworkErrorToast() {
        Toast.makeText(Session.instance, BaseConstants.NETWORK_ERROR, Toast.LENGTH_LONG).show()
    }


    interface DialogDismissListener {
        fun dismiss()
    }
}