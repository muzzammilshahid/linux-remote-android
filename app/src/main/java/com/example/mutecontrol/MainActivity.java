package com.example.mutecontrol;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import pk.codebase.requests.HttpRequest;
import pk.codebase.requests.HttpResponse;

public class MainActivity extends AppCompatActivity {

    MaterialButton mute,lock,unlock;
    ImageView imageView;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("This"+getIntent().getStringExtra("ip"));
        mute = findViewById(R.id.mutebtn);
        imageView = findViewById(R.id.iv);
        pb = findViewById(R.id.pb);
        lock = findViewById(R.id.lock);
        unlock = findViewById(R.id.unlock);

        mute.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            requestsData();
        });

        lock.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            requestData("http://" + getIntent().getStringExtra("ip") + ":5000/api/volume/lock");
        });

        unlock.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            requestData("http://" + getIntent().getStringExtra("ip") + ":5000/api/volume/unlock");
        });
        check();
    }

    public void requestData(String url){
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            if (response.code == HttpResponse.HTTP_OK) {
                System.out.println("Success"+ response.text.trim());
                pb.setVisibility(View.INVISIBLE);
                if (url.equals("http://"+getIntent().getStringExtra("ip")+":5000/api/volume/unmute")){
                    Picasso.get().load(R.drawable.unmute).into(imageView);
                    pb.setVisibility(View.INVISIBLE);
                } else if (url.equals("http://"+getIntent().getStringExtra("ip")+":5000/api/volume/mute")){
                    Picasso.get().load(R.drawable.mute).into(imageView);
                    pb.setVisibility(View.INVISIBLE);
                }
            }
        });
        request.setOnErrorListener(error -> {
            pb.setVisibility(View.INVISIBLE);
            System.out.println("Error");
            error.printStackTrace();
            // There was an error, deal with it
        });
        request.get(url);
    }

    public void requestsData(){
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            if (response.code == HttpResponse.HTTP_OK) {
                System.out.println(response.toJSONObject());
                if (response.text.trim().equals("True")){
                    requestData("http://"+getIntent().getStringExtra("ip")+":5000/api/volume/unmute");
                    mute.setText(R.string.mute);
                } else if (response.text.trim().equals("False")){
                    requestData("http://"+getIntent().getStringExtra("ip")+":5000/api/volume/mute");
                    mute.setText(R.string.unmute);
                }
            }
        });
        request.setOnErrorListener(error -> {
            // There was an error, deal with it
        });
        request.get("http://"+getIntent().getStringExtra("ip")+":5000/api/volume/status");
    }

    public void check(){
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            if (response.code == HttpResponse.HTTP_OK) {
                System.out.println(response.toJSONObject());
                if (response.text.trim().equals("True")){
                    mute.setText(R.string.unmute);
                    Picasso.get().load(R.drawable.mute).into(imageView);
                } else if (response.text.trim().equals("False")){
                    mute.setText(R.string.mute);
                    Picasso.get().load(R.drawable.unmute).into(imageView);
                }
            }
        });
        request.setOnErrorListener(error -> {
            // There was an error, deal with it
        });
        request.get("http://"+getIntent().getStringExtra("ip")+":5000/api/volume/status");
    }

    Runnable r2=new Runnable() {
        @Override
        public void run() {
            check();
            h2.postDelayed(r2,500);
        }
    };
    Handler h2=new Handler();

    @Override
    protected void onResume() {
        super.onResume();
        h2.postDelayed(r2,500);
    }

    @Override
    protected void onPause() {
        h2.removeCallbacks(r2);
        super.onPause();
    }
}