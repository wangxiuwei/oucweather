package com.oucweather.app.util;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.StrictMode;


//�ṩHTTP��������Ĺ�����
public class HttpUtil {
	
	public static  void  sendHttpRequest(final String address,final HttpCallbackListener listener) {
		//ʹ��HttpURLConnection����HTTP����
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection =null;
				try {
					
					URL url =new URL(address);
					StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build()); 
					connection =(HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
				    connection.setReadTimeout(8000);
				
					InputStream in =connection.getInputStream();
				  BufferedReader reader =new BufferedReader(new InputStreamReader(in));//��ȡ������
				  StringBuffer response =new StringBuffer();
				  String line;
				  while((line=reader.readLine())!=null){
					  response.append(line);
				  }
			               if(listener!=null){
			            	   listener.onFinish(response.toString());//�ص�onfinish()����
			               }
				} catch (Exception e) {
					if(listener!=null){
						listener.onError(e);//�ص�onError����
					}

				}finally{
					if(connection!=null){
						connection.disconnect();//�ر�HTTP����
					}
				}
			}
				
			
		}).start();
				
		
			
		}
}

