package com.example.skytoday;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.skytoday.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    String apiKey = "0f30877e5af13f2595eb60fa756135d6";
    private EditText editText;
    TextView tempTV, tempTV1;
    TextView condTV, condTV1;
    TextView wind, wind1;
    TextView humid, humid1;
    String location;
    CardView c1, c2;
    TextView city, city1;;
    ImageView image1;
    String icon;
    int len;
    Button btn;
    private LocationStorage locationStorage;
    private ProgressBar loadingIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        editText=findViewById(R.id.locationEditText);
        tempTV=findViewById(R.id.temperatureTV);
        condTV=findViewById(R.id.conditionTV);
        condTV1=findViewById(R.id.conditionTV1);
        loadingIndicator=findViewById(R.id.progressBar);
        wind=findViewById(R.id.windTV);
        tempTV1=findViewById(R.id.temperatureTV1);
        city=findViewById(R.id.city);
        c1=findViewById(R.id.card);
        c2=findViewById(R.id.card1);
        city1=findViewById(R.id.city1);
        wind1=findViewById(R.id.windTV1);
        locationStorage = new LocationStorage(this);
        humid=findViewById(R.id.humidityTV);
        humid1=findViewById(R.id.humidityTV1);
        btn=findViewById(R.id.getWeatherButton);
        image1=findViewById(R.id.img1);
        autoUpdate(0);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location = editText.getText().toString().trim();
                locationStorage.addLocation(location);
                getWeatherData(apiKey,location, 1);
            }
        });
    }

    private void getWeatherData(String apiKey, String location, int flag) {
        loadingIndicator.setVisibility(View.VISIBLE);
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey;
        Gson gson = new Gson();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loadingIndicator.setVisibility(View.GONE);
                            Main main=gson.fromJson(response.get("main").toString(), Main.class);
                            int temperature=(int)(main.getTemp() - 273.15);
                            JSONArray jsonArray = new JSONArray(response.get("weather").toString());
                            JSONObject element = jsonArray.getJSONObject(0);
                            JSONObject element2 = (JSONObject) response.get("wind");
                            if(flag==1) {
                                city1.setText(response.get("name").toString());
                                tempTV.setText("Temperature : "+ temperature+" C");
                                icon=element.get("icon").toString();
                                wind.setText("Wind Speed : "+ element2.get("speed")+" kmph");
                                condTV.setText("Description : "+ element.getString("description").toUpperCase()+"");
                                humid.setText("Humidity : "+ main.getHumidity().toString() + " %RH");
                                Glide.with(getApplicationContext())
                                        .load("https://openweathermap.org/img/wn/"+icon+"@2x.png")
                                        .apply(RequestOptions.centerCropTransform())
                                        .into(new SimpleTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Drawable> transition) {
                                                c1.setBackground(resource);
                                            }
                                        });
                                autoUpdate(1);
                            }
                            else {
                                city.setText(response.get("name").toString());
                                tempTV1.setText("Temperature : "+ temperature+" C");
                                icon=element.get("icon").toString();
                                wind1.setText("Wind Speed : "+ element2.get("speed")+" kmph");
                                condTV1.setText("Description : "+ element.getString("description").toUpperCase()+"");
                                humid1.setText("Humidity : "+ main.getHumidity().toString() + " %RH");
                                Glide.with(getApplicationContext())
                                        .load("https://openweathermap.org/img/wn/"+icon+"@2x.png")
                                        .apply(RequestOptions.centerCropTransform())
                                        .into(new SimpleTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Drawable> transition) {
                                                c2.setBackground(resource);
                                            }
                                        });
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingIndicator.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }
    public void autoUpdate(int auto) {
        if (locationStorage != null) {
            List<String> locations = locationStorage.getLocations();
            if (locations != null && !locations.isEmpty()) {
                len = locations.size();
                if (auto == 0 && len > 0) {
                    getWeatherData(apiKey, locationStorage.getLocations().get(len - 1), 0);
                } else if (auto == 1 && len > 1) {
                    getWeatherData(apiKey, locationStorage.getLocations().get(len - 2), 0);
                }
            }
        }
    }
}