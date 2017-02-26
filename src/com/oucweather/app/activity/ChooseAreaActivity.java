package com.oucweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oucweather.R;
import com.oucweather.app.db.OucWeatherDB;
import com.oucweather.app.model.City;
import com.oucweather.app.model.County;
import com.oucweather.app.model.Province;
import com.oucweather.app.util.HttpCallbackListener;
import com.oucweather.app.util.HttpUtil;
import com.oucweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {
  public static final int LEVEL_PROVINCE=0;
  public static final int LEVEL_CITY=1;
  public static final int LEVEL_COUNTY=2;
  
  private ProgressDialog progressDialog;
  
  private TextView titleText;
  //为dataList建立适配器
  private ListView listView;
  private ArrayAdapter<String> adapter;
  private List<String> dataList =new ArrayList<String>();
  
  private OucWeatherDB oucWeatherDB;
  
  private List<Province> provinceList;//省列表
  private List<City>  cityList;//市列表
  private List<County> countyList;//县列表
  
  private Province selectedProvince;//当前选中的省份
  private City selectedCity;//当前选中的城市
  private int currentLevel;//当前选中的级别
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.choose_area);
	
	listView =(ListView)findViewById(R.id.list_view);
	titleText =(TextView)findViewById(R.id.title_text);
	adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
	listView.setAdapter(adapter);
	oucWeatherDB=OucWeatherDB.getInstance(this);
	listView.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(currentLevel==LEVEL_PROVINCE){
				selectedProvince=provinceList.get(position);
				queryCities();
			}else if(currentLevel==LEVEL_CITY){
			selectedCity=cityList.get(position);
			    queryCounties();
			}
			
		}
	});

    queryProvince();//加载省级数据
}
/**
 * 查询全国所有省份的信息,优先从数据库中查询,如果没有查询到再去服务器上查询
 */
private void queryProvince() {
provinceList =oucWeatherDB.loadProvinces();//从数据库中读取省级数据
if(provinceList.size()>0){//如读到数据
	dataList.clear();//清空集合
	for(Province province: provinceList){//遍历所有省级数据,并添加到datalist集合中
		dataList.add(province.getProvinceName());//此时适配器中的内容已发生变化
	}
	adapter.notifyDataSetChanged();//当适配器中的内容发生变化时,刷新每个item内容并显示到界面上
	listView.setSelection(0);//将列表移到指定的0位置
	titleText.setText("中国");//设置标题
	currentLevel =LEVEL_PROVINCE;//设置当前的数据等级
}else {
	queryFromServer(null,"province");//从数据库上查询数据
}
	
}
/**
 * 查询选中省内所有市的信息,优先从数据库中查询,如果没有查询到再去服务器上查询
 */
protected void queryCities() {
	cityList =oucWeatherDB.loadCities(selectedProvince.getId());//从数据库中读取市级数据
	if(cityList.size()>0){//如读到数据
		dataList.clear();//清空集合
		for(City  city: cityList){//遍历所有市级数据,并添加到datalist集合中
			dataList.add(city.getCityName());//此时适配器中的内容已发生变化
		}
		adapter.notifyDataSetChanged();//当适配器中的内容发生变化时,刷新每个item内容并显示到界面上
		listView.setSelection(0);//将列表移到指定的0位置
		titleText.setText(selectedProvince.getProvinceName());//设置标题
		currentLevel =LEVEL_CITY;//设置当前的数据等级
	}else {
		queryFromServer(selectedProvince.getProvinceCode(),"city");//从数据库上查询数据
	}
	
}
/**
 * 查询选中市内所有县的信息,优先从数据库中查询,如果没有查询到再去服务器上查询
 */
protected void queryCounties() {
	countyList =oucWeatherDB.loadCounties(selectedCity.getId());//从数据库中读取市级数据
	if(countyList.size()>0){//如读到数据
		dataList.clear();//清空集合
		for(County  county: countyList){//遍历所有市级数据,并添加到datalist集合中
			dataList.add(county.getCountyName());//此时适配器中的内容已发生变化
		}
		adapter.notifyDataSetChanged();//当适配器中的内容发生变化时,刷新每个item内容并显示到界面上
		listView.setSelection(0);//将列表移到指定的0位置
		titleText.setText(selectedCity.getCityName());//设置标题
		currentLevel =LEVEL_COUNTY;//设置当前的数据等级
	}else {
		queryFromServer(selectedCity.getCityCode(),"county");//从数据库上查询数据
	}
}
/**
 * 根据传入的代号和类型从服务器上查询省市县信息
 * @param code
 * @param type
 */
private void queryFromServer(final String code, final String type) {
	String address;
	if(!TextUtils.isEmpty(code)){
		address="http://www.weather.com.cn/data/list3/city"+code+".xml";
	}else{
		//address="http://www.baidu.com";
		address ="http://www.weather.com.cn/data/list3/city.xml";
	}
	showProgressDialog();//显示进度对话框
	//发送http网络请求
	HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
		
		@Override
		public void onFinish(String response) {
			boolean result =false;
			if("province".equals(type)){
				//解析和处理服务器返回的数据,并将数据存储到数据库中
			
				result=Utility.handleProvincesResponse(oucWeatherDB, response);
			}else if ("city".equals(type)){
				result=Utility.handleCitiesResponse(oucWeatherDB, response, selectedProvince.getId());
				
			}else if("county".equals(type)){
				result= Utility.handleCountiesResponse(oucWeatherDB, response, selectedCity.getId());
			}
			if(result){
				//通过runOnUIThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						if("province".equals(type)){
							queryProvince();//重新查询数据
						}else if("city".equals(type)){
							queryCities();
						}else if("county".equals(type)){
							queryCounties();
						}
					}

					
				});
			}
			
		}
		
	@Override
		public void onError(Exception e) {
			//通过runOnUIThread()方法回到主线程处理逻辑
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
				closeProgressDialog();
				Toast.makeText(ChooseAreaActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
					
				}
			});
		}
	});
}
/**
 * 显示进度对话框
 */
private void showProgressDialog() {
if(progressDialog==null){
	progressDialog =new ProgressDialog(this);
	progressDialog.setMessage("正在加载中.....");
	progressDialog.setCanceledOnTouchOutside(false);
}
	progressDialog.show();
}
  /**
   * 关闭进度对话框
   */
private void closeProgressDialog() {
	// TODO Auto-generated method stub
	if(progressDialog!=null){
		progressDialog.dismiss();
	}
}

/**
 * 捕获back健,根据当前的级别来判断,此时应该返回市列表、省列表,还是直接退出
 */
@Override
public void onBackPressed() {
	if(currentLevel==LEVEL_COUNTY){
				queryCities();
	}else if(currentLevel==LEVEL_CITY){
		queryProvince();
	}else{
		finish();
	}
} 




}
