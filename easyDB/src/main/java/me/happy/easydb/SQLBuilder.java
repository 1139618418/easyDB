package me.happy.easydb;

import android.text.TextUtils;

import java.util.Date;
import java.util.List;

import me.happy.easydb.bean.BindSQL;
import me.happy.easydb.bean.ColumnEntity;
import me.happy.easydb.bean.PrimaryKeyEntity;
import me.happy.easydb.bean.TableEntity;
import me.happy.easydb.manager.TableEntityManager;

/**
 * Created by Administrator on 2016/10/13 0013.
 */
public class SQLBuilder {

    public static String getCreateTableSQL(TableEntity tableEntity) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(tableEntity.getTableName());
        sql.append(" (");
        if(null!=tableEntity.getPrimaryKey()){
            sql.append(tableEntity.getPrimaryKey().getColumnName());//主键字段
            sql.append(" INTEGER PRIMARY KEY ");//主键
            if(tableEntity.getPrimaryKey().isAutoGenerate()) {
                sql.append("AUTOINCREMENT");
            }
            sql.append(",");
        }
        List<ColumnEntity> columnList = tableEntity.getColumnList();
        for (ColumnEntity columnEntity : columnList) {
            sql.append(columnEntity.getColumnName());
            if(!TextUtils.isEmpty(columnEntity.getDefaultValue())) {
                sql.append(" DEFAULT ").append(columnEntity.getDefaultValue());
            }
            sql.append(",");
        }
        //删除最后一个逗号
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        return sql.toString();
    };

    public static String getAlterTableColumnSQL(String tableName, ColumnEntity columnEntity) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ");
        sql.append(tableName);
        sql.append(" ADD COLUMN ");
        sql.append(columnEntity.getColumnName());
        if (!TextUtils.isEmpty(columnEntity.getDefaultValue())){
            sql.append(" DEFAULT ");
            sql.append(columnEntity.getDefaultValue());
         }
        return sql.toString();
    };

    public static String getTableAllColumnSQL(String tableName) {
        return "SELECT * FROM " + tableName + " LIMIT 0";
    }

    public static <T> BindSQL getInsertSQL(TableEntity tableEntity,T entity){
        StringBuilder sql = new StringBuilder();
        StringBuilder valueSql = new StringBuilder();
        int size = tableEntity.getColumnList().size();
        Object[] args = new Object[size+1];
        sql.append("INSERT INTO ").append(tableEntity.getTableName()).append("(");
        valueSql.append(" VALUES(");

        int i = 0;
        PrimaryKeyEntity primaryKey = tableEntity.getPrimaryKey();
        if(null!=primaryKey&&!primaryKey.isAutoGenerate()){
            sql.append(primaryKey.getColumnName()+",");
            valueSql.append("?,");
            args[i++] = primaryKey.getValue(entity);
        }

        for (ColumnEntity columnEntity:tableEntity.getColumnList()){
            Object valueObj = columnEntity.getValue(entity);
            if(null!=valueObj){
                sql.append(columnEntity.getColumnName());
                sql.append(",");
                valueSql.append("?");
                valueSql.append(",");
                args[i++] = columnEntity.getValue(entity);
            }
        }

        sql.deleteCharAt(sql.length() - 1);
        valueSql.deleteCharAt(valueSql.length()-1);
        sql.append(")");
        valueSql.append(")");
        sql.append(valueSql);
        Object[] args2 = new Object[i];
        System.arraycopy(args,0,args2,0,i);
        return new BindSQL(sql.toString(),args2);
    };

    public static <T> String getDeleteSQL(TableEntity tableEntity) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(tableEntity.getTableName());
        sql.append(" WHERE ").append(tableEntity.getPrimaryKey().getColumnName()).append(" = ?");
        return sql.toString();
    }

    public static <T> BindSQL getUpdateSQL(TableEntity tableEntity, T entity) {
        Object[] args = new Object[tableEntity.getColumnList().size()+1];
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(tableEntity.getTableName());
        sql.append(" SET ");
        int i = 0;
        for (ColumnEntity columnEntity:tableEntity.getColumnList()){
            Object valueObj = columnEntity.getValue(entity);
            if(null!=valueObj){
                args[i++] = valueObj;
                sql.append(columnEntity.getColumnName() + "= ?");
                sql.append(",");
            }
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE ").append(tableEntity.getPrimaryKey().getColumnName()).append(" = ?");
        args[i++] = tableEntity.getPrimaryKey().getValue(entity);
        Object[] args2 = new Object[i];
        System.arraycopy(args,0,args2,0,i);
        return new BindSQL(sql.toString(),args2);
    }

    public static <T> String getQueryByIdSQL(TableEntity tableEntity) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        sql.append(tableEntity.getTableName());
        sql.append(" WHERE ");
        sql.append(tableEntity.getPrimaryKey().getColumnName());
        sql.append(" = ?");
        return sql.toString();
    }

    public static String getQueryPageSQL(TableEntity tableEntity) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        sql.append(tableEntity.getTableName());
        sql.append(" LIMIT ? OFFSET ? * ? ");
        return sql.toString();
    }

    public static String getQueryPageSQL(TableEntity tableEntity,String whereClause) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        sql.append(tableEntity.getTableName());
        if(null!=whereClause) {
            sql.append(" WHERE ");
            sql.append(whereClause);
        }
        sql.append(" LIMIT ? OFFSET ? * ? ");
        return sql.toString();
    }

}
