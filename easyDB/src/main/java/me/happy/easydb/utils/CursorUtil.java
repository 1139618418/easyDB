package me.happy.easydb.utils;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.happy.easydb.annotation.PrimaryKey;
import me.happy.easydb.bean.ColumnEntity;
import me.happy.easydb.bean.PrimaryKeyEntity;
import me.happy.easydb.bean.TableEntity;
import me.happy.easydb.manager.TableEntityManager;

/**
 * Created by Administrator on 2016/10/21 0021.
 */
public class CursorUtil {

    /**
     * 判断Cursor是否正确，即存在结果集
     */
    public static boolean isCursorRight(Cursor cursor) {
        if(cursor == null || cursor.getCount() <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 关闭某个Cursor
     */
    public static void closeCursor(Cursor cursor) {
        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = null;
    }

    public static <T> T parseOneResult(Cursor cursor, Class<T> mClass) {
        if(!isCursorRight(cursor)) {
            return null;
        }
        TableEntity tableEntity = TableEntityManager.getTableEntity(mClass);
        PrimaryKeyEntity primaryKey = tableEntity.getPrimaryKey();
        T entity = null;
        try {
            entity = (T) mClass.newInstance();
            cursor.moveToFirst();
            if(null!=primaryKey) {
                primaryKey.setValue(entity, cursor);
            }
            for(ColumnEntity columnEntity : tableEntity.getColumnList()) {
                columnEntity.setValue(entity,cursor);
            }
        } catch (Exception e) {
            entity = null;
            throw new IllegalArgumentException(e);
        } finally {
            closeCursor(cursor);
        }
        return entity;
    }

    public static <T> List<T> parseList(Cursor cursor, Class<T> mClass) {
        List<T> list = new ArrayList<T>();
        if(!isCursorRight(cursor)) {
            return list;
        }
//        long count = cursor.getCount();
        TableEntity tableEntity = TableEntityManager.getTableEntity(mClass);
        PrimaryKeyEntity primaryKey = tableEntity.getPrimaryKey();
        try {
//            for(int i = 0; i < count; i++) {
            while (cursor.moveToNext()){
//                cursor.moveToPosition(1);
                T entity = (T) mClass.newInstance();
                if(null!=primaryKey){
                    primaryKey.setValue(entity,cursor);
                }
                for(ColumnEntity columnEntity : tableEntity.getColumnList()) {
                    columnEntity.setValue(entity,cursor);
                }
                list.add(entity);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        } finally {
            closeCursor(cursor);
        }
        return list;
    }

    public static int parseCount(Cursor cursor) {
        int value = 0;
        if(!isCursorRight(cursor)) {
            return 0;
        }
        try {
            cursor.moveToFirst();
            value = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return value;
    }

}
