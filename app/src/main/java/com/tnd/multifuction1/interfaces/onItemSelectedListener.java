package com.tnd.multifuction1.interfaces;


import com.tnd.multifuction1.model.UserInputModel;

import java.util.List;

public interface onItemSelectedListener<T extends UserInputModel> {

    int getSelectedCount();

    List<T> getSelectedList();
}
