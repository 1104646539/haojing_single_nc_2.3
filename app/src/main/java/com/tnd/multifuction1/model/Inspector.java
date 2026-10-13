package com.tnd.multifuction1.model;

/**
 * 检测人员
 */
public class Inspector extends UserInputModel<Inspector> implements FiltrateModel {
    public int i_id;
    public String i_name;
    public boolean isSelect;

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public Inspector() {
    }

    @Override
    public String toString() {
        return i_name==null?"":i_name;
    }

    @Override
    public boolean isSelect() {
        return isSelect;
    }

    public Inspector(String name) {
        this.i_name = name;

    }

    @Override
    public String getName() {
        return i_name;
    }
}
