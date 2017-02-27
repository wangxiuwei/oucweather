package com.oucweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.oucweather.app.db.OucWeatherDB;
import com.oucweather.app.model.City;
import com.oucweather.app.model.County;
import com.oucweather.app.model.Province;

public class Utility {
/**
 * 解析和处理服务器返回的省级数据
 */
	public synchronized static boolean handleProvincesResponse(OucWeatherDB oucWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			String [] allProvinces= response.split(",");//按逗号分隔字符串
			if(allProvinces!=null &&allProvinces.length>0){
				for(String p: allProvinces){
					String [] array= p.split("\\|");//按|号分隔字符串
					Province province =new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//将解析出的数据存储到Province表
					oucWeatherDB.saveProvince(province);
				
				}
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
	 */
	public static boolean handleCitiesResponse(OucWeatherDB oucWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String [] allCities= response.split(",");//按逗号分隔字符串
			if(allCities!=null &&allCities.length>0){
				for(String p: allCities){
					String [] array= p.split("\\|");//按|号分隔字符串
					City city =new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//将解析出的数据存储到Province表
					oucWeatherDB.saveCity(city);
					
				}
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的县级数据
	 */
	public static boolean handleCountiesResponse(OucWeatherDB oucWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String [] allCounties = response.split(",");//按逗号分隔字符串
			if(allCounties!=null &&allCounties.length>0){
				for(String p: allCounties){
					String [] array= p.split("\\|");//按|号分隔字符串
					County county =new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//将解析出的数据存储到Province表
					oucWeatherDB.saveCounty(county);
					
				}
				return true;
			}
		}
		
		return false;
	}
	/**
	 * 解析服务器返回的JSON数据,并将解析出的数据存储到本地
	 */
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherInfo");//获取名为"weatherInfo"的JSONObject对象
			String cityName =weatherInfo.getString("city");
			String weatherCode =weatherInfo.getString("cityid");
			String temp1 =weatherInfo.getString("temp1");
			String temp2 =weatherInfo.getString("temp2");
			String weatherDesp =weatherInfo.getString("weather");
			String publishTime =weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
			
		} catch (Exception e) {
		e.printStackTrace();
		}
	}
/**
 * 将服务器返回的所有天气信息存储到SharedPreferences文件中
 * @param context
 * @param cityName
 * @param weatherCode
 * @param temp1
 * @param temp2
 * @param weatherDesp
 * @param publishTime
 */
	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
	SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
	editor.putBoolean("city_selected", true);
	editor.putString("city_name", cityName);
	editor.putString("weather_code", weatherCode);
	editor.putString("temp1", temp1);
	editor.putString("temp2", temp2);
	editor.putString("weather_desp", weatherDesp);
	editor.putString("publish_time", publishTime);
	editor.putString("current_date", sdf.format(new Date()));
	editor.commit();
		
	}
}
