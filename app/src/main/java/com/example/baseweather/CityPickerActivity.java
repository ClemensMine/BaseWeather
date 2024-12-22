package com.example.baseweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baseweather.adapters.CityListAdapter;
import com.example.baseweather.entities.CityEntity;
import com.example.baseweather.entities.ProvinceEntity;
import com.example.baseweather.util.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CityPickerActivity extends AppCompatActivity {

    private ListView listView;
    private List<ProvinceEntity> provinceEntities = new ArrayList<>();
    private ArrayAdapter adapter;
    private Boolean onCityPickStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_picker);

        listView = findViewById(R.id.province_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 城镇选择
                if (parent.getItemAtPosition(position) instanceof CityEntity){
                    CityEntity item = (CityEntity) parent.getItemAtPosition(position);

                    Intent intent = new Intent(getBaseContext(), SecondaryDisplayActivity.class);
                    intent.setAction("select_city");
                    intent.putExtra("id",item.getId());
                    startActivity(intent);
                    finish();

                    return;
                }

                // 省份选择
                String name = (String) parent.getItemAtPosition(position);
                if(!onCityPickStatus){
                    setAdapterToCities(name);
                }else {
                    Intent intent = new Intent(getApplicationContext(), SecondaryDisplayActivity.class);
                    intent.setAction("select_city");
                    intent.putExtra("id", name);
                    startActivity(intent);
                    finish();
                }
                onCityPickStatus = true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getProvincesAndCities();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        setAdapterToProvinces();
    }

    /***
     * 设置adapter为省份
     */
    private void setAdapterToProvinces(){
        List<String> names = new ArrayList<>();
        provinceEntities.forEach(i -> names.add(i.getName()));
        adapter.clear();
        adapter.addAll(names);
        adapter.notifyDataSetChanged();
        setTitle("选择省份");
    }

    /**
     * 设置adapter为城市
     * @param name 身份名字
     */
    private void setAdapterToCities(String name){
        ProvinceEntity entity = provinceEntities.stream()
                .filter(i -> i.getName().equals(name))
                .collect(Collectors.toList())
                .get(0);

        listView.setAdapter(new CityListAdapter(this, entity.getCities()));

        setTitle("选择城市");
    }


    /**
     * 获取所有城市信息
     */
    private void getProvincesAndCities() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("Authorization", "Bearer " + getAccessToken());
        try {
            String res = HttpHandler.sendJsonPost(MainActivity.url + "/dev-api/common/province/list", stringStringHashMap, "{}", null, false);
            try {
                JSONObject data = new JSONObject(res);
                if(data.getInt("code") != 200){
                    Toast.makeText(CityPickerActivity.this, "请求城市信息失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONArray provinces = data.getJSONArray("data");
                // 获取所有省份
                for (int i = 0; i < provinces.length(); i++) {
                    JSONObject provinceJSON = provinces.getJSONObject(i);
                    ProvinceEntity provinceEntity = new ProvinceEntity(provinceJSON.getInt("id"), provinceJSON.getString("name"));
                    List<CityEntity> cities = new ArrayList<>();

                    // 存储所有城市
                    JSONArray citiesJSON = provinceJSON.getJSONArray("cityList");
                    for (int j = 0; j < citiesJSON.length(); j++) {
                        JSONObject city = citiesJSON.getJSONObject(j);
                        cities.add(new CityEntity(city.getString("id"), city.getString("name")));
                    }

                    provinceEntity.setCities(cities);
                    provinceEntities.add(provinceEntity);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * 获得登陆token
     * @return
     */
    private String getAccessToken(){
        SharedPreferences sharedPreferences = getSharedPreferences("weather", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token == null){
            throw new RuntimeException("用户未登录");
        }
        return token;
    }
}