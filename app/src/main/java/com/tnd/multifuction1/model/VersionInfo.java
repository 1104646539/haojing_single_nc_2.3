package com.tnd.multifuction1.model;

public class VersionInfo {
    //    [{"id":"1","verName":"1.2.1","verCode":"19"}]
    private String id;
    private String verName;
    private String verCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    public String getVerCode() {
        return verCode;
    }

    public void setVerCode(String verCode) {
        this.verCode = verCode;
    }
}
