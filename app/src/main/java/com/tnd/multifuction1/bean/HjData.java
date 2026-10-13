package com.tnd.multifuction1.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author :created by zsl
 * 时间:2020/10/9 09
 * 描述:
 */
public class HjData implements Serializable {
    private String cmd;
    private String username;
    private String password;
    private List<Items> items;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public static class Items implements Serializable{
        private String gid;
        private String jcxm;
        private String jcz;
        private String jcjg;
        private String jcrq;
        private String weight;

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getJcxm(String projectName) {
            return jcxm;
        }

        public void setJcxm(String jcxm) {
            this.jcxm = jcxm;
        }

        public String getJcz() {
            return jcz;
        }

        public void setJcz(String jcz) {
            this.jcz = jcz;
        }

        public String getJcjg() {
            return jcjg;
        }

        public void setJcjg(String jcjg) {
            this.jcjg = jcjg;
        }

        public String getJcrq() {
            return jcrq;
        }

        public void setJcrq(String jcrq) {
            this.jcrq = jcrq;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }
    }
}
