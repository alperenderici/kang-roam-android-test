package tr.com.akilliekip.kangapp.kang_roam_android_test.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class RoamPreferences {

    private static final String signIn = "signIn";
    private static final String userId = "userId";
    private static final String selfLogin = "selfLogin";

    private static SharedPreferences getInstance(Context context) {
        return context.getSharedPreferences("roam", Context.MODE_PRIVATE);
    }

    private static void setBoolean(Context context, String tagName, boolean value) {
        SharedPreferences.Editor mEditor = getInstance(context).edit();
        mEditor.putBoolean(tagName, value);
        mEditor.apply();
        mEditor.commit();
    }



    private static void setString(Context context, String tagName, String value) {
        SharedPreferences.Editor mEditor = getInstance(context).edit();
        mEditor.putString(tagName, value);
        mEditor.apply();
        mEditor.commit();
    }


    private static boolean getBoolean(Context context, String tagName) {
        return getInstance(context).getBoolean(tagName, false);
    }

    public static void setSignIn(Context context, boolean value) {
        RoamPreferences.setBoolean(context, signIn, value);
    }

    public static String getUserId(Context context, String tagName) {
        return getInstance(context).getString(tagName, userId);
    }

    public static void setUserId(Context context, String value) {
        RoamPreferences.setString(context, userId, value);
    }

    public static boolean isSignedIn(Context context) {
        return getBoolean(context, signIn);
    }

    public static void setSelfLogin(Context context, boolean value) {
        setBoolean(context, selfLogin, value);
    }

    public static boolean isSelfLoggedIn(Context context) {
        return getBoolean(context, selfLogin);
    }
}