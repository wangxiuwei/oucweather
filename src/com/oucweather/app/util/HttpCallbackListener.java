package com.oucweather.app.util;

public interface HttpCallbackListener {
	   public  void onFinish(String response);//服务器成功响应时调用
	      
	   public void onError(Exception e);//网络操作出现错误时调用
	}
