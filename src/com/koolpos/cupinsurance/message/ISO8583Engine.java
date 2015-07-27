package com.koolpos.cupinsurance.message;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.koolpos.cupinsurance.message.control.CUP8583Controller;
import com.koolpos.cupinsurance.message.iso8583.ISO8583Util;
import com.koolpos.cupinsurance.message.utils.MessageUtil;
import com.koolpos.cupinsurance.network.NetConnection;

public class ISO8583Engine {

	private static int traceNo = 0;
	private static int batchId = 0;

	private static ISO8583Engine instance = null;
	private static Context context;
	private static CUP8583Controller cup8583Controller;

	public static ISO8583Engine getInstance(Context ctx, String merchId, String terId) {
		if (instance == null) {
			instance = new ISO8583Engine();
		}

		context = ctx;
		if (cup8583Controller == null) {
			generateCUP8583Controller(merchId, terId);
		}
		return instance;

	}

	private static CUP8583Controller generateCUP8583Controller(String merchId, String terId) {
		MessageUtil.calculateBatchTraceNo(context);
		JSONObject merchObj = MessageUtil.getTransactionFromPreference(context);
		traceNo = merchObj.optInt("traceNo");
		batchId = merchObj.optInt("batchId");
		cup8583Controller = new CUP8583Controller(context, merchId, terId, traceNo, batchId);
		return cup8583Controller;
	}
	
	public JSONObject signIn() {
		JSONObject responseObj = null;
		cup8583Controller.signin();
		String str8583 = cup8583Controller.toString();
		NetConnection connect = new NetConnection();
		String response8583 = connect.socketConnect(str8583);
		if (!TextUtils.isEmpty(response8583)) {
			responseObj = ISO8583Util.convert8583(context, cup8583Controller, response8583);
		}
		
		return responseObj;
	}
	
	public JSONObject exeTransaction(JSONObject transObj) {
		JSONObject responseObj = null;
		cup8583Controller.purchase(transObj);
		String str8583 = cup8583Controller.toString();
		Log.w("request 8583", "request:" + str8583);
		NetConnection connect = new NetConnection();
		String response8583 = connect.socketConnect(str8583);
		if (!TextUtils.isEmpty(response8583)) {
			responseObj = ISO8583Util.convert8583(context, cup8583Controller, response8583);
		}
		
		return responseObj;
	}

}
