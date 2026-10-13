package com.tnd.jinbiao.model;

import com.lidroid.xutils.db.annotation.Transient;

public abstract class UserInputModel<T extends UserInputModel> extends BaseData<T> {


    @Transient
    public boolean isSelected;

}
