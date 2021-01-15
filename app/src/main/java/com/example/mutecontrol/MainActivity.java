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

import pk.codebase.requests.HttpRequest;
import pk.codebase.requests.HttpResponse;

public class MainActivity extends AppCompatActivity {

    NoboButton mute,screenShot,lock;
    ImageView imageView,imageViewScreenshot;
    ProgressBar pb;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("This"+getIntent().getStringExtra("ip"));
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
            requestsData1("http://"+getIntent().getStringExtra("ip")+":5009/api/volume/status");
        });

        lock.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            requestsData1("http://" + getIntent().getStringExtra("ip") + ":5009/api/lock");
        });

        check("http://"+getIntent().getStringExtra("ip")+":5009/api/volume/status");

        screenShot.setOnClickListener(v -> Picasso.get().load("https://blog.malwarebytes.com/wp-content/uploads/2017/07/shutterstock_328174601-900x506.jpg").into(imageViewScreenshot));
    }

    public void requestData(String url){
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            if (response.code == HttpResponse.HTTP_OK) {
                System.out.println("Success"+ response.text.trim());
                pb.setVisibility(View.INVISIBLE);
                if (url.equals("http://"+getIntent().getStringExtra("ip")+":5009/api/volume/unmute")){
                    Picasso.get().load(R.drawable.unmute).into(imageView);
                    pb.setVisibility(View.INVISIBLE);
                } else if (url.equals("http://"+getIntent().getStringExtra("ip")+":5009/api/volume/mute")){
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

    public void requestsData1(String url){
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            if (response.code == HttpResponse.HTTP_OK) {
                System.out.println(response.toJSONObject());
                if (url.equals("http://"+getIntent().getStringExtra("ip")+":5009/api/volume/status")) {
                    if (response.text.trim().equals("True")) {
                        requestData("http://" + getIntent().getStringExtra("ip") + ":5009/api/volume/unmute");
                    } else if (response.text.trim().equals("False")) {
                        requestData("http://" + getIntent().getStringExtra("ip") + ":5009/api/volume/mute");
                    }
                }
                else if (url.equals("http://" + getIntent().getStringExtra("ip") + ":5009/api/lock")){
                    if (response.text.trim().equals("False")){
                        requestData("http://" + getIntent().getStringExtra("ip") + ":5009/api/volume/lock");
                    } else if (response.text.trim().equals("True")){
                        requestData("http://" + getIntent().getStringExtra("ip") + ":5009/api/volume/unlock");
                    }
                }
            }
        });
        request.setOnErrorListener(error -> {
            System.out.println("Error");
            // There was an error, deal with it
        });
        request.get(url);
    }

    public void check(String url){
        HttpRequest request = new HttpRequest();
        request.setOnResponseListener(response -> {
            if (response.code == HttpResponse.HTTP_OK) {
                progressDialog.dismiss();
                System.out.println(response.toJSONObject());
                if (url.equals("http://"+getIntent().getStringExtra("ip")+":5009/api/volume/status")){
                    if (response.text.trim().equals("True")){
                        mute.setText("Unmute");
                        Picasso.get().load(R.drawable.mute).into(imageView);
                    } else if (response.text.trim().equals("False")){
                        mute.setText("Mute");
                        Picasso.get().load(R.drawable.unmute).into(imageView);
                    }
                } else if (url.equals("http://" + getIntent().getStringExtra("ip") + ":5009/api/lock")) {
                    if (response.text.trim().equals("False")){
                        lock.setText("Lock Screen");
                        pb.setVisibility(View.INVISIBLE);
                    } else if (response.text.trim().equals("True")){
                        lock.setText("Unlock Screen");
                        pb.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        request.setOnErrorListener(error -> {
            progressDialog.show();
            System.out.println("Error");
            // There was an error, deal with it
        });
        request.get(url);
    }

    Runnable r2=new Runnable() {
        @Override
        public void run() {
            check("http://"+getIntent().getStringExtra("ip")+":5009/api/volume/status");
            check("http://" + getIntent().getStringExtra("ip") + ":5009/api/lock");
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
