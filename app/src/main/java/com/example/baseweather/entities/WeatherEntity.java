package com.example.baseweather.entities;

import android.os.Bundle;

public class WeatherEntity {
    private String name;
    private String weather;
    private String minTemp;
    private String maxTemp;
    private String humidity;
    private String windPower;

    public WeatherEntity() {
    }

    public WeatherEntity(String name, String weather, String minTemp, String maxTemp, String humidity, String windPower) {
        this.name = name;
        this.weather = weather;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.humidity = humidity;
        this.windPower = windPower;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindPower() {
        return windPower;
    }

    public void setWindPower(String windPower) {
        this.windPower = windPower;
    }

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("weather", weather);
        bundle.putString("minTemp", minTemp);
        bundle.putString("maxTemp", maxTemp);
        bundle.putString("humidity", humidity);
        bundle.putString("windPower", windPower);
        return bundle;
    }
}
