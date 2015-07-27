package com.koolpos.cupinsurance.message.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class MessageUtil {

	public static String getCardValidTime(String msg) {
		String str = null;
		String[] strs = msg.split("=");
		if (strs.length == 1) {
			str = strs[0];
		} else if (strs.length > 1) {
			if (strs[1].length() < 4) {
				str = strs[1];
			} else {
				str = strs[1].substring(0, 4);
			}
		}
		return str;
	}

	public static String getServicesCode(String msg) {
		String str = null;
		String[] strs = msg.split("=");
		if (strs.length == 1) {
			str = strs[0];
		} else if (strs.length > 1) {
			if (strs[1].length() < 7) {
				str = strs[1];
			} else {
				str = strs[1].substring(4, 7);
			}
		}
		return str;
	}

	public static JSONObject getTransactionFromPreference(Context context) {

		int traceNo = 0;
		int batchId = 0;

		JSONObject merchPreference = new JSONObject();
		Map<String, ?> map = UtilForDataStorage
				.readPropertyBySharedPreferences(context,
						UtilForDataStorage.TRANSACTION_PARAMS_PREFS_NAME);
		if (map == null) {
			try {
				merchPreference.put("batchId", 0);
				merchPreference.put("traceNo", 0);
				return merchPreference;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (null == map.get("traceNo")) {
			traceNo = 0;
		} else {
			traceNo = ((Integer) map.get("traceNo")).intValue();
		}

		if (null == map.get("batchId")) {
			batchId = 0;
		} else {
			batchId = ((Integer) map.get("batchId")).intValue();
		}

		if (traceNo > 999999) {
			traceNo = 0;
		}

		try {
			merchPreference.put("batchId", batchId);
			merchPreference.put("traceNo", traceNo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return merchPreference;
	}

	public static void calculateBatchTraceNo(Context context) {
		int traceNo, batchId;
		Map<String, ?> map = UtilForDataStorage
				.readPropertyBySharedPreferences(context,
						UtilForDataStorage.TRANSACTION_PARAMS_PREFS_NAME);
		if (null == map.get("traceNo")) {
			traceNo = 0;
		} else {
			traceNo = ((Integer) map.get("traceNo")).intValue();
		}

		if (null == map.get("batchId")) {
			batchId = 0;
		} else {
			batchId = ((Integer) map.get("batchId")).intValue();
		}

		if (traceNo > 999999) {
			traceNo = 0;
		}

		Map<String, Object> newMerchantMap = new HashMap<String, Object>();

		newMerchantMap.put("batchId", Integer.valueOf(batchId));
		newMerchantMap.put("traceNo", Integer.valueOf(++traceNo));
		UtilForDataStorage.savePropertyBySharedPreferences(context,
				UtilForDataStorage.TRANSACTION_PARAMS_PREFS_NAME, newMerchantMap);

	}

}
