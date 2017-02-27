package com.oucweather.app.activity;

import com.example.oucweather.R;
import com.oucweather.app.util.HttpCallbackListener;
import com.oucweather.app.util.HttpUtil;
import com.oucweather.app.util.Utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {
private LinearLayout weatherInfoLayout;
private TextView cityNameText;//显示城市名
private TextView publishText;//显示发布时间
private  TextView weatherDespText;//显示天气描述信息
private TextView temp1Text;//显示气温1
private TextView temp2Text;//显示气温2
private TextView currentDateText;//显示当前时间
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.weather_layout);
	//初始化各控件
	weatherInfoLayout =(LinearLayout)findViewById(R.id.weather_info_layout);
	cityNameText =(TextView)findViewById(R.id.city_name);
	publishText =(TextView)findViewById(R.id.publish_text);
	weatherDespText =(TextView)findViewById(R.id.weather_desp);
	temp1Text =(TextView)findViewById(R.id.temp1);
	temp2Text =(TextView)findViewById(R.id.temp2);
	currentDateText =(TextView)findViewById(R.id.current_data);
	String countyCode =getIntent().getStringExtra("county_code");//从Intent中取出县级代号
	if(!TextUtils.isEmpty(countyCode)){	//有县级代号就去查询天气
		publishText.setText("同步中.....");
		weatherInfoLayout.setVisibility(View.INVISIBLE);//控件可见
		cityNameText.setVisibility(View.INVISIBLE);
		queryWeatherCode(countyCode);//查询县级代号对应的天气代号
	
	}else{//没有县级代号时,就直接显示本机存储的天气
		showWeather();
		
	}
}

/**
 * 查询县级代号对应的天气代号
 * @param countyCode
 */
private void queryWeatherCode(String countyCode) {
	String address ="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
	queryFromServer(address,"countyCode");//从服务器查询天气代号
	
	
}

/**
 * 查询天气代号对应的天气信息
 * @param weatherCode
 */
protected void queryWeatherInfo(String weatherCode) {
	String address ="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
	queryFromServer(address, "weatherCode");
}

/**
 * 根据传入的地址和类型去向服务器查询天气代号或天气信息
 * @param address
 * @param string
 */
private void queryFromServer(final String address, final String type) {
	HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {//发送http请求
		
		@Override
		public void onFinish(String response) {
		if("countyCode".equals(type)){
			if(!TextUtils.isEmpty(response)){
				//从服务器返回的数据中解析出天气代号
				String [] array =response.split("\\|");
				if(array!=null&&array.length==2){
					String weatherCode =array[1];
					queryWeatherInfo(weatherCode);//查询天气信息
				}
				
			}
		}else if("weatherCode".equals(type)){
			//处理服务器返回的天气信息
			Utility.handleWeatherResponse(WeatherActivity.this, response);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					showWeather();//显示天气信息到界面上
				}
			});
		}
			
		}
		
		@Override
		public void onError(Exception e) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					publishText.setText("同步失败...");
				}
			});
		}
	});
	
}

/**
 * 从SharedPreferences文件中读取存储的天气信息,并显示到界面上
 */
private void showWeather() {
	// TODO Auto-generated method stub
	SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(this);
	try {
		cityNameText.setText(new String(prefs.getString("city_name", "").getBytes(),"utf-8"));//从文件中读取出来
		publishText.setText("今天"+new String(prefs.getString("publish_time", "").getBytes(),"utf-8")+"发布");//从文件中读取出来
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	temp1Text.setText(prefs.getString("temp1", ""));//从文件中读取出来
	temp2Text.setText(prefs.getString("temp2", ""));//从文件中读取出来
	weatherDespText.setText(prefs.getString("weather_desp", ""));//从文件中读取出来
	
	currentDateText.setText(prefs.getString("current_date", ""));//从文件中读取出来
	weatherInfoLayout.setVisibility(View.INVISIBLE);
	cityNameText.setVisibility(View.INVISIBLE);
}

}
