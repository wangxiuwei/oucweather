package com.oucweather.app.util;

import android.text.TextUtils;
import android.util.Log;

import com.oucweather.app.db.OucWeatherDB;
import com.oucweather.app.model.City;
import com.oucweather.app.model.County;
import com.oucweather.app.model.Province;

public class Utility {
/**
 * �����ʹ�����������ص�ʡ������
 */
	public synchronized static boolean handleProvincesResponse(OucWeatherDB oucWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			String [] allProvinces= response.split(",");//�����ŷָ��ַ���
			if(allProvinces!=null &&allProvinces.length>0){
				for(String p: allProvinces){
					String [] array= p.split("\\|");//��|�ŷָ��ַ���
					Province province =new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//�������������ݴ洢��Province��
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
	 * �����ʹ�����������ص��м�����
	 */
	public static boolean handleCitiesResponse(OucWeatherDB oucWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String [] allCities= response.split(",");//�����ŷָ��ַ���
			if(allCities!=null &&allCities.length>0){
				for(String p: allCities){
					String [] array= p.split("\\|");//��|�ŷָ��ַ���
					City city =new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//�������������ݴ洢��Province��
					oucWeatherDB.saveCity(city);
					
				}
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * �����ʹ�����������ص��ؼ�����
	 */
	public static boolean handleCountiesResponse(OucWeatherDB oucWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String [] allCounties = response.split(",");//�����ŷָ��ַ���
			if(allCounties!=null &&allCounties.length>0){
				for(String p: allCounties){
					String [] array= p.split("\\|");//��|�ŷָ��ַ���
					County county =new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//�������������ݴ洢��Province��
					oucWeatherDB.saveCounty(county);
					
				}
				return true;
			}
		}
		
		return false;
	}
	
}
