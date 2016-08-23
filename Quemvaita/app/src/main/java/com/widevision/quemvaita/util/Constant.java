package com.widevision.quemvaita.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.joda.time.DateTime;


/**
 * Created by widevision on 20/01/16.
 */
public class Constant {

    public static final String CHAT_SERVER_URL = "http://192.168.0.206:3003";
    //public static final String CHAT_SERVER_URL = "http://103.231.44.154:3022/";

    public static String SET_PROFILE_URL = "http://103.231.44.154/newquemvaita/index.php?option=com_quemvaita&task=setprofile";
    public static String GET_PROFILE_URL = "http://103.231.44.154/newquemvaita/index.php?option=com_quemvaita&task=getprofile";
    public static String URL = "http://103.231.44.154/newquemvaita/index.php?option=com_quemvaita&task=";
    public static int mDeviceHeight;
    public static int mDeviceWidth;

    public static boolean buttonEnable = true;
    public static String datePattern = "yyyy-MM-dd";
    public static String dateTimePattern = "yyyy-MM-dd HH:mm";
    public static String datePattern_date = "MM/dd/yyyy";
    public static int mButtonTime = 500;
    public static String _id = "";
    public static String _name = "";
    public static String _email = "";
    public static String _gender = "";
    public static String _country = "";
    public static String _state = "";
    public static String _notify_prays = "";
    public static String _notify_comments = "";
    public static String _profile_pic = "";
    public static String _type = "0";
    public static String RECEIVER = "receiver";
    public static String SENDER = "sender";
    public static final String ACTION_RECEIVER = "com.widevision.quemvaita.action_receiver";
    public static final String ACTION_RECEIVER_FRAGMENT = "com.widevision.quemvaita.action_receiver_frag";
    public static final String ACTION_RECEIVER_LOCATION = "com.widevision.quemvaita.action_receiver_location";
    public static int width;
    public static int height;

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getMobileDateTime(String pattern) {
        DateTime dateTime = new DateTime();
        return dateTime.toString(pattern);
    }

    public static String getMobileDate() {
        DateTime dateTime = new DateTime();
        return dateTime.toString(datePattern);
    }

    public static String getMobileDate(String datePattern) {
        DateTime dateTime = new DateTime();
        return dateTime.toString(datePattern);
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getMobileTime() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTime dateTime = new DateTime();
        return dateTime.toString(pattern);
    }

    /*
     * getDeviceId() function Returns the unique device ID.
     * for example,the IMEI for GSM and the MEID or ESN for CDMA phones.
     */
    public static String getDevice_Id(Context context) {
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static void setButtonEnable() {
        buttonEnable = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonEnable = true;
            }
        }, mButtonTime);
    }

    public static void showAlert(View view, String msg) {
        final Snackbar snackbar;
        snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public static boolean isValidMobile(String phone) {
        if (phone.length() < 9 || phone.length() > 12) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }

    public static void disableAllViews(View view) {
        if ((view instanceof EditText)) {
            EditText e = (EditText) view;
            e.setEnabled(false);
            e.setFocusable(false);
            e.setClickable(false);
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int j = 0; j < ((ViewGroup) view).getChildCount(); j++) {
                View innerView = ((ViewGroup) view).getChildAt(j);
                disableAllViews(innerView);
            }
        }
    }

    public static void enableAllViews(View view) {
        if ((view instanceof EditText)) {
            EditText edit = (EditText) view;
            edit.setEnabled(true);
            edit.setFocusable(true);
            edit.setFocusableInTouchMode(true);
            edit.setClickable(true);
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int j = 0; j < ((ViewGroup) view).getChildCount(); j++) {
                View innerView = ((ViewGroup) view).getChildAt(j);
                enableAllViews(innerView);
            }
        }
    }


   /* public static void setAlert(Activity context, String msg) {
        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.setContentView(R.layout.alert_message_layout);
        dialog.setCancelable(false);
        TextView msgTxt = (TextView) dialog.findViewById(R.id.msgTxt);
        Button okBtn = (Button) dialog.findViewById(R.id.action_ok);
        msgTxt.setText(msg);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }*/


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
