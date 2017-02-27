package com.oucweather.app.util;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.StrictMode;


//提供HTTP网络请求的公共类
public class HttpUtil {
	
	public static  void  sendHttpRequest(final String address,final HttpCallbackListener listener) {
		//使用HttpURLConnection发送HTTP请求
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
				  BufferedReader reader =new BufferedReader(new InputStreamReader(in));//读取输入流
				  StringBuffer response =new StringBuffer();
				  String line;
				  while((line=reader.readLine())!=null){
					  response.append(line);
				  }
			               if(listener!=null){
			            	   listener.onFinish(response.toString());//回调onfinish()方法
			               }
				} catch (Exception e) {
					if(listener!=null){
						listener.onError(e);//回调onError方法
					}

				}finally{
					if(connection!=null){
						connection.disconnect();//关闭HTTP连接
					}
				}
			}
				
			
		}).start();
				
		
			
		}
}

