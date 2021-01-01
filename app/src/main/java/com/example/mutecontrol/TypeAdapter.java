package com.example.mutecontrol;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Map;

public class TypeAdapter extends ArrayAdapter<String> {

    private Activity mActivity;
    private Map<String, ArrayList<Service>> dataModels;

    TypeAdapter(Activity mActivity, Map<String, ArrayList<Service>> dataModels) {
        super(mActivity.getApplicationContext(), R.layout.types_group);
        this.mActivity = mActivity;
        this.dataModels = dataModels;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.types_group, parent,
                    false);
            viewHolder = new ViewHolder();
//            viewHolder.totalTypes = convertView.findViewById(R.id.type_total);
            viewHolder.type = convertView.findViewById(R.id.type);
            convertView.setTag(viewHolder);
            Log.i("TAG", " creating new");
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            Log.i("TAG", " using old one");
        }
//        Service mdatamodels = services.get(position);
//        viewHolder.totalTypes.setText("" + dataModels.get(getItem(position)).get(position).getPort());
        viewHolder.type.setText("" + dataModels.get(getItem(position)).get(position).getHostName());
        System.out.println("This"+dataModels);

        convertView.setFocusable(false);
        return convertView;
    }

    @Override
    public int getCount() {
        return dataModels.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return (String) dataModels.keySet().toArray()[position];
    }

    class ViewHolder{
        TextView type;
        TextView totalTypes;
    }
}
