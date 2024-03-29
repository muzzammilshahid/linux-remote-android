package com.example.mutecontrol;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ornach.nobobutton.NoboButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pk.codebase.requests.HttpRequest;

public class MainActivity extends AppCompatActivity {

    NoboButton muteMicBtn, unmuteMicBtn, showScreen, lock, mute;
    ProgressDialog progressDialog;
    String url, brightnessUrl;
    String link;
    SeekBar seekBar, seekBarSound, seekBarMic;
    TextView percent;
    ImageView muteImageView, micImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = "http://" + getIntent().getStringExtra("ip") + ":" + getIntent().getStringExtra("port") + "/api/";
        brightnessUrl = "http://" + getIntent().getStringExtra("ip") + ":8520/api/brightness";

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please connect with computer...");

        mute = findViewById(R.id.mutebtn);
        lock = findViewById(R.id.lock);
        muteMicBtn = findViewById(R.id.muteMicbtn);
        unmuteMicBtn = findViewById(R.id.unmuteMicbtn);
        seekBar = findViewById(R.id.seekBar);
        seekBarSound = findViewById(R.id.seekBarSound);
        seekBarMic = findViewById(R.id.seekBarMic);

        showScreen = findViewById(R.id.btn_showscreen);
        muteImageView = findViewById(R.id.mute_img);
        micImageView = findViewById(R.id.imageView_mic);

        percent = findViewById(R.id.percent);


        link = getIntent().getStringExtra("link");
        if (link != null) {
            openLink(link);
        }

        showScreen.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ImageFullscreen.class);
            intent.putExtra("url", url);
            startActivity(intent);
        });

        lock.setOnClickListener(v -> isLocked());

        isLocked1();

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


        mute.setOnClickListener(v -> {
            isMute();
            if (mute.getText().trim().equals("Mute")) {
                setMute();
            } else {
                setUnMute();
            }
        });

        muteMicBtn.setOnClickListener(v -> setMicMute());

        unmuteMicBtn.setOnClickListener(v -> setMicUnMute());


        isMute();

        currentVolume();

        seekBarMic.setProgress(100);


        seekBarSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                setVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        seekBarMic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                setMicVolume(progress);
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

        request.get(url + "lock");
    }

    public void isLocked1() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            JSONObject state = response.toJSONObject();
            if (state.optBoolean("is_locked")) {
                lock.setText("Unlock Screen");
            } else {
                lock.setText("Lock Screen");
            }
        });
        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());

        request.get(url + "lock");
    }

    private void setLock() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
        });

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        Map<String, Integer> data = new HashMap<>();
        data.put("set_lock", 1);
        request.post(url + "lock", data);
    }

    private void setUnLock() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
        });

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        Map<String, Integer> data = new HashMap<>();
        data.put("set_lock", 0);
        request.post(url + "lock", data);
    }

    private void openLink(String link) {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> System.out.println(response.toJSONObject()));

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        Map<String, String> data = new HashMap<>();
        data.put("link", link);
        request.post(url + "open", data);
    }

    private void getBrightness() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> seekBar.setProgress(Integer.parseInt(response.text.trim())));

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        request.get(brightnessUrl);
    }

    private void setBrightness(int progress) {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> System.out.println("Brightness changes"));

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        Map<String, Integer> data = new HashMap<>();
        data.put("brightness", progress);
        request.post(brightnessUrl, data);
    }

    private void getBatteryInfo() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            System.out.println("This is" + response.toJSONObject());
            JSONObject responseJson = response.toJSONObject();
            try {
                percent.setText(responseJson.get("percentage").toString() + " %");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        request.get(url + "battery");

    }

    private void setMute() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            Glide.with(this).load(R.drawable.mute).into(muteImageView);
            mute.setText("Unmute");
        });

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        Map<String, Integer> data = new HashMap<>();
        data.put("set_mute", 1);
        request.post(url + "volume", data);
    }

    private void setUnMute() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            Glide.with(this).load(R.drawable.unmute).into(muteImageView);
            mute.setText("Mute");
        });

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        Map<String, Integer> data = new HashMap<>();
        data.put("set_mute", 0);
        request.post(url + "volume", data);
    }

    private void isMute() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            JSONObject state = response.toJSONObject();
            if (state.optBoolean("is_muted")) {

                Glide.with(this).load(R.drawable.mute).into(muteImageView);
                mute.setText("Unmute");
            } else {

                Glide.with(this).load(R.drawable.unmute).into(muteImageView);
                mute.setText("Mute");
            }
        });
        request.setOnErrorListener(error -> Toast.makeText(this, "Please connect with your computer", Toast.LENGTH_SHORT).show());

        request.get(url + "volume");
    }

    private void setVolume(int vol) {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> System.out.println("This" + response.toJSONObject()));
        request.setOnErrorListener(error -> Toast.makeText(this, "Please connect with your computer", Toast.LENGTH_SHORT).show());

        Map<String, Integer> data = new HashMap<>();
        data.put("volume", vol);
        request.post(url + "vol", data);
    }

    private void currentVolume() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            System.out.println("This" + response.text);
            seekBarSound.setProgress(Integer.parseInt(response.text.trim()));
        });
        request.setOnErrorListener(error -> Toast.makeText(this, "Please connect with your computer", Toast.LENGTH_SHORT).show());

        request.get(url + "vol");
    }


    private void setMicMute() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> Glide.with(this).load(R.drawable.mic_mute).into(micImageView));

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect with your computer", Toast.LENGTH_SHORT).show());
        Map<String, Integer> data = new HashMap<>();
        data.put("set_mute", 1);
        request.post(url + "mic/mute", data);
    }

    private void setMicUnMute() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> Glide.with(this).load(R.drawable.mic_up).into(micImageView));

        request.setOnErrorListener(error -> Toast.makeText(MainActivity.this, "Please connect with your computer", Toast.LENGTH_SHORT).show());
        Map<String, Integer> data = new HashMap<>();
        data.put("set_mute", 0);
        request.post(url + "mic/mute", data);
    }


    private void setMicVolume(int vol) {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> System.out.println("This" + response.toJSONObject()));
        request.setOnErrorListener(error -> Toast.makeText(this, "Please connect with your computer", Toast.LENGTH_SHORT).show());

        Map<String, Integer> data = new HashMap<>();
        data.put("volume", vol);
        request.post(url + "mic/vol", data);
    }


}
