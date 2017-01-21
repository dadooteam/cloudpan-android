package com.project_test.forsix;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
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

import com.project_test.forsix.RetrofitBeans.FileListBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kun on 2017/1/20.
 */

public class CloudFileAdapter extends BaseAdapter {
    private Context mContext;
    private List<FileListBean.DataBean> files;
    private String currentPath;

    public CloudFileAdapter() {
    }

    public CloudFileAdapter(Context context, List<FileListBean.DataBean> beans) {
        mContext = context;
        files = beans;
    }

    @Override
    public int getCount() {
        return files.equals(null) ? 0 : files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    CloudItemListender cloudItemListener;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = convertView;
        ret = LayoutInflater.from(mContext).inflate(R.layout.cloud_list_file_cell, parent, false);

        Holer holder = (Holer) ret.getTag();
        if (holder == null) {
            holder = new Holer(ret);
            ret.setTag(holder);
        }
        holder.bindView(position, files.get(position));
        return ret;
    }


    public static String generateSize(FileListBean.DataBean item) {
        if (item.getType() == 1) {
            long result = item.getSize();
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
        return "文件夹";
    }

    private class Holer {
        private ImageView cloudFileImage;
        private TextView cloudFileName;
        private TextView cloudFileSize;
        private TextView cloudFileTime;
        private ImageButton cloudFilemore;


        public Holer(View v) {
            cloudFileImage = (ImageView) v.findViewById(R.id.cloud_file_image);
            cloudFileName = (TextView) v.findViewById(R.id.cloud_file_name);
            cloudFileSize = (TextView) v.findViewById(R.id.cloud_file_size);
            cloudFileTime = (TextView) v.findViewById(R.id.cloud_file_time);
            cloudFilemore = (ImageButton) v.findViewById(R.id.cloud_file_more);
        }

        public void bindView(final int position, final FileListBean.DataBean item) {
            cloudFileName.setText(item.getName());
            cloudFileSize.setText(generateSize(item));
            if (item.getType() == 2) {
                cloudFileImage.setImageResource(R.drawable.folder);
            } else {
                cloudFileImage.setImageResource(R.drawable.file);
            }
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cloudFileTime.setText(format.format(new Date(item.getGmtModify())));
            cloudItemListener = new CloudItemListender(item);
            cloudFilemore.setOnClickListener(cloudItemListener);

        }
    }

    private class CloudItemListender implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private FileListBean.DataBean data;

        public CloudItemListender(FileListBean.DataBean data) {
            this.data = data;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        @Override
        public void onClick(final View v) {
            PopupMenu popupMenu = new PopupMenu(mContext, v);
            popupMenu.getMenuInflater().inflate(R.menu.cloud_file_popup_menu, popupMenu.getMenu());
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
            if (data.getType() == 2) {
                popupMenu.getMenu().findItem(R.id.cloud_download).setVisible(false);
            } else {
                popupMenu.getMenu().findItem(R.id.cloud_download).setVisible(true);
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
                case R.id.cloud_download:
                    download();
                    break;
                case R.id.cloud_delete:
                    delete();
                default:
                    break;
            }
            return true;
        }

        private void download() {
            String path = data.getPath();
            Log.e("download: ", "假装下载了" + path);
        }

        private void delete() {
            judgeAlertDialog(mContext, "提醒", "确认删除 " + data.getName() + " ?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteFile(data);
                    files.remove(data);
                    notifyDataSetChanged();

                }
            }, null);

        }

    }

    public static AlertDialog judgeAlertDialog(Context context, String title,
                                               String message, DialogInterface.OnClickListener okListener,
                                               DialogInterface.OnClickListener cancleListener) {
        AlertDialog aDialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setNegativeButton("确定", okListener)
                .setPositiveButton("取消", cancleListener).show();
        return aDialog;
    }

    public static void deleteFile(FileListBean.DataBean bean){
        Log.e("delete: ", "假装删除了" +bean.getName());

    }
}
