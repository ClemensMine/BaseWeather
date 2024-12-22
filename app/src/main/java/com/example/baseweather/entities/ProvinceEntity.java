package com.example.baseweather.entities;

import java.util.List;

public class ProvinceEntity {
    private int id;
    private String name;
    private List<CityEntity> cities;

    public ProvinceEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProvinceEntity(int id, String name, List<CityEntity> cities) {
        this.id = id;
        this.name = name;
        this.cities = cities;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityEntity> getCities() {
        return cities;
    }

    public void setCities(List<CityEntity> cities) {
        this.cities = cities;
    }
}
