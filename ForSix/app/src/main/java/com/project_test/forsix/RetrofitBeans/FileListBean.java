package com.project_test.forsix.RetrofitBeans;

import java.util.List;

/**
 * Created by kun on 2017/1/20.
 */

public class FileListBean {


    /**
     * code : 200001000
     * status : 200
     * data : [{"gmtModify":1484025173590,"path":"D:\\cloudpan\\1\\aaa\\bbb\\ccc","name":"ccc","mime":"","size":0,"type":2},{"gmtModify":1484025443766,"path":"D:\\cloudpan\\1\\aaa\\bbb\\stock-redirect.png","name":"stock-redirect.png","size":9544,"type":1}]
     */

    private int code;
    private int status;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * gmtModify : 1484025173590
         * path : D:\cloudpan\1\aaa\bbb\ccc
         * name : ccc
         * mime :
         * size : 0
         * type : 2
         */

        private long gmtModify;
        private String path;
        private String name;
        private String mime;
        private int size;
        private int type;

        public long getGmtModify() {
            return gmtModify;
        }

        public void setGmtModify(long gmtModify) {
            this.gmtModify = gmtModify;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMime() {
            return mime;
        }

        public void setMime(String mime) {
            this.mime = mime;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
