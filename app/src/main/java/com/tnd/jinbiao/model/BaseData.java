package com.tnd.jinbiao.model;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.tnd.multifuction1.db.DbHelper;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BaseData<T> {

    public int id;
    protected Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    protected DbUtils dbUtils = DbHelper.GetInstance();


    public void saveOrUpdate(T t) {
        try {
            dbUtils.saveOrUpdate(t);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    public List<T> findAll() {
        try {
            return dbUtils.findAll(Selector.from(entityClass).orderBy("id", true));
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<T> findAll(Selector selector) {
        try {
            return dbUtils.findAll(selector.orderBy("id", true));
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean update(String[] columns) {

        try {
            dbUtils.update(this, columns);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAll(List<T> list, String[] columns) {

        try {
            dbUtils.updateAll(list, columns);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean findEntity(Selector selector) {

        try {
            T data = dbUtils.findFirst(selector);
            return  data != null;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean save(T t) {
        try {
            dbUtils.saveBindingId(t);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveBindingIdAll(List<T> list) {
        try {
            dbUtils.saveBindingIdAll(list);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveAll(List<T> list) {
        try {
            dbUtils.saveAll(list);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(T t) {
        try {
            dbUtils.delete(t);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAll(Class c) {
        try {
            dbUtils.deleteAll(c);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAll(List<T> list) {
        try {
            dbUtils.deleteAll(list);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }
}
