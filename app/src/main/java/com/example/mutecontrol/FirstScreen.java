package com.example.mutecontrol;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pk.codebase.requests.HttpRequest;

public class FirstScreen extends AppCompatActivity implements ServiceFinder.ServiceListener,
        WiFi.StateListener, AdapterView.OnItemClickListener {

    private ServiceFinder mFinder;
    private WiFi mWiFi;
    private AlertDialog mDialog;
    private ListView mListView;
    private ArrayList<Service> arrayListService;
    private ServiceAdapter myAdapter;
    private ProgressDialog progressDialog;
    private int selectedPosition = -1;
    private boolean foreground = false;
    String sharedText;
    String deviceId;

    @SuppressLint("HardwareIds")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        mWiFi = new WiFi(getApplicationContext());
        mWiFi.addStateListener(this);
        mWiFi.trackState();
        mDialog = createDialog();
        mListView = findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);
        arrayListService = new ArrayList<>();
        myAdapter = new ServiceAdapter(this, arrayListService);
        mListView.setAdapter(myAdapter);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Looking for services...");
        progressDialog.show();
        mFinder = new ServiceFinder(getApplicationContext());
        mFinder.addServiceListener(this);
//        mFinder.discoverAll();
        mFinder.discover("_http._tcp.local.");

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);







        Intent intent1 = getIntent();
        String action = intent1.getAction();
        String type = intent1.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            sharedText = intent1.getStringExtra(Intent.EXTRA_TEXT);
            System.out.println("This iss" + sharedText);
        }
    }

    private AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);
        builder.setNegativeButton(R.string.ok, (dialog, id) -> finish());
        return builder.create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mWiFi.hasIP()) {
            mDialog.show();
        }
        foreground = true;
        selectedPosition = -1;
    }

    @Override
    protected void onPause() {
        super.onPause();
        foreground = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onFound(String type, Service service) {
        System.out.println("Found: " + type + ": " + service.getHostName()+" "+service.getHostIP());
        arrayListService.add(service);
        progressDialog.dismiss();
        if (foreground) {
            myAdapter.notifyDataSetChanged();
        }
        if (foreground) {
            myAdapter.notifyDataSetChanged();
        }
        if (selectedPosition != -1) {
            ArrayList<Service> serviceArrayList = arrayListService;
            Intent intent1 = new Intent("com.update");
            intent1.putExtra("items", serviceArrayList);
            sendBroadcast(intent1);
        }
    }

    @Override
    public void onLost(String type, String name) {
        Log.i("TAG", " on lost");
        ArrayList<Service> services = arrayListService;
        List<Service> toRemove = new ArrayList<>();
        for (Service service : services) {
            if (service.getHostName().equals(name)) {
                toRemove.add(service);
            }
        }
        for (Service service: toRemove) {
            services.remove(service);
        }
        myAdapter.notifyDataSetChanged();
        if (selectedPosition != -1) {
            ArrayList<Service> serviceArrayList = arrayListService;
            Intent intent1 = new Intent("com.update");
            intent1.putExtra("items", serviceArrayList);
            sendBroadcast(intent1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDestroy() {
        mFinder.removeServiceListener(this);
        mFinder.cleanup();
        super.onDestroy();
    }

    @Override
    public void onConnect(String ip) {
        mDialog.dismiss();
    }

    @Override
    public void onDisconnect() {
        mDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedPosition = position;
        ArrayList<Service> services = arrayListService;
        Service mdatamodels = services.get(position);
        String port = String.valueOf(mdatamodels.getPort());
        String ip = mdatamodels.getHostIP();
        checkDevice(ip, port);
     }

     public void getOtp(String ip, String port){
         HttpRequest request = new HttpRequest();
         request.setOnResponseListener(response -> Toast.makeText(this, "Check the OTP on your Computer", Toast.LENGTH_SHORT).show());

         request.setOnErrorListener(error -> Toast.makeText(FirstScreen.this, "Please Connect your computer", Toast.LENGTH_SHORT).show());
         request.get("http://" + ip + ":"+port+"/api/pair/");
     }

     public void checkOtp(String otp, String ip, String port, Dialog dialog){
         HttpRequest request1 = new HttpRequest();
         request1.setOnResponseListener(response1 -> {
             if (response1.text.trim().equals("true")){
                 checkDevice(ip, port);
                 dialog.dismiss();
             }
             else {
                 Toast.makeText(this, "OPT is correct", Toast.LENGTH_SHORT).show();
             }
         });

         request1.setOnErrorListener(error -> Toast.makeText(FirstScreen.this, "Please connect your computer", Toast.LENGTH_SHORT).show());
         Map<String, String> data1 = new HashMap<>();
         data1.put("device_id",deviceId);
         data1.put("otp", otp);
         request1.post("http://" + ip + ":"+port+"/api/pair/", data1);
     }



     public void checkDevice(String ip, String port){
         HttpRequest request = new HttpRequest();
         request.setOnResponseListener(response -> {
             if (response.text.trim().equals("true")){
                 Intent intent = new Intent(FirstScreen.this,MainActivity.class);
                 intent.putExtra("ip", ip);
                 intent.putExtra("port",port);
                 intent.putExtra("link",sharedText);
                 startActivity(intent);
             } else {
                 getOtp(ip,port);
                 Dialog dialog = new Dialog(this);
                 dialog.setContentView(R.layout.dialog_verify);
                 int width = WindowManager.LayoutParams.MATCH_PARENT;
                 int height = WindowManager.LayoutParams.WRAP_CONTENT;
                 dialog.getWindow().setLayout(width,height);
                 dialog.show();
                 EditText otp = dialog.findViewById(R.id.otp);
                 Button verify = dialog.findViewById(R.id.bt_verify);
                 verify.setOnClickListener(v -> checkOtp(otp.getText().toString().trim(),ip,port,dialog));
             }
         });

         request.setOnErrorListener(error -> Toast.makeText(FirstScreen.this, "Please connect to your computer", Toast.LENGTH_SHORT).show());
         Map<String, String> data = new HashMap<>();
         data.put("device_id",deviceId);
         request.post("http://" + ip + ":"+port+"/api/verify/", data);
     }
}