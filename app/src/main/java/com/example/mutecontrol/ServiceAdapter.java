package com.example.mutecontrol;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class ServiceAdapter extends ArrayAdapter<String> {

    private Activity mActivity;
    private ArrayList<Service> serviceArrayList;

    ServiceAdapter(Activity mActivity, ArrayList<Service> serviceArrayList) {
        super(mActivity.getApplicationContext(), R.layout.list_item);
        this.mActivity = mActivity;
        this.serviceArrayList = serviceArrayList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.list_item, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.textView = convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
            Log.i("TAG", " creating new");
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            Log.i("TAG", " using old one");
        }
        viewHolder.textView.setText(serviceArrayList.get(position).getHostName());

        convertView.setFocusable(false);
        return convertView;
    }

    @Override
    public int getCount() {
        return serviceArrayList.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return serviceArrayList.get(position).getHostName();
    }

    class ViewHolder {
        TextView textView;
    }
}
