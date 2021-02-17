package com.example.mutecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ornach.nobobutton.NoboButton;

import java.util.HashMap;
import java.util.Map;

import pk.codebase.requests.HttpError;
import pk.codebase.requests.HttpRequest;

public class MicActivity extends AppCompatActivity {

    NoboButton mute, unmute;
    ImageView imageViewMuteStatus;
    ProgressBar pb;
    String url;
    SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic);

        mute = findViewById(R.id.mutebtn);
        unmute = findViewById(R.id.unmute_btn);
        imageViewMuteStatus = findViewById(R.id.iv_mute_status);
        pb = findViewById(R.id.pb);
        seekBar= findViewById(R.id.seekBar);
        url = getIntent().getStringExtra("url");


        mute.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
                setMute();
        });

        unmute.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            setUnMute();
        });


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
            Glide.with(this).load(R.drawable.mic_mute).into(imageViewMuteStatus);
        });

        request.setOnErrorListener(error -> Toast.makeText(MicActivity.this, "Please connect with your computer", Toast.LENGTH_SHORT).show());
        Map<String, Integer> data = new HashMap<>();
        data.put("set_mute", 1);
        request.post(url+"mic/mute", data);
    }

    private void setUnMute() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            Glide.with(this).load(R.drawable.mic_unmute).into(imageViewMuteStatus);
            pb.setVisibility(View.INVISIBLE);
        });

        request.setOnErrorListener(error -> Toast.makeText(MicActivity.this, "Please connect with your computer", Toast.LENGTH_SHORT).show());
        Map<String, Integer> data = new HashMap<>();
        data.put("set_mute", 0);
        request.post(url+"mic/mute", data);
    }


    private void setVolume(int vol) {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> System.out.println("This"+response.toJSONObject()));
        request.setOnErrorListener(error -> Toast.makeText(this, "Please connect with your computer", Toast.LENGTH_SHORT).show());

        Map<String, Integer> data = new HashMap<>();
        data.put("volume", vol);
        request.post(url+"mic/vol", data);
    }
}