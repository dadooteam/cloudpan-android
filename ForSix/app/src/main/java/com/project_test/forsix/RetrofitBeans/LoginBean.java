package com.project_test.forsix.RetrofitBeans;

/**
 * Created by kun on 2017/1/19.
 */

public class LoginBean {


    /**
     * code : 200001000
     * status : 200
     * message : null
     * data : {"id":1,"gmtCreate":1484668932227,"name":"17180100300","phone":"17180100300","token":"0966a0d3b9c164324d3771131f47c092"}
     */

    private int code;
    private int status;
    private Object message;
    private DataBean data;

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

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * gmtCreate : 1484668932227
         * name : 17180100300
         * phone : 17180100300
         * token : 0966a0d3b9c164324d3771131f47c092
         */

        private int id;
        private long gmtCreate;
        private String name;
        private String phone;
        private String token;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public long getGmtCreate() {
            return gmtCreate;
        }

        public void setGmtCreate(long gmtCreate) {
            this.gmtCreate = gmtCreate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
