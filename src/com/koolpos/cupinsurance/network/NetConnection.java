package com.koolpos.cupinsurance.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.content.Context;

import com.koolpos.cupinsurance.message.utils.StringUtil;

public class NetConnection {

	public String socketConnect(Context context, String req8583) throws Exception {
		X509TrustManager x509m = new X509TrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
		};

		SSLSocket socket = null;
		try {
			
			String res8583 = "";
			// 创建SSLSocket
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { x509m }, new SecureRandom());

			SSLSocketFactory sslSocketFactory = (SSLSocketFactory) sslContext.getSocketFactory();

			socket = (SSLSocket) sslSocketFactory.createSocket("103.6.222.170", 33161);

			// 设置超时时间
			socket.setSoTimeout(60 * 1000);

			// 发送数据
			OutputStream out = socket.getOutputStream();
			out.write(StringUtil.hex2byte(req8583));
			out.flush();

			// 从服务器接收数据
			InputStream is = socket.getInputStream();
			ByteBuffer buff = ByteBuffer.allocate(512);

			int len = -1, size = 64, total = 0;
			byte[] bytes = new byte[size];

			while ((len = is.read(bytes)) != -1) {
				total += len;

				if (total > 1024 * 5) {
					throw new IllegalStateException("socket data too long.");
				}

				if (buff.limit() < total) {
					ByteBuffer newBuff = ByteBuffer.allocate(buff.limit() * 2);
					buff.flip();
					newBuff.put(buff);

					buff = newBuff;
				}

				buff.put(bytes, 0, len);

				if (len < size) {
					break;
				}
			}

			buff.flip();

			byte[] response = new byte[buff.limit()];
			buff.get(response);

			// 打印签到返回数据
			System.out.println("response data:" + StringUtil.byte2HexStr(response));
			res8583 = StringUtil.byte2HexStr(response);
			
			return res8583;
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
