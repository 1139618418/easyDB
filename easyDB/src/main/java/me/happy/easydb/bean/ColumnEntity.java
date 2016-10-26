package me.happy.easydb.bean;

import android.database.Cursor;
import android.widget.EditText;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016/10/14 0014.
 */
public class ColumnEntity {

    /**
     * 实体类中的属性字段
     */
    private Field field;
    private String columnName;
    private String defaultValue;

    /**
     * 获取指定对象的当前字段的值
     * @param entity	获取字段值的对象
     * @return
     */
    public Object getValue(Object entity) {
        if(entity != null) {
            try {
                //设置属性是可以访问的
                field.setAccessible(true);
                return field.get(entity);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 设置指定对象的当前字段的值
     * @param entity	获取字段值的对象
     * @return
     */
    public void setValue(Object entity, Cursor cursor) throws Exception{
        Class<?> fieldType = field.getType();
        try {
            int columnIdx = cursor.getColumnIndex(columnName);
            if(columnIdx == -1) {//当前游标中没有该字段的值
                return;
            }
            field.setAccessible(true);
            if(fieldType==int.class||fieldType==Integer.class) {
                field.set(entity, cursor.getInt(columnIdx));
                return;
            }
            if(fieldType==char.class||fieldType==String.class){
                field.set(entity, cursor.getString(columnIdx));
                return;
            }
            if(fieldType==long.class||fieldType==Long.class){
                field.set(entity, cursor.getLong(columnIdx));
                return;
            }
            if(fieldType==float.class||fieldType==Float.class){
                field.set(entity, cursor.getFloat(columnIdx));
                return;
            }
            if(fieldType==double.class||fieldType==Double.class){
                field.set(entity, cursor.getDouble(columnIdx));
                return;
            }
        } catch (Exception e) {
            throw e;
        }
    }

//    public Field getField() {
//        return field;
//    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
