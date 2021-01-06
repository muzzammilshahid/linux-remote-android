package com.example.mutecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageFullscreen extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);
        imageView = findViewById(R.id.fullscreen_image);
        Picasso.get().load("https://blog.malwarebytes.com/wp-content/uploads/2017/07/shutterstock_328174601-900x506.jpg").into(imageView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ImageFullscreen.this.finish();
    }
}