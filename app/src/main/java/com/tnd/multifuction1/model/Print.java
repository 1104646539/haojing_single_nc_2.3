package com.tnd.multifuction1.model;

/**
 * 打印
 */
public class Print extends UserInputModel<Print> {
    public String p_name;
    public int readPosition;
    public boolean isSelect;//是否被选中
    public boolean isMultiple;//是否逐条打印可选
    public boolean isMerge;//是否合并打印可选
    public boolean isRequired;//是否必选
    public boolean isSelectMultiple;//是否被选中逐条
    public boolean isSelectMerge;//是否被选中合并


    public boolean isPrintQrCode; //是否打印二维码

    public Print(String p_name, boolean isMultiple, boolean isMerge, boolean isRequired) {
        this.p_name = p_name;
        this.isMultiple = isMultiple;
        this.isMerge = isMerge;
        this.isRequired = isRequired;
    }

    public boolean isSelectMultiple() {
        return isSelectMultiple;
    }

    public void setSelectMultiple(boolean selectMultiple) {
        isSelectMultiple = selectMultiple;
    }

    public boolean isSelectMerge() {
        return isSelectMerge;
    }

    public void setSelectMerge(boolean selectMerge) {
        isSelectMerge = selectMerge;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public boolean isMerge() {
        return isMerge;
    }

    public void setMerge(boolean merge) {
        isMerge = merge;
    }

    public boolean isPrintQrCode() {
        return isPrintQrCode;
    }

    public void setPrintQrCode(boolean printQrCode) {
        isPrintQrCode = printQrCode;
    }

    @Override
    public String toString() {
        String str = "";
        str += "{" +
                "\"" + "p_name" + "\"" + ":" + "\"" + p_name + "\"" + "," +
                "\"" + "isMerge" + "\"" + ":" + isMerge + "," +
                "\"" + "isMultiple" + "\"" + ":" + isMultiple + "," +
                "\"" + "isRequired" + "\"" + ":" + isRequired + "," +
                "\"" + "isSelectMultiple" + "\"" + ":" + isSelectMultiple + "," +
                "\"" + "readPosition" + "\"" + ":" + readPosition + "," +
                "\"" + "isSelectMerge" + "\"" + ":" + isSelectMerge + "," +
                "\"" + "isPrintQrCode" + "\"" + ":" + isPrintQrCode +
                "}";
        return str;
    }
}
