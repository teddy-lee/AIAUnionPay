package com.koolpos.cupinsurance.message.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

	public static final String PREFS_NAME = "reverse_preference_file";
	public static final String DATA_8583 = "data_8583";
	public static final String TRANS_TYPE = "trans_type";

	public static String getReverse8583(Context context) {
		SharedPreferences prefer = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_MULTI_PROCESS);
		return prefer.getString(DATA_8583, "");
	}

	public static void saveReverse8583(Context context, String terminalID) {
		SharedPreferences prefer = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_MULTI_PROCESS);
		prefer.edit().putString(DATA_8583, terminalID).commit();
	}

	public static String getOldTransType(Context context) {
		SharedPreferences prefer = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		return prefer.getString(TRANS_TYPE, "");
	}

	public static void saveOldTransType(Context context, String merchId) {
		SharedPreferences prefer = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		prefer.edit().putString(TRANS_TYPE, merchId).commit();
	}
	
	public static void clearPropertyBySharedPreferences(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
}
