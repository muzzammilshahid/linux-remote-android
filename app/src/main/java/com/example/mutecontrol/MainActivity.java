package com.example.mutecontrol;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.ornach.nobobutton.NoboButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pk.codebase.requests.HttpRequest;

public class MainActivity extends AppCompatActivity {

    NoboButton soundControl, micBtn, showScreen, lock;
    ProgressBar pb;
    ProgressDialog progressDialog;
    String url;
    String link;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = "http://" + getIntent().getStringExtra("ip") + ":" + getIntent().getStringExtra("port") + "/api/";

        pb = findViewById(R.id.pb);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please connect with computer...");

        soundControl = findViewById(R.id.sound_btn);
        micBtn = findViewById(R.id.mic_btn);
        lock = findViewById(R.id.lock);
        seekBar= findViewById(R.id.seekBar);
        showScreen = findViewById(R.id.btn_showscreen);

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
            if (lock.getText().trim().equals("Lock Screen")) {
                setLock();
            } else {
                setUnLock();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                String absd = "http://" + getIntent().getStringExtra("ip") + ":8520/api/brightnes/"+progress;

                HttpRequest request = new HttpRequest();
                request.setOnResponseListener(response -> {

                });

                request.get(absd);
                System.out.println("abcd"+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                System.out.println("abcd"+"Stopped");
            }
        });

    }


    private void isLocked() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            JSONObject state = response.toJSONObject();
            if (state.optBoolean("is_locked")) {
                lock.setText("Unlock Screen");
            } else {
                lock.setText("Lock Screen");
            }
        });

        request.get(url+"lock");
    }

    private void setLock() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            System.out.println(response.toJSONObject());
            pb.setVisibility(View.INVISIBLE);
        });

        Map<String, Integer> data = new HashMap<>();
        data.put("set_lock", 1);
        request.post(url+"lock", data);
    }

    private void setUnLock() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            System.out.println(response.toJSONObject());
            pb.setVisibility(View.INVISIBLE);
        });

        Map<String, Integer> data = new HashMap<>();
        data.put("set_lock", 0);
        request.post(url+"lock", data);
    }

    private void openLink(String link){
            System.out.println("This issssssss"+ link);
        System.out.println("This "+url+"open");
            HttpRequest request = new HttpRequest();
            request.setOnResponseListener(response -> System.out.println(response.toJSONObject()));

            Map<String, String> data = new HashMap<>();
            data.put("link", link);
            request.post(url+"open", data);
    }


    Runnable r2=new Runnable() {
        @Override
        public void run() {
            isLocked();
            h2.postDelayed(r2,100);
        }
    };
    Handler h2=new Handler();

    @Override
    protected void onResume() {
        super.onResume();
        h2.postDelayed(r2,100);
    }

    @Override
    protected void onPause() {
        h2.removeCallbacks(r2);
        super.onPause();
    }
}
