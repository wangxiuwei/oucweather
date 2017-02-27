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
private TextView cityNameText;//��ʾ������
private TextView publishText;//��ʾ����ʱ��
private  TextView weatherDespText;//��ʾ����������Ϣ
private TextView temp1Text;//��ʾ����1
private TextView temp2Text;//��ʾ����2
private TextView currentDateText;//��ʾ��ǰʱ��
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.weather_layout);
	//��ʼ�����ؼ�
	weatherInfoLayout =(LinearLayout)findViewById(R.id.weather_info_layout);
	cityNameText =(TextView)findViewById(R.id.city_name);
	publishText =(TextView)findViewById(R.id.publish_text);
	weatherDespText =(TextView)findViewById(R.id.weather_desp);
	temp1Text =(TextView)findViewById(R.id.temp1);
	temp2Text =(TextView)findViewById(R.id.temp2);
	currentDateText =(TextView)findViewById(R.id.current_data);
	String countyCode =getIntent().getStringExtra("county_code");//��Intent��ȡ���ؼ�����
	if(!TextUtils.isEmpty(countyCode)){	//���ؼ����ž�ȥ��ѯ����
		publishText.setText("ͬ����.....");
		weatherInfoLayout.setVisibility(View.INVISIBLE);//�ؼ��ɼ�
		cityNameText.setVisibility(View.INVISIBLE);
		queryWeatherCode(countyCode);//��ѯ�ؼ����Ŷ�Ӧ����������
	
	}else{//û���ؼ�����ʱ,��ֱ����ʾ�����洢������
		showWeather();
		
	}
}

/**
 * ��ѯ�ؼ����Ŷ�Ӧ����������
 * @param countyCode
 */
private void queryWeatherCode(String countyCode) {
	String address ="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
	queryFromServer(address,"countyCode");//�ӷ�������ѯ��������
	
	
}

/**
 * ��ѯ�������Ŷ�Ӧ��������Ϣ
 * @param weatherCode
 */
protected void queryWeatherInfo(String weatherCode) {
	String address ="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
	queryFromServer(address, "weatherCode");
}

/**
 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż�������Ϣ
 * @param address
 * @param string
 */
private void queryFromServer(final String address, final String type) {
	HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {//����http����
		
		@Override
		public void onFinish(String response) {
		if("countyCode".equals(type)){
			if(!TextUtils.isEmpty(response)){
				//�ӷ��������ص������н�������������
				String [] array =response.split("\\|");
				if(array!=null&&array.length==2){
					String weatherCode =array[1];
					queryWeatherInfo(weatherCode);//��ѯ������Ϣ
				}
				
			}
		}else if("weatherCode".equals(type)){
			//������������ص�������Ϣ
			Utility.handleWeatherResponse(WeatherActivity.this, response);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					showWeather();//��ʾ������Ϣ��������
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
					publishText.setText("ͬ��ʧ��...");
				}
			});
		}
	});
	
}

/**
 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ,����ʾ��������
 */
private void showWeather() {
	// TODO Auto-generated method stub
	SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(this);
	try {
		cityNameText.setText(new String(prefs.getString("city_name", "").getBytes(),"utf-8"));//���ļ��ж�ȡ����
		publishText.setText("����"+new String(prefs.getString("publish_time", "").getBytes(),"utf-8")+"����");//���ļ��ж�ȡ����
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	temp1Text.setText(prefs.getString("temp1", ""));//���ļ��ж�ȡ����
	temp2Text.setText(prefs.getString("temp2", ""));//���ļ��ж�ȡ����
	weatherDespText.setText(prefs.getString("weather_desp", ""));//���ļ��ж�ȡ����
	
	currentDateText.setText(prefs.getString("current_date", ""));//���ļ��ж�ȡ����
	weatherInfoLayout.setVisibility(View.INVISIBLE);
	cityNameText.setVisibility(View.INVISIBLE);
}

}
