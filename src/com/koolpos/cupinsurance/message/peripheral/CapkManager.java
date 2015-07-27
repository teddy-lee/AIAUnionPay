package com.koolpos.cupinsurance.message.peripheral;

import java.io.File;

import android.util.Log;
import cn.koolcloud.jni.EmvL2Interface;

import com.koolpos.cupinsurance.message.parameter.CapkParamsInfo;
import com.koolpos.cupinsurance.message.utils.StringUtil;

public class CapkManager {

	public static void deleteParamsFiles() {
		File file1 = new File("/mnt/sdcard/aid_param.ini");
		if (file1.exists()) {
			file1.delete();
		}
		File file2 = new File("/mnt/sdcard/ca_param.ini");
		if (file2.exists()) {
			file2.delete();
		}
	}

	public static Boolean paramsFilesIsExists() {
		Boolean existsTag = false;
		File file1 = new File("/mnt/sdcard/aid_param.ini");
		File file2 = new File("/mnt/sdcard/ca_param.ini");
		if (!file1.exists() || !file2.exists()) {
			file1.delete();
			file2.delete();
			existsTag = false;
		} else {
			existsTag = true;
		}
		return existsTag;
	}

	public static void updateParams() {
		int res = -1;
		// open kernel
		EMVICManager emvICm = EMVICManager.getEMVICManagerInstance();
		emvICm.downloadParamsInit();
		deleteParamsFiles();
		for (int i = 0; i < CapkParamsInfo.capk.length; i++) {
			byte[] capk = StringUtil.hexString2bytes(CapkParamsInfo.capk[i]);
			res = EmvL2Interface.downloadParam(capk, capk.length, 1);
			if (res >= 0) {
				Log.d("updateParams", "----------update capk is success!");
			} else {
				Log.d("updateParams", "----------update capk is failed!");
			}
		}
		for (int i = 0; i < CapkParamsInfo.aParams.length; i++) {
			byte[] aParams = StringUtil
					.hexString2bytes(CapkParamsInfo.aParams[i]);
			res = EmvL2Interface.downloadParam(aParams, aParams.length, 0);
			if (res >= 0) {
				Log.d("updateParams", "----------update params is success!");
			} else {
				Log.d("updateParams", "----------update params is failed!");
			}
		}
		res = EmvL2Interface.saveParam();
		if (res >= 0) {
			Log.d("updateParams", "----------saveParam params is success!");
		} else {
			Log.d("updateParams", "----------saveParam params is failed!");
		}
		emvICm.downloadParamsFinish();
	}

}
