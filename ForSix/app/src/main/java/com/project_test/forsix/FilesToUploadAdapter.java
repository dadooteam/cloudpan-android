package com.project_test.forsix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;


/**
 * Created by kun on 2017/1/26.
 */
public class FilesToUploadAdapter extends BaseAdapter {
    private ArrayList<String> mFiles;
    private static Context mContext;


    public FilesToUploadAdapter() {
    }

    public FilesToUploadAdapter(Context context, ArrayList<String> files) {
        mContext = context;
        mFiles = files;
    }

    @Override
    public int getCount() {
        return mFiles == null ? 0 : mFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return mFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = convertView;
        ret = LayoutInflater.from(mContext).inflate(R.layout.file_to_upload_cell, parent, false);

        Holer holder = (Holer) ret.getTag();
        if (holder == null) {
            holder = new Holer(ret);
            ret.setTag(holder);
        }
        holder.bindView(position, mFiles.get(position));
        return ret;
    }

    private class Holer {
        private TextView fileName;
        private Button remove;


        public Holer(View v) {
            fileName = (TextView) v.findViewById(R.id.file_name);
            remove = (Button) v.findViewById(R.id.remove_from_list);
        }

        public void bindView(int position, final String name) {
            fileName.setText(name);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFiles.remove(name);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
