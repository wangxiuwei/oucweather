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
  //ΪdataList����������
  private ListView listView;
  private ArrayAdapter<String> adapter;
  private List<String> dataList =new ArrayList<String>();
  
  private OucWeatherDB oucWeatherDB;
  
  private List<Province> provinceList;//ʡ�б�
  private List<City>  cityList;//���б�
  private List<County> countyList;//���б�
  
  private Province selectedProvince;//��ǰѡ�е�ʡ��
  private City selectedCity;//��ǰѡ�еĳ���
  private int currentLevel;//��ǰѡ�еļ���
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

    queryProvince();//����ʡ������
}
/**
 * ��ѯȫ������ʡ�ݵ���Ϣ,���ȴ����ݿ��в�ѯ,���û�в�ѯ����ȥ�������ϲ�ѯ
 */
private void queryProvince() {
provinceList =oucWeatherDB.loadProvinces();//�����ݿ��ж�ȡʡ������
if(provinceList.size()>0){//���������
	dataList.clear();//��ռ���
	for(Province province: provinceList){//��������ʡ������,����ӵ�datalist������
		dataList.add(province.getProvinceName());//��ʱ�������е������ѷ����仯
	}
	adapter.notifyDataSetChanged();//���������е����ݷ����仯ʱ,ˢ��ÿ��item���ݲ���ʾ��������
	listView.setSelection(0);//���б��Ƶ�ָ����0λ��
	titleText.setText("�й�");//���ñ���
	currentLevel =LEVEL_PROVINCE;//���õ�ǰ�����ݵȼ�
}else {
	queryFromServer(null,"province");//�����ݿ��ϲ�ѯ����
}
	
}
/**
 * ��ѯѡ��ʡ�������е���Ϣ,���ȴ����ݿ��в�ѯ,���û�в�ѯ����ȥ�������ϲ�ѯ
 */
protected void queryCities() {
	cityList =oucWeatherDB.loadCities(selectedProvince.getId());//�����ݿ��ж�ȡ�м�����
	if(cityList.size()>0){//���������
		dataList.clear();//��ռ���
		for(City  city: cityList){//���������м�����,����ӵ�datalist������
			dataList.add(city.getCityName());//��ʱ�������е������ѷ����仯
		}
		adapter.notifyDataSetChanged();//���������е����ݷ����仯ʱ,ˢ��ÿ��item���ݲ���ʾ��������
		listView.setSelection(0);//���б��Ƶ�ָ����0λ��
		titleText.setText(selectedProvince.getProvinceName());//���ñ���
		currentLevel =LEVEL_CITY;//���õ�ǰ�����ݵȼ�
	}else {
		queryFromServer(selectedProvince.getProvinceCode(),"city");//�����ݿ��ϲ�ѯ����
	}
	
}
/**
 * ��ѯѡ�����������ص���Ϣ,���ȴ����ݿ��в�ѯ,���û�в�ѯ����ȥ�������ϲ�ѯ
 */
protected void queryCounties() {
	countyList =oucWeatherDB.loadCounties(selectedCity.getId());//�����ݿ��ж�ȡ�м�����
	if(countyList.size()>0){//���������
		dataList.clear();//��ռ���
		for(County  county: countyList){//���������м�����,����ӵ�datalist������
			dataList.add(county.getCountyName());//��ʱ�������е������ѷ����仯
		}
		adapter.notifyDataSetChanged();//���������е����ݷ����仯ʱ,ˢ��ÿ��item���ݲ���ʾ��������
		listView.setSelection(0);//���б��Ƶ�ָ����0λ��
		titleText.setText(selectedCity.getCityName());//���ñ���
		currentLevel =LEVEL_COUNTY;//���õ�ǰ�����ݵȼ�
	}else {
		queryFromServer(selectedCity.getCityCode(),"county");//�����ݿ��ϲ�ѯ����
	}
}
/**
 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ������Ϣ
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
	showProgressDialog();//��ʾ���ȶԻ���
	//����http��������
	HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
		
		@Override
		public void onFinish(String response) {
			boolean result =false;
			if("province".equals(type)){
				//�����ʹ�����������ص�����,�������ݴ洢�����ݿ���
			
				result=Utility.handleProvincesResponse(oucWeatherDB, response);
			}else if ("city".equals(type)){
				result=Utility.handleCitiesResponse(oucWeatherDB, response, selectedProvince.getId());
				
			}else if("county".equals(type)){
				result= Utility.handleCountiesResponse(oucWeatherDB, response, selectedCity.getId());
			}
			if(result){
				//ͨ��runOnUIThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						if("province".equals(type)){
							queryProvince();//���²�ѯ����
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
			//ͨ��runOnUIThread()�����ص����̴߳����߼�
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
				closeProgressDialog();
				Toast.makeText(ChooseAreaActivity.this, "���ݼ���ʧ��", Toast.LENGTH_SHORT).show();
					
				}
			});
		}
	});
}
/**
 * ��ʾ���ȶԻ���
 */
private void showProgressDialog() {
if(progressDialog==null){
	progressDialog =new ProgressDialog(this);
	progressDialog.setMessage("���ڼ�����.....");
	progressDialog.setCanceledOnTouchOutside(false);
}
	progressDialog.show();
}
  /**
   * �رս��ȶԻ���
   */
private void closeProgressDialog() {
	// TODO Auto-generated method stub
	if(progressDialog!=null){
		progressDialog.dismiss();
	}
}

/**
 * ����back��,���ݵ�ǰ�ļ������ж�,��ʱӦ�÷������б�ʡ�б�,����ֱ���˳�
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
