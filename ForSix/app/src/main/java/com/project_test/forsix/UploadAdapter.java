package com.project_test.forsix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.project_test.forsix.RetrofitBeans.FileListBean;
import com.project_test.forsix.UploadRelated.UploadBean;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.name;


/**
 * Created by kun on 2017/1/26.
 */
public class UploadAdapter extends BaseAdapter {
    private static Context mContext;
    private ArrayList<UploadBean> mFiles;

    public UploadAdapter() {
    }

    public UploadAdapter(Context context, ArrayList<UploadBean> files) {
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
        private ProgressBar bar;
        private TextView percentage;


        public Holer(View v) {
            fileName = (TextView) v.findViewById(R.id.file_name);
            remove = (Button) v.findViewById(R.id.remove_from_list);
            bar= (ProgressBar) v.findViewById(R.id.progressbar);
            percentage= (TextView) v.findViewById(R.id.percentage);

        }

        public void bindView(int position, final UploadBean bean) {
            fileName.setText(bean.getFileName());
            bar.setProgress(bean.getProgress());
            bar.setMax(100);
            percentage.setText(bean.getProgress()+"%");
            if (bean.getStatus()==0){
                bar.setVisibility(View.INVISIBLE);
                percentage.setVisibility(View.INVISIBLE);
                remove.setVisibility(View.VISIBLE);
            }else {
                bar.setVisibility(View.VISIBLE);
                percentage.setVisibility(View.VISIBLE);
                remove.setVisibility(View.INVISIBLE);
            }
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mFiles.size(); i++) {
                        if(mFiles.get(i).getFileName().equals(bean.getFileName())){
                            mFiles.remove(i);
                        }
                    }
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
