package com.koolpos.cupinsurance.message.iso8583;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.koolpos.cupinsurance.message.ISO8583Engine;
import com.koolpos.cupinsurance.message.control.CUP8583Controller;
import com.koolpos.cupinsurance.message.parameter.HostMessage;
import com.koolpos.cupinsurance.message.utils.Utility;

public class ISO8583Util {
	public static JSONObject convert8583(Context context, CUP8583Controller iso8583Controller, String data8583) {
		JSONObject data8583JsonObject = new JSONObject();
		try {

			Log.d("convert 8583", "load8583 data8583 : " + data8583);
			boolean load8583Result = iso8583Controller.load(Utility.hex2byte(data8583));
			Log.d("convert 8583", "load8583 load8583Result : " + load8583Result);
			
			String resCode = iso8583Controller.getResCode();
			Log.d("convert 8583", "load8583 resCode : " + resCode);
            String resMessage = HostMessage.getMessage(resCode);
			Log.d("convert 8583", "load8583 resMessage : " + resMessage);
			String queryResCode = iso8583Controller.getStatusResCode();
			data8583JsonObject.put("resCode", resCode);
			data8583JsonObject.put("resMsg", resMessage);
			data8583JsonObject.put("queryResCode", queryResCode);
			data8583JsonObject.put("refNo", iso8583Controller.getRRN());// 使用机构的参考号
			data8583JsonObject.put("paramDownloadFlag", iso8583Controller.getParamDownloadFlag());
			data8583JsonObject.put("paramsCapkDownloadNeed", iso8583Controller.getParamsCapkDownloadNeed());
			data8583JsonObject.put("paramsCapkCheckNeed", iso8583Controller.getIcParamsCapkCheckNeed());
			data8583JsonObject.put("apOrderId", iso8583Controller.getApOrderId());// 机构参考号
			data8583JsonObject.put("batchNo", iso8583Controller.getBatchNum());
			data8583JsonObject.put("transAmount", iso8583Controller.getTransAmount());
			data8583JsonObject.put("transTime", iso8583Controller.getTransTime());
			data8583JsonObject.put("paymentId", iso8583Controller.getPaymentId());
			data8583JsonObject.put("pan", iso8583Controller.getBankCardNum());// 卡号
			data8583JsonObject.put("iusserName", iso8583Controller.getIssuerName());
			data8583JsonObject.put("issuerId", iso8583Controller.getIssuerId());
			data8583JsonObject.put("dateExpr", iso8583Controller.getDateExpiry());
			data8583JsonObject.put("stlmDate", iso8583Controller.getSettlementTime());
			data8583JsonObject.put("authNo", iso8583Controller.getAuthCode());
			data8583JsonObject.put("transType", iso8583Controller.getApmpTransType());
			data8583JsonObject.put("traceNo", iso8583Controller.getTraceNum());
			iso8583Controller.setAuthCode("");
			Log.d("convert 8583", "converted 8583:" + data8583JsonObject.toString());
		} catch (Exception e) {
			try {
				data8583JsonObject.put("error", "ERROR");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			e.printStackTrace();
		}
		
		return data8583JsonObject;
	}
	
	public static String getDateRange(int dateRange) {
		String dateRangeStr = "";
		switch (dateRange) {
		case 0:
			dateRangeStr = "day";
			break;
		case 1:
			dateRangeStr = "week";
			break;
		case 2:
			dateRangeStr = "month";
			break;

		default:
			dateRangeStr = "day";
			break;
		}
		
		return dateRangeStr;
	}
}
