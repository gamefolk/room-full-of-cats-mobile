/*
 * AbsurdEngine (https://bitbucket.org/smpsnr/absurdengine/) 
 * (c) by Sam Posner (http://www.arcadeoftheabsurd.com/)
 *
 * AbsurdEngine is licensed under a
 * Creative Commons Attribution 4.0 International License
 *
 * You should have received a copy of the license along with this
 * work. If not, see http://creativecommons.org/licenses/by/4.0/ 
 */

package com.arcadeoftheabsurd.absurdengine;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;

/**
 * Provides device-independent network methods
 * @author sam
 */

public class WebUtils 
{	
	// downloads a file to the current process's private storage space
	public static String downloadFile(Context context, String fileUrl, String fileName) throws IOException {
		URL url = new URL(fileUrl);
		URLConnection conn = url.openConnection();
		conn.setUseCaches(false);
		InputStream httpIn = new BufferedInputStream(conn.getInputStream());
		
		ByteArrayOutputStream httpOut = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int byteIn = 0;
		
		while ((byteIn = httpIn.read(buffer)) != -1) {
			httpOut.write(buffer, 0, byteIn);
		}
		httpOut.close();
		httpIn.close();
		
		FileOutputStream fileOut = context.openFileOutput(fileName, 0);				
		fileOut.write(httpOut.toByteArray());	
		fileOut.close();
		
		String filePath = context.getFileStreamPath(fileName).getAbsolutePath();
		
		return filePath;
	}
	
	public static String restRequest(String request) throws IOException {
		URL url = new URL(request);
		URLConnection conn = url.openConnection();
		conn.setUseCaches(false);
		conn.setRequestProperty("User-Agent", DeviceUtility.getUserAgent());
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

		StringBuilder result = new StringBuilder();
		String line = "";
		while((line = in.readLine()) != null) {
			result.append(line);
		}
		in.close();
		return result.toString();
	}
}
