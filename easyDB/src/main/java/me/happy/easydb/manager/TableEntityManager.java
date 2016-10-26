package me.happy.easydb.manager;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.happy.easydb.annotation.Column;
import me.happy.easydb.annotation.NotDBColumn;
import me.happy.easydb.annotation.PrimaryKey;
import me.happy.easydb.annotation.Table;
import me.happy.easydb.bean.ColumnEntity;
import me.happy.easydb.bean.PrimaryKeyEntity;
import me.happy.easydb.bean.TableEntity;

/**
 * 注解处理类
 * 将实体类对应的表名,主键,列名的注解解析后封装到TableEntity
 * Created by Administrator on 2016/10/13 0013.
 */
public class TableEntityManager {

    /**
     * 每张表的相关信息缓存集合
     */
    private static HashMap<Object, TableEntity> mTableMap = new HashMap<Object, TableEntity>();

    /**
     * 获取EntityTable对象
     */
    public static <T> TableEntity getTableEntity(T entity) {
        return getTableEntity(entity.getClass());
    }

    public static <T> TableEntity getTableEntity(Class<?> mClass) {
        if(mTableMap.containsKey(mClass)) {
            return mTableMap.get(mClass);
        } else {
            return createTableEntity(mClass);
        }
    }

    public static TableEntity createTableEntity(Class<?> mClass) {
        TableEntity tableEntity = new TableEntity();
        //设置表名
        setTableName(tableEntity,mClass);
        //设置主键和列
        setColumnList(tableEntity,mClass);
        mTableMap.put(mClass, tableEntity);
        return tableEntity;
    }

    /**
     * 解析注解设置表名
     */
    public static void setTableName(TableEntity tableEntity,Class<?> mClass) {
        Table table = mClass.getAnnotation(Table.class);
        if(null==table||TextUtils.isEmpty(table.value())) {
            tableEntity.setTableName(mClass.getSimpleName());
        }
        tableEntity.setTableName(table.value());
    }

    /**
     * 解析注解设置主键(PrimaryKeyEntity)
     */
    public static boolean setPrimaryKey(TableEntity tableEntity,Field field){
        PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
        if(null == primaryKey) {
            return false;
        }
        if(primaryKey.isAutoGenerate() && field.getType()!=long.class && field.getType()!=int.class){
            throw new RuntimeException("自增长主键字段类型不正确，请设置自增长字段类型为long或者int");
        }
        PrimaryKeyEntity primaryKeyEntity = new PrimaryKeyEntity();
        primaryKeyEntity.setField(field);
        if(TextUtils.isEmpty(primaryKey.value())) {//没有通过注解设置列名，默认取字段名称为列名
            primaryKeyEntity.setColumnName(field.getName());
        } else {
            primaryKeyEntity.setColumnName(primaryKey.value());
        }
        primaryKeyEntity.setAutoGenerate(primaryKey.isAutoGenerate());//获取是否自动增长
        tableEntity.setPrimaryKey(primaryKeyEntity);
        return true;
    }

    /**
     * 解析注解获得字段列名和默认值并封装到 ColumnEntity
     */
    public static void setColumnList(TableEntity tableEntity,Class<?> mClass) {
        Field[] fields = mClass.getDeclaredFields();
        List<ColumnEntity> columnList = new ArrayList<ColumnEntity>();
        ColumnEntity columnEntity = null;
        for(Field field:fields){
            if (Modifier.isStatic(field.getModifiers())) {//过滤掉static静态字段
                continue;
            }
            if(field.getType() == Object.class) {//过滤掉非基本类型字段
                continue;
            }
            NotDBColumn notDbColumn = field.getAnnotation(NotDBColumn.class);
            if(null != notDbColumn) {//非数据库字段不作处理
                continue;
            }
            if(null==tableEntity.getPrimaryKey()){ //判断是否有主键注释 并设置主键
                if(setPrimaryKey(tableEntity,field)){
                    continue;
                }
            }
            //获取每个字段的注解
            Column column = field.getAnnotation(Column.class);
            columnEntity = new ColumnEntity();
            columnEntity.setField(field);
            // 解析注解设置列名，默认取字段名称为列名
            String columnName = field.getName();
            if(column != null) {
                if (!TextUtils.isEmpty(column.value())) {
                    columnName = column.value();
                }
                if(!TextUtils.isEmpty(column.defaultValue())){
                    columnEntity.setDefaultValue(column.defaultValue());
                }
            }
            columnEntity.setColumnName(columnName);
            columnList.add(columnEntity);
        }
        tableEntity.setColumnList(columnList);
    }


    private void changeFieldValue(){

    }

}
