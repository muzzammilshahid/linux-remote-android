package com.example.mutecontrol;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.ornach.nobobutton.NoboButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pk.codebase.requests.HttpRequest;

public class MainActivity extends AppCompatActivity {

    NoboButton mute,screenShot,lock;
    ImageView imageView,imageViewScreenshot;
    ProgressBar pb;
    ProgressDialog progressDialog;
    String url;
    String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        url = "http://"+getIntent().getStringExtra("ip")+":"+getIntent().getStringExtra("port")+"/api/";

        link = getIntent().getStringExtra("link");
        if (link!=null){
            openLink(link);
        }

        mute = findViewById(R.id.mutebtn);
        imageView = findViewById(R.id.iv);
        pb = findViewById(R.id.pb);
        lock = findViewById(R.id.lock);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please connect with computer...");
        screenShot = findViewById(R.id.btn_screenshot);
        imageViewScreenshot = findViewById(R.id.iv_screenshot);
        imageViewScreenshot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,ImageFullscreen.class);
            startActivity(intent);
        });


        mute.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            if (mute.getText().trim().equals("Mute")){
                setMute();
            } else {
                setUnMute();
            }
        });

        lock.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            if (lock.getText().trim().equals("Lock Screen")){
                setLock();
            } else {
                setUnLock();
            }
        });


        screenShot.setOnClickListener(v -> Picasso.get()
                .load("https://blog.malwarebytes.com/wp-content/uploads/2017/07/shutterstock_328174601-900x506.jpg")
                .into(imageViewScreenshot));
    }

    private void isMute() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            JSONObject state = response.toJSONObject();
            progressDialog.dismiss();
            if (state.optBoolean("is_muted")) {
                Picasso.get().load(R.drawable.mute).into(imageView);
                mute.setText("Unmute");
            } else {
                Picasso.get().load(R.drawable.unmute).into(imageView);
                mute.setText("Mute");
            }
        });
        request.setOnErrorListener(error -> progressDialog.show());

        request.get(url+"volume");
    }

    private void setMute() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            System.out.println(response.toJSONObject());
            pb.setVisibility(View.INVISIBLE);
        });

        Map<String, Integer> data = new HashMap<>();
        data.put("set_mute", 1);
        request.post(url+"volume", data);
    }

    private void setUnMute() {
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            System.out.println(response.toJSONObject());
            pb.setVisibility(View.INVISIBLE);
        });

        Map<String, Integer> data = new HashMap<>();
        data.put("set_mute", 0);
        request.post(url+"volume", data);
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
            request.setOnResponseListener(response -> {
                System.out.println(response.toJSONObject());
            });

            Map<String, String> data = new HashMap<>();
            data.put("link", link);
            request.post(url+"open", data);
    }

    Runnable r2=new Runnable() {
        @Override
        public void run() {
            isMute();
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
