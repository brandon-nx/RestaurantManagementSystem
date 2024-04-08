package com.restaurantmanagementsystem.pos.extra;

public interface IManageData {
    void addData(Object data);
    void removeData(Object data);
    void updateData(Object oldData, Object newData);
}
