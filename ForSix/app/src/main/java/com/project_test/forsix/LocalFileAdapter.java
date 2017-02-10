package com.project_test.forsix;

import android.content.Context;
import android.content.Intent;
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


import com.project_test.forsix.RetrofitBeans.FileListBean;
import com.project_test.forsix.Retrofits.RetrofitUtil;
import com.project_test.forsix.UploadRelated.UploadBean;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kun on 2016/12/28.
 */

public class LocalFileAdapter extends BaseAdapter {
    ArrayList<File> filedata;
    Context context;
    Retrofit retrofit;
    String currentPath;

    public LocalFileAdapter(Context context, ArrayList<File> data) {
        this.context = context;
        this.filedata = data;
        fileItemListener = new FileListItemListender();
        retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        currentPath=Upload2Activity.instance.getCurrentPath();
    }

    public File[] setfiledata(ArrayList<File> data) {
        this.filedata = data;
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
                boolean addAvailable=true;//true是可以添加
                for (UploadBean bean:tmp){
                    if (bean.getFileName().equals(fileName)){
                        addAvailable=false;
                    }
                }
                if (addAvailable) {
                    Intent intent = new Intent(context, UploadService.class);
                    checkRepeatFiles(tmp, fileName, intent);
                } else {
                    Toast.makeText(context, "文件已存在于上传列表中", Toast.LENGTH_SHORT).show();
                }
                if (Upload2Activity.instance.uploadAdapter != null) {
                    Upload2Activity.instance.uploadAdapter.notifyDataSetChanged();
                }
        }

        private void checkRepeatFiles(final ArrayList<UploadBean> tmp, final String fileName, final Intent intent) {
            RetrofitUtil fileListRequest = retrofit.create(RetrofitUtil.class);
            Call call = fileListRequest.getFileListRequest(UserInfo.getInstance().getToken(), currentPath);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        FileListBean fileListBean = (FileListBean) response.body();
                        if (fileListBean.getStatus() == 200) {
                            List<FileListBean.DataBean> data = fileListBean.getData();
                            List<String> filesOnline = new ArrayList<String>();
                            File file = new File(fileName);
                            String fileNameTmp = file.getName();
                            for (FileListBean.DataBean beantmp : data) {
                                filesOnline.add(beantmp.getName());
                            }
                            if (filesOnline.contains(fileNameTmp)) {
                                Toast.makeText(context, fileNameTmp + "已存在，不能再次上传", Toast.LENGTH_LONG).show();
                            } else {
                                tmp.add(new UploadBean(0, 0, fileName));
                                Upload2Activity.instance.uploadAdapter.notifyDataSetChanged();
                                context.startService(intent);
                            }

                        } else {
                            Toast.makeText(context, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(context, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

}
