package com.example.skytoday;


import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationStorage {
    private static final String PREF_NAME = "LocationStorage";
    private static final String KEY_LOCATIONS = "locations";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public LocationStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void addLocation(String location) {
        List<String> locations = getLocations();
        if (locations == null) {
            locations = new ArrayList<>();
        }
        locations.add(location);
        saveLocations(locations);
    }

    public List<String> getLocations() {
        String locationsJson = sharedPreferences.getString(KEY_LOCATIONS, null);
        if (locationsJson != null) {
            try {
                JSONObject jsonObject = new JSONObject(locationsJson);
                List<String> locations = new ArrayList<>();
                for (int i = 0; i < jsonObject.length(); i++) {
                    String location = jsonObject.getString(String.valueOf(i));
                    locations.add(location);
                }
                return locations;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void saveLocations(List<String> locations) {
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < locations.size(); i++) {
            try {
                jsonObject.put(String.valueOf(i), locations.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        editor.putString(KEY_LOCATIONS, jsonObject.toString());
        editor.apply();
    }
}