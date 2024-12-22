package com.example.baseweather.adapters;

import android.content.Context;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.baseweather.R;
import com.example.baseweather.entities.CityEntity;

import java.util.List;

public class CityListAdapter extends BaseAdapter {

    private Context context;
    private List<CityEntity> cities;
    private LayoutInflater inflater;

    public CityListAdapter(Context context, List<CityEntity> cities) {
        this.context = context;
        this.cities = cities;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public Object getItem(int position) {
        return cities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = inflater.inflate(R.layout.listview_layout, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.listText);
        textView.setText(cities.get(position).getName());

        return convertView;
    }
}
