package com.example.mutecontrol;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ornach.nobobutton.NoboButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pk.codebase.requests.HttpRequest;

public class SoundActivity extends AppCompatActivity {

    NoboButton mute;
    ImageView imageViewMuteStatus;
    ProgressBar pb;
    String url;
    SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        mute = findViewById(R.id.mutebtn);
        imageViewMuteStatus = findViewById(R.id.iv_mute_status);
        pb = findViewById(R.id.pb);
        seekBar= findViewById(R.id.seekBar);

        url = getIntent().getStringExtra("url");


        mute.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            isMute();
            if (mute.getText().trim().equals("Mute")) {
                setMute();
            } else {
                setUnMute();
            }
        });


        isMute();

        currentVolume();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
    }



    private void setMute() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            pb.setVisibility(View.INVISIBLE);
            Glide.with(this).load(R.drawable.mute).into(imageViewMuteStatus);
            mute.setText("Unmute");
        });

        request.setOnErrorListener(error -> Toast.makeText(SoundActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        Map<String, Integer> data = new HashMap<>();
        data.put("set_mute", 1);
        request.post(url+"volume", data);
    }

    private void setUnMute() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            Glide.with(this).load(R.drawable.unmute).into(imageViewMuteStatus);
            mute.setText("Mute");
            pb.setVisibility(View.INVISIBLE);
        });

        request.setOnErrorListener(error -> Toast.makeText(SoundActivity.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
        Map<String, Integer> data = new HashMap<>();
        data.put("set_mute", 0);
        request.post(url+"volume", data);
    }

    private void isMute() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            JSONObject state = response.toJSONObject();
            if (state.optBoolean("is_muted")) {
                Glide.with(this).load(R.drawable.mute).into(imageViewMuteStatus);
                mute.setText("Unmute");
            } else {
                Glide.with(this).load(R.drawable.unmute).into(imageViewMuteStatus);
                mute.setText("Mute");
            }
        });
        request.setOnErrorListener(error -> Toast.makeText(this, "Please connect with your computer", Toast.LENGTH_SHORT).show());

        request.get(url+"volume");
    }

    private void setVolume(int vol) {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> System.out.println("This"+response.toJSONObject()));
        request.setOnErrorListener(error -> Toast.makeText(this, "Please connect with your computer", Toast.LENGTH_SHORT).show());

        Map<String, Integer> data = new HashMap<>();
        data.put("volume", vol);
        request.post(url+"vol", data);
    }

    private void currentVolume(){
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            System.out.println("This" + response.text);
            seekBar.setProgress(Integer.parseInt(response.text.trim()));
        });
        request.setOnErrorListener(error -> Toast.makeText(this, "Please connect with your computer", Toast.LENGTH_SHORT).show());

        request.get(url+"vol");
    }
}