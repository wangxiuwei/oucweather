package com.oucweather.app.util;

public interface HttpCallbackListener {
	   public  void onFinish(String response);//�������ɹ���Ӧʱ����
	      
	   public void onError(Exception e);//����������ִ���ʱ����
	}
