package com.tnd.jinbiao.model;

/**
 * 检测单位
 */
public class CheckOrg extends UserInputModel<CheckOrg> implements FiltrateModel {
    //id
    public int co_id;
    //name
    public String co_name;
    private boolean isSelect;

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public CheckOrg() {
    }

    @Override
    public String toString() {
        return co_name == null ? "" : co_name;
    }

    public CheckOrg(String co_name) {
        this.co_name = co_name;
    }

    @Override
    public boolean isSelect() {
        return isSelect;
    }

    @Override
    public String getName() {

        return co_name;
    }
}
