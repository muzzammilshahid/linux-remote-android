package com.example.mutecontrol;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ornach.nobobutton.NoboButton;

public class ImageFullscreen extends AppCompatActivity {

    ImageView imageView;
    NoboButton refreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);
        imageView = findViewById(R.id.fullscreen_image);
        refreshBtn = findViewById(R.id.refresh_btn);

        Glide.with(this).
                load(getIntent().getStringExtra("url") + "screenshot")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);


        refreshBtn.setOnClickListener(v -> Glide.with(this).
                load(getIntent().getStringExtra("url") + "screenshot")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ImageFullscreen.this.finish();
    }
}