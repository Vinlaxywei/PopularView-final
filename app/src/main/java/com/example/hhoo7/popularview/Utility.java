package com.example.hhoo7.popularview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Utility{

    private static final String TAG = Utility.class.getSimpleName();

    //自定义函数，调用第三方库Picasso，用于解析图片，并加载到ImageView中
    public static void loadPicture(Context context, String posterUri, ImageView currentView) {
        Picasso.with(context).load(posterUri)
                //如果图片正在下载，将会显示这张图片
                .placeholder(R.drawable.im_loading)
                //如果图片下载失败，将会显示这张图片
                .error(R.drawable.im_error)
                .into(currentView);
    }

    // 获取用户设置的电影排序方式，并以字符串的方式返回
    public static String getModeFromPreference(Context context) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        return mPref.getString(context.getString(R.string.pref_movieSort_key),
                context.getString(R.string.pref_movieSort_defalutValue));
    }

    // 获取用户设置的电影海报的清晰度
    public static String getPosterSizePreference(Context context) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        return mPref.getString(context.getString(R.string.pref_posterSize_key),
                context.getString(R.string.pref_posterSize_defalutValue));
    }

    /*
    * 获取当前设备屏幕尺寸，进行像素值的转换
    * */
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static boolean checkIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;
        return isIntentSafe;
    }

    /*
    * @return 设备网络情况
    * */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
