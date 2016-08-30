package com.example.hhoo7.popularview;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PublicMethod {

    private static final String TAG = PublicMethod.class.getSimpleName();


    //自定义函数，调用第三方库Picasso，用于解析图片，并加载到ImageView中
    public static void loadPicture(Context context, String posterUri, ImageView currentView) {
        Picasso.with(context).load(posterUri)
                //如果图片正在下载，将会显示这张图片
                .placeholder(R.drawable.im_loading)
                //如果图片下载失败，将会显示这张图片
                .error(R.drawable.im_error)
                .into(currentView);
    }

    //获取用户自定义的偏好
    public static String getModePreference(Context context) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        //获取电影清单类型
        return mPref.getString(context.getString(R.string.pref_movieSort_key),
                context.getString(R.string.pref_movieSort_defalutValue));
    }

    public static String getlanguagePreference(Context context) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        //获取电影清单类型
        return mPref.getString(context.getString(R.string.pref_language_key),
                context.getString(R.string.pref_language_defalutValue));
    }

}
