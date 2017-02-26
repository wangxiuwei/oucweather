package com.oucweather.app.util;

import android.text.TextUtils;
import android.util.Log;

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
					//oucWeatherDB.saveProvince(province);
					Log.d("wangxiuei", province.getProvinceCode());
					Log.d("wangxiuei", province.getProvinceName());
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
	
}
