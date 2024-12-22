package com.example.baseweather;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baseweather.entities.WeatherEntity;
import com.example.baseweather.util.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class SecondaryDisplayActivity extends AppCompatActivity {

    private Button selectCityBtn;
    private TextView cityText;
    private TextView weatherText;
    private TextView maxTempText;
    private TextView minTempText;
    private TextView humidityText;
    private TextView windPowerText;
    private SwitchCompat syncSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary_display);

        initView();
        onBroadcastReceive();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if(intent.getAction() == null || !intent.getAction().equals("select_city")){
            return;
        }

        SharedPreferences preferences = getSharedPreferences("weather", MODE_PRIVATE);
        syncSwitch.setChecked(preferences.getBoolean("syncState", false));

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject res = getLocalWeatherReport(intent.getStringExtra("id"));
                runOnUiThread(()->{
                    try {
                        String city = res.getString("city");
                        String weather = res.getString("weather");
                        String temperature = res.getString("temperature");
                        String humidity = res.getString("humidity");
                        String windpower = res.getString("windpower");

                        WeatherEntity weatherEntity = new WeatherEntity(city, weather, temperature, temperature, humidity, windpower);

                        setWeatherInfo(weatherEntity);

                        if(syncSwitch.isChecked()){
                            broadcastWeatherChange(weatherEntity);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }).start();
    }

    private void initView(){
        selectCityBtn = findViewById(R.id.switch_city_btn);
        selectCityBtn.setOnClickListener(v -> onSelectCityBtnClick());

        cityText = findViewById(R.id.city_name);
        weatherText = findViewById(R.id.weather);
        maxTempText = findViewById(R.id.max_temperature);
        minTempText = findViewById(R.id.low_temperature);
        humidityText = findViewById(R.id.humidity);
        windPowerText = findViewById(R.id.wind_speed);

        syncSwitch = findViewById(R.id.syncSwitch);
        syncSwitch.setOnCheckedChangeListener((view, isChecked) -> onSwitchChangedHandler(isChecked));
    }

    private void onSelectCityBtnClick() {
        Intent intent = new Intent(this, CityPickerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void onSwitchChangedHandler(Boolean isChecked){
        sendBroadcast(new Intent("change_sync").putExtra("sync", isChecked));
        SharedPreferences preferences = getSharedPreferences("weather", MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean("syncState", isChecked);
        edit.apply();
    }

    private JSONObject getLocalWeatherReport(String id){
        StringBuilder builder = new StringBuilder("https://restapi.amap.com/v3/weather/weatherInfo?");
        builder.append("key=3d7a236f46ee24a9fcc3665417bb324e");
        builder.append("&city=").append(id);

        try {
            String s = HttpHandler.requestGETMethod(builder.toString(), new HashMap<>(), null, false);
            JSONObject res = new JSONObject(s);
            JSONArray lives = res.getJSONArray("lives");
            return lives.getJSONObject(0);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setWeatherInfo(WeatherEntity entity){
        String city = entity.getName();
        String weather = entity.getWeather();
        String maxTemp = entity.getMaxTemp();
        String minTemp = entity.getMinTemp();
        String humidity = entity.getHumidity();
        String windPower = entity.getWindPower();

        cityText.setText(city);
        weatherText.setText("天气：" + weather);
        maxTempText.setText("最高：" + maxTemp + "°C");
        minTempText.setText("最低：" + minTemp + "°C");
        humidityText.setText("当前湿度：" + humidity + "%");
        windPowerText.setText("风速：" + windPower + "级");
    }

    private void broadcastWeatherChange(WeatherEntity entity){
        sendBroadcast(new Intent("change_weather").putExtras(entity.toBundle()));
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void onBroadcastReceive(){
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == null){
                    return;
                }

                switch (intent.getAction()){
                    case "change_weather":
                        handleChangeWeatherReceiver(intent);
                        break;

                    case "change_sync":
                        handleChangeSyncReceiver(intent);
                        break;
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("change_weather");
        filter.addAction("change_sync");
        registerReceiver(broadcastReceiver, filter);
    }

    private void handleChangeWeatherReceiver(Intent intent){
        Bundle bundle = intent.getExtras();

        String city = bundle.getString("name");
        String weather = bundle.getString("weather");
        String minTemp = bundle.getString("minTemp");
        String maxTemp = bundle.getString("maxTemp");
        String humidity = bundle.getString("humidity");
        String windPower = bundle.getString("windPower");

        WeatherEntity weatherEntity = new WeatherEntity(city, weather, minTemp, maxTemp, humidity, windPower);
        setWeatherInfo(weatherEntity);
    }

    private void handleChangeSyncReceiver(Intent intent){
        syncSwitch.setChecked(intent.getBooleanExtra("sync", false));
    }
}