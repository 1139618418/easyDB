package me.happy.easydb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.util.Collection;
import java.util.List;
import me.happy.easydb.manager.SQLExecuteManager;
import me.happy.easydb.utils.DBUtil;

/**
 * Created by Administrator on 2016/10/11 0011.
 */
public class SQLiteDB {

    /**
     * 数据库操作类
     */
    private SQLiteDatabase mDB;
    /**
     * SQL语句执行管理器
     */
    private SQLExecuteManager mSQLExecuteManager;
    /**
     * 数据库保存路径
     */
    private String dbFilePath;

    //双重校验锁单例
    private volatile static SQLiteDB sqliteDB;
    private SQLiteDB() {}
    public static SQLiteDB getInstance() {
        if (sqliteDB == null) {
            synchronized (SQLiteDB.class) {
                if (sqliteDB == null) {
                    sqliteDB = new SQLiteDB();
                }
            }
        }
        return sqliteDB;
    }

    /**
     * 创建数据库
     * 使用默认路径/data/data/packagename/database
     */
    public void openOrCreateDB(Context context, String dbName) {
        openOrCreateDB(context, dbName,null,0);
    }

    /**
     * 创建数据库->SD卡
     * @param dbDirName 数据库目录名称 (比如:easy或者easy/db)
     */
    public void openOrCreateDB(Context context,String dbName,String dbDirName) {
        openOrCreateDB(context, dbName,dbDirName,0);
    }

    /**
     * 创建数据库 ->从项目raw文件夹下copy到SD卡
     * @param dbName    数据库名字
     * @param dbDirName 数据库目录名称
     * @param rawResource 项目raw文件夹下面的数据库资源
     */
    public void openOrCreateDB(Context context,String dbName,String dbDirName,int rawResource) {
        if(null==dbFilePath) {
            dbFilePath = DBUtil.getDataBaseFilePath(context, dbName, dbDirName, rawResource);
            open();
        }
    }

    /**
     * 创建表
     */
    public <T> void createTable(Class<T> mClass){
        mSQLExecuteManager.createTable(mClass);
    }

    /**
     * 给已有表添加列(sqlLite3不支持更改列名和删除列)
     */
    public <T> void alterTableColumn(Class<T> mClass){
        mSQLExecuteManager.alterTableColumn(mClass);
    }

    /**
     * 删除指定表
     */
    public void dropTable(String tableName) {
        mSQLExecuteManager.dropTable(tableName);
    }

    /**
     * 新增一条数据
     */
    public <T> long insert(T entity){
        return mSQLExecuteManager.insert(entity);
    }

    /**
     * 新增集合
     */
    public <T> long insert(Collection<T> collection){
        return mSQLExecuteManager.insert(collection);
    };

    /**
     * 新增或者修改一条数据(根据主键)
     */
    public <T> long insertOrUpdate(T entity){
        if(queryCount(entity.getClass())>0){
           return update(entity);
        }
        return insert(entity);
    };

    /**
     * 修改一条数据(根据主键)
     */
    public <T> long update(T entity){
        return mSQLExecuteManager.update(entity);
    }

    /**
     * 修改集合数据（根据主键）
     */
    public <T> long update(Collection<T> collection) {
        return mSQLExecuteManager.update(collection);
    }

    /**
     * 自定义修改语句和条件
     */
    public <T> long update(T entity,String whereClause,String[] whereArgs){
        return mSQLExecuteManager.update(entity,whereClause,whereArgs);
    }

    /**
     * 自定义修改
     * sql语句 update 表名 set xxx=?,xxx=? ... where xxx = ？...
     */
    public <T> long update(String sql,String[] args){
        return mSQLExecuteManager.update(sql,args);
    }

    /**
     * 删除一条数据(默认是主键)
     */
    public <T> long delete(T entity){
        return mSQLExecuteManager.delete(entity);
    }

    public <T> long delete(Class<T> mClass,String primaryKeyValue){
        return mSQLExecuteManager.delete(mClass,primaryKeyValue);
    }

    /**
     * 自定义删除
     * sql语句 delete from 表名 where xxx = ？...
     */
    public <T> long delete(String sql,String[] args){
        return mSQLExecuteManager.delete(sql,args);
    }

    /**
     * 查询一条数据（主键ID）
     */
    public <T> T queryOne(Class<T> mClass, String id) {
        return mSQLExecuteManager.queryOne(mClass,id);
    }

    /**
     * 查询一条数据（自定义条件）
     * @param whereSql 条件的sql语句 xxx=？ and xxx=?
     * @param args 参数占位符
     */
    public <T> T queryOne(Class<T> mClass,String whereSql,String[] args) {
        return mSQLExecuteManager.queryOne(mClass,whereSql,args);
    }

    /**
     * 查询集合
     */
    public <T> List<T> queryList(Class<T> mClass) {
        return mSQLExecuteManager.queryList(mClass);
    }

    /**
     * 查询集合（自定义条件）
     */
    public <T> List<T> queryList(Class<T> mClass,String whereSql,String[] args) {
        return mSQLExecuteManager.queryList(mClass,whereSql,args);
    }

    /**
     * 分页查询
     */
    public <T> List<T> queryPage(Class<T> mClass,int curPage, int pageSize){
        return mSQLExecuteManager.queryPage(mClass,curPage,pageSize);
    }

    /**
     * 分页查询（自定义查询条件）
     */
    public <T> List<T> queryPage(Class<T> mClass,String whereClause,String[] whereArgs,int curPage, int pageSize){
        return mSQLExecuteManager.queryPage(mClass,whereClause,whereArgs,curPage,pageSize);
    }

    /**
     * 查询数据总数
     */
    public <T> int queryCount(Class<T> mClass){
        return mSQLExecuteManager.queryCount(mClass);
    }

    /**
     * 查询数据总数（自定义查询条件）
     */
    public <T> int queryCount(Class<T> mClass,String whereClause,String[] args){
        return mSQLExecuteManager.queryCount(mClass,whereClause,args);
    }
    
    /**
     * 对于复杂的查询可以自己写sql语句
     */
    public Cursor query(String sql,String[] args){
        return mSQLExecuteManager.rawQuery(sql,args);
    }
    
    //复杂的查询 返回的Cursor转实体
    public <T> T query(Class<T> mClass,String sql,String[] args){
        return CursorUtil.parseOneResult(qyery(sql,args),mClass);
    }
    
    //复杂的查询 返回的Cursor转实体集合
    public <T> List<T> query(Class<T> mClass,String sql,String[] args){
        return CursorUtil.parseList(qyery(sql,args),mClass);
    }

     
    public SQLiteDatabase open(){
        if(null==mDB||!mDB.isOpen()){
            mDB = SQLiteDatabase.openOrCreateDatabase(dbFilePath, null);
            mSQLExecuteManager = new SQLExecuteManager(mDB);
        }
        return mDB;
    }

    public void close() {
        if(null!=mDB&&mDB.isOpen()){
            mDB.close();
        }
    }

}
