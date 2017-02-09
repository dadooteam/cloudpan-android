package com.project_test.forsix;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.project_test.forsix.UploadRelated.UploadBean;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by kun on 2016/12/28.
 */

public class LocalFileAdapter extends BaseAdapter {
    ArrayList<File> filedata;
    Context context;

    public LocalFileAdapter(Context context, ArrayList<File> data) {
        this.context = context;
        this.filedata = data;
        fileItemListener = new FileListItemListender();
    }

    public File[] setfiledata(ArrayList<File> data) {
        this.filedata = data;
//        sort();
        this.notifyDataSetChanged();
        File[] files = new File[filedata.size()];
        for (int i = 0; i < files.length; i++) {
            files[i] = filedata.get(i);
        }
        return files;
    }

    public File[] setfiledata() {
        File[] files = new File[filedata.size()];
        for (int i = 0; i < files.length; i++) {
            files[i] = filedata.get(i);
        }
        return files;
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filedata.size();
    }

    @Override
    public Object getItem(int position) {
        return filedata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * ibButton点击监听器
     */
    FileListItemListender fileItemListener;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File file = filedata.get(position);
        fileItemListener = new FileListItemListender();
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_file_cell, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (file.isDirectory()) {
            viewHolder.fileImage.setImageResource(R.drawable.folder);
            viewHolder.fileSize.setText("文件夹");
            viewHolder.filemore.setVisibility(View.GONE);
        } else {
            viewHolder.fileImage.setImageResource(R.drawable.file);
            viewHolder.fileSize.setText(generateSize(file));
            viewHolder.filemore.setVisibility(View.VISIBLE);
        }

        viewHolder.filemore.setTag(position);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        viewHolder.fileName.setText(file.getName());
        viewHolder.fileTime.setText(format.format(new Date(file.lastModified())));
        viewHolder.filemore.setOnClickListener(fileItemListener);
        return convertView;
    }

    public static String generateSize(File file) {
        if (file.isFile()) {
            long result = file.length();
            long gb = 2 << 29;
            long mb = 2 << 19;
            long kb = 2 << 9;
            if (result < kb) {
                return result + "B";
            } else if (result >= kb && result < mb) {
                return String.format("%.2fKB", result / (double) kb);
            } else if (result >= mb && result < gb) {
                return String.format("%.2fMB", result / (double) mb);
            } else if (result >= gb) {
                return String.format("%.2fGB", result / (double) gb);
            }
        }
        return null;
    }

    public static class ViewHolder {
        ImageView fileImage;
        TextView fileName;
        TextView fileSize;
        TextView fileTime;
        ImageButton filemore;

        public ViewHolder(View v) {
            fileImage = (ImageView) v.findViewById(R.id.file_image);
            fileName = (TextView) v.findViewById(R.id.file_name);
            fileSize = (TextView) v.findViewById(R.id.file_size);
            fileTime = (TextView) v.findViewById(R.id.file_time);
            filemore = (ImageButton) v.findViewById(R.id.file_more);
        }
    }

    public class FileListItemListender implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        Integer position;

        @Override
        public void onClick(final View v) {
            position = (Integer) v.getTag();
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.file_list_popup_menu);
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    RotateAnimation rotateAnimation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(200);
                    rotateAnimation.setFillAfter(true);
                    v.startAnimation(rotateAnimation);
                }
            });
            popupMenu.setOnMenuItemClickListener(this);
            if (filedata.get(position).isDirectory()) {
                popupMenu.getMenu().findItem(R.id.more_upload).setVisible(false);
            } else {
                popupMenu.getMenu().findItem(R.id.more_upload).setVisible(true);

            }
            RotateAnimation rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            v.startAnimation(rotateAnimation);
            popupMenu.show();

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.more_upload:
                    doUpload();
                    break;
                default:
                    break;
            }
            return true;
        }

        private void doUpload() {
            File file = filedata.get(position);
            String fileName = file.getAbsolutePath();
            ArrayList<UploadBean> tmp = UserInfo.getInstance().getFilesToUpload();
            if (tmp.size() <= 4) {
                boolean addAvailable=true;//true是可以添加
                for (UploadBean bean:tmp){
                    if (bean.getFileName().equals(fileName)){
                        addAvailable=false;
                    }
                }
                if (addAvailable) {
                    tmp.add(new UploadBean(0,0,fileName));
                } else {
                    Toast.makeText(context, "文件已存在于上传列表中", Toast.LENGTH_SHORT).show();
                }
                if (UploadActivity.instance.uploadAdapter != null) {
                    UploadActivity.instance.uploadAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(context, "上传队列文件数量过多，请立即开始上传", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
