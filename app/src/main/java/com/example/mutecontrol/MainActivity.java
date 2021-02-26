package com.example.mutecontrol;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ornach.nobobutton.NoboButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pk.codebase.requests.HttpRequest;

public class MainActivity extends AppCompatActivity {

    NoboButton soundControl, micBtn, showScreen, lock;
    ProgressBar pb;
    ProgressDialog progressDialog;
    String url, brightnessUrl;
    String link;
    SeekBar seekBar;
    TextView percent, plugin, timeRemaining, timeToFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = "http://" + getIntent().getStringExtra("ip") + ":" + getIntent().getStringExtra("port") + "/api/";
        brightnessUrl = "http://" + getIntent().getStringExtra("ip") + ":8520/api/brightness";

        pb = findViewById(R.id.pb);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please connect with computer...");

        soundControl = findViewById(R.id.sound_btn);
        micBtn = findViewById(R.id.mic_btn);
        lock = findViewById(R.id.lock);
        seekBar= findViewById(R.id.seekBar);
        showScreen = findViewById(R.id.btn_showscreen);

        percent = findViewById(R.id.percent);
        plugin = findViewById(R.id.plugin);
        timeRemaining = findViewById(R.id.time_remaining);
        timeToFull = findViewById(R.id.time_to_full);


        link = getIntent().getStringExtra("link");
        if (link != null) {
            openLink(link);
        }

        soundControl.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,SoundActivity.class);
            intent.putExtra("url",url);
            startActivity(intent);
        });

        micBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,MicActivity.class);
            intent.putExtra("url",url);
            startActivity(intent);
        });

        showScreen.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ImageFullscreen.class);
            intent.putExtra("url",url);
            startActivity(intent);
        });

        lock.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            isLocked();
        });

        getBrightness();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                setBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        getBatteryInfo();
    }


    private void isLocked() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            JSONObject state = response.toJSONObject();
            if (state.optBoolean("is_locked")) {
                setUnLock();
                lock.setText("Lock Screen");
            } else {
                setLock();
                lock.setText("Unlock Screen");
            }
        });
        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());

        request.get(url+"lock");
    }

    private void setLock() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> pb.setVisibility(View.INVISIBLE));

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        Map<String, Integer> data = new HashMap<>();
        data.put("set_lock", 1);
        request.post(url+"lock", data);
    }

    private void setUnLock() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> pb.setVisibility(View.INVISIBLE));

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        Map<String, Integer> data = new HashMap<>();
        data.put("set_lock", 0);
        request.post(url+"lock", data);
    }

    private void openLink(String link){
            HttpRequest request = new HttpRequest();
            request.setOnResponseListener(response -> System.out.println(response.toJSONObject()));

            request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
            Map<String, String> data = new HashMap<>();
            data.put("link", link);
            request.post(url+"open", data);
    }

    private void getBrightness(){
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> seekBar.setProgress(Integer.parseInt(response.text.trim())));

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        request.get(brightnessUrl);
    }

    private void setBrightness(int progress){
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> System.out.println("Brightness changes"));

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        Map<String,Integer> data = new HashMap<>();
        data.put("brightness", progress);
        request.post(brightnessUrl, data);
    }

    private void getBatteryInfo(){
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            System.out.println("This is"+ response.toJSONObject());
            JSONObject responseJson = response.toJSONObject();
            try {
                if (responseJson.get("plugin_info").equals(true)){
                    timeToFull.setText("Time to Full: ");
                }
                percent.setText(responseJson.get("percentage").toString());
                plugin.setText(responseJson.get("plugin_info").toString());
                timeRemaining.setText(responseJson.get("battery_time_left").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        request.get(url+"battery");

    }
}
