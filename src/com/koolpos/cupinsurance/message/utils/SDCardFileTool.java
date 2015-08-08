package com.koolpos.cupinsurance.message.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Environment;

public class SDCardFileTool {
	public static void writePiciFile(String str) {
		FileWriter writer = null;
		try {
			File f = new File(Environment.getExternalStorageDirectory()+"/pici.bin");
			if(!f.exists()) {
				f.createNewFile();
			}
			
			writer = new FileWriter(f, false);
			writer.write(str);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static String getPiciContent() {
		FileInputStream fileIS = null;
		BufferedReader buf = null;
		try {
			File f = new File(Environment.getExternalStorageDirectory()+"/pici.bin");
			if(!f.exists()) {
				f.createNewFile();
			}
			fileIS = new FileInputStream(f.getAbsolutePath());
			buf = new BufferedReader(new InputStreamReader(fileIS));
			String readString = buf.readLine();
			
			return readString;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileIS.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void writePingzhengFile(String str) {
		FileWriter writer = null;
		try {
			File f = new File(Environment.getExternalStorageDirectory()+"/pingzheng.bin");
			if(!f.exists()) {
				f.createNewFile();
			}
			
			writer = new FileWriter(f, false);
			writer.write(str);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static String getPingzhengContent() {
		FileInputStream fileIS = null;
		BufferedReader buf = null;
		try {
			File f = new File(Environment.getExternalStorageDirectory()+"/pingzheng.bin");
			if(!f.exists()) {
				f.createNewFile();
			}
			fileIS = new FileInputStream(f.getAbsolutePath());
			buf = new BufferedReader(new InputStreamReader(fileIS));
			String readString = buf.readLine();
			
			return readString;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileIS.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
