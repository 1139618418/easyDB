package me.happy.easydb.manager;

import me.happy.easydb.SQLBuilder;
import me.happy.easydb.bean.BindSQL;
import me.happy.easydb.bean.ColumnEntity;
import me.happy.easydb.bean.PrimaryKeyEntity;
import me.happy.easydb.bean.TableEntity;
import me.happy.easydb.utils.CursorUtil;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * SQL语句执行器
 * Author: hyl
 * Time: 2015-8-14下午10:26:54
 */
public class SQLExecuteManager implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * SQLite中的关键字 
	 */
	public static final String[] SQLITE_KEYWORDS = { "ABORT", "ACTION",
		"ADD", "AFTER", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC",
		"ATTACH", "AUTOINCREMENT", "BEFORE", "BEGIN", "BETWEEN", "BY",
		"CASCADE", "CASE", "CAST", "CHECK", "COLLATE", "COLUMN", "COMMIT",
		"CONFLICT", "CONSTRAINT", "CREATE", "CROSS", "CURRENT_DATE",
		"CURRENT_TIME", "CURRENT_TIMESTAMP", "DATABASE", "DEFAULT",
		"DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DETACH", "DISTINCT",
		"DROP", "EACH", "ELSE", "END", "ESCAPE", "EXCEPT", "EXCLUSIVE",
		"EXISTS", "EXPLAIN", "FAIL", "FOR", "FOREIGN", "FROM", "FULL",
		"GLOB", "GROUP", "HAVING", "IF", "IGNORE", "IMMEDIATE", "IN",
		"INDEX", "INDEXED", "INITIALLY", "INNER", "INSERT", "INSTEAD",
		"INTERSECT", "INTO", "IS", "ISNULL", "JOIN", "KEY", "LEFT", "LIKE",
		"LIMIT", "MATCH", "NATURAL", "NO", "NOT", "NOTNULL", "NULL", "OF",
		"OFFSET", "ON", "OR", "ORDER", "OUTER", "PLAN", "PRAGMA",
		"PRIMARY", "QUERY", "RAISE", "REFERENCES", "REGEXP", "REINDEX",
		"RELEASE", "RENAME", "REPLACE", "RESTRICT", "RIGHT", "ROLLBACK",
		"ROW", "SAVEPOINT", "SELECT", "SET", "TABLE", "TEMP", "TEMPORARY",
		"THEN", "TO", "TRANSACTION", "TRIGGER", "UNION", "UNIQUE",
		"UPDATE", "USING", "VACUUM", "VALUES", "VIEW", "VIRTUAL", "WHEN",
		"WHERE" };
	
	/**
	 * 数据库操作类
	 */
	private SQLiteDatabase mSQLiteDataBase;

	public SQLExecuteManager(SQLiteDatabase mSQLiteDataBase) {
		this.mSQLiteDataBase = mSQLiteDataBase;
	}
	
	/**
	 * 开启一个事务(事务开始)
	 * 在事务代码执行完成后，必须要执行successTransaction()将事务标记为成功
	 * 在代码的最后必须要执行endTransaction()来结束当前事务，如果事务成功则提交事务，否则回滚事务
	 */
	public void beginTransaction() {
		this.mSQLiteDataBase.beginTransaction();
	}
	
	/**
	 * 标记当前事务成功
	 */
	public void successTransaction() {
		this.mSQLiteDataBase.setTransactionSuccessful();
	}
	
	/**
	 * 结束当前事务，当事物被标记成功后，此操作会提交事务，否则会回滚事务
	 */
	public void endTransaction() {
		this.mSQLiteDataBase.endTransaction();
	}

	/**
	 * 执行指定无返回值的单条SQL语句，如建表、创建数据库等
	 */
	public void execSQL(String sql) {
		mSQLiteDataBase.execSQL(sql);
	}

	/**
	 * 执行查询返回Cursor
	 * @param sql 查询语句(参数使用占位符)
	 * @param args 占位符参数
     */
	public Cursor rawQuery(String sql,String[] args) {
		return mSQLiteDataBase.rawQuery(sql,args);
	}

	/**
	 * 执行sql返回SQLiteStatement
	 * SQLiteStatement 用于增删改 可以防止sql注入 性能也好很多
     */
	public SQLiteStatement getSQLiteStatement(String sql){
		return mSQLiteDataBase.compileStatement(sql);
	}

	/**
	 * 创建表
	 */
	public <T> void createTable(Class<T> mClass){
		TableEntity tableEntity = TableEntityManager.getTableEntity(mClass);
		execSQL(SQLBuilder.getCreateTableSQL(tableEntity));
	}

	/**
	 * 给已有表添加列(sqlLite3不支持更改列名和删除列)
	 */
	public <T> void alterTableColumn(Class<T> mClass){
		TableEntity tableEntity = TableEntityManager.getTableEntity(mClass);
		//获取到已有表所有的列名
		Cursor cursor = rawQuery(SQLBuilder.getTableAllColumnSQL(tableEntity.getTableName()),null);
		List<String> columnNames = Arrays.asList(cursor.getColumnNames());
		//获取当前对象属性对应的列名
		List<ColumnEntity> columnList= tableEntity.getColumnList();
		for(ColumnEntity columnEntity:columnList) {
			//数据表中不包含当前实体属性字段信息，说明当前实体属性字段需要新增到数据表中
			if(!columnNames.contains(columnEntity.getColumnName())) {
				execSQL(SQLBuilder.getAlterTableColumnSQL(tableEntity.getTableName(),columnEntity));
			}
		}
	}

	/**
	 * 删除指定表
	 */
	public void dropTable(String tableName){
		execSQL("DROP TABLE IF EXISTS " + tableName);
	}

	/**
	 * 新增一条数据
     */
	public <T> long insert(T entity){
		TableEntity tableEntity = TableEntityManager.getTableEntity(entity);
		BindSQL bindSQL = SQLBuilder.getInsertSQL(tableEntity,entity);
		long rowId =  insert(bindSQL.getSql(),bindSQL.getBindArgs());
		return rowId;
	}

	/**
	 * 新增集合
     */
	public <T> long insert(Collection<T> collection){
		long rowId = -1;
		if(null==collection||collection.size()<=0){
			return rowId;
		}
		try {
			beginTransaction();
			Iterator<T> iterator = collection.iterator();
			while (iterator.hasNext()) {
				rowId = insert(iterator.next());
				if(rowId == -1) {
					throw new SQLException("保存实体失败");
				}
			}
			successTransaction();
			rowId = collection.size();
		} catch (SQLException e) {
			e.printStackTrace();
			rowId = -1;
		} finally {
			endTransaction();
		}
		return rowId;
	}

	/**
	 * 修改一条数据（根据主键）
	 */
	public <T> long update(T entity){
		TableEntity tableEntity = TableEntityManager.getTableEntity(entity);
		if(null==tableEntity.getPrimaryKey().getValue(entity)) {
			throw new IllegalArgumentException("未设置要修改的实体的主键");
		}
		BindSQL bindSQL = SQLBuilder.getUpdateSQL(tableEntity,entity);
		return updateOrDelete(bindSQL.getSql(),bindSQL.getBindArgs());
	}

	/**
	 * 修改集合数据（根据主键）
	 */
	public <T> long update(Collection<T> collection) {
		long rowId = -1;
		if(collection.size()<=0) {
			return rowId;
		}
		try {
			Iterator<T> iterator = collection.iterator();
			beginTransaction();
			while(iterator.hasNext()) {
				rowId = update(iterator.next());
				if(rowId == -1) {
					throw new SQLException("修改实体失败");
				}
			}
			successTransaction();
			rowId = collection.size();
		}catch (SQLException e) {
			e.printStackTrace();
			rowId = -1;
		} finally {
			endTransaction();
		}
		return rowId;
	}

	/**
	 * 修改一条数据（自定义条件和占位符）
	 */
	public <T> long update(T entity,String whereClause,String[] args){
		TableEntity tableEntity = TableEntityManager.getTableEntity(entity);
		ContentValues contentValues = new ContentValues();
		for (ColumnEntity columnEntity:tableEntity.getColumnList()){
			Object valueObj = columnEntity.getValue(entity);
			if(null!=valueObj){
				contentValues.put(columnEntity.getColumnName(),String.valueOf(valueObj));
			}
		}
		return mSQLiteDataBase.update(tableEntity.getTableName(),contentValues,whereClause,args);
	}

	/**
	 * 自定义修改语句和条件
	 */
	public <T> long update(String sql,String[] args){
		return updateOrDelete(sql,args);
	}

	/**
	 * 删除一条数据（根据主键）
	 */
	public <T> long delete(T entity){
		TableEntity tableEntity = TableEntityManager.getTableEntity(entity);
		if(null==tableEntity.getPrimaryKey().getValue(entity)) {
			throw new IllegalArgumentException("未设置要删除的实体的主键");
		}
		String keyStr = tableEntity.getPrimaryKey().getValue(entity).toString();
		return updateOrDelete(SQLBuilder.getDeleteSQL(tableEntity),new String[]{keyStr});
	}

	/**
	 * 删除一条数据（根据主键）
	 */
	public <T> long delete(Class<T> mClass,String primaryKeyValue){
		TableEntity tableEntity = TableEntityManager.getTableEntity(mClass);
		return updateOrDelete(SQLBuilder.getDeleteSQL(tableEntity),new String[]{primaryKeyValue});
	}

	/**
	 * 自定义删除语句和条件
	 */
	public <T> long delete(String sql,String[] args){
		return updateOrDelete(sql,args);
	}

	/**
	 * 查询一条数据（根据主键）
	 */
	public <T> T queryOne(Class<T> mClass,String id){
		TableEntity tableEntity = TableEntityManager.getTableEntity(mClass);
		Cursor cursor = rawQuery(SQLBuilder.getQueryByIdSQL(tableEntity),new String[]{id});
		return CursorUtil.parseOneResult(cursor,mClass);
	}

	/**
	 * 查询一条数据(自定义条件)
	 */
	public <T> T queryOne(Class<T> mClass, String whereClause, String[] whereArgs) {
		TableEntity tableEntity = TableEntityManager.getTableEntity(mClass);
		Cursor cursor = rawQuery("select * from " + tableEntity.getTableName() + "where" + whereClause,whereArgs);
		return CursorUtil.parseOneResult(cursor,mClass);
	}

	/**
	 * 查询所有数据
	 */
	public <T> List<T> queryList(Class<T> mClass){
		TableEntity tableEntity = TableEntityManager.getTableEntity(mClass);
		Cursor cursor = rawQuery("select * from "+tableEntity.getTableName(),null);
		return CursorUtil.parseList(cursor,mClass);
	}

	/**
	 * 查询所有数据(自定义条件)
	 */
	public <T> List<T> queryList(Class<T> mClass, String whereClause, String[] whereArgs) {
		TableEntity tableEntity = TableEntityManager.getTableEntity(mClass);
		Cursor cursor = rawQuery("select * from " + tableEntity.getTableName() + "where" + whereClause,whereArgs);
		return CursorUtil.parseList(cursor,mClass);
	}

	/**
	 * 分页查询
	 */
	public <T> List<T> queryPage(Class<T> mClass,int curPage,int pageSize){
		TableEntity tableEntity = TableEntityManager.getTableEntity(mClass);
		Cursor cursor = rawQuery(SQLBuilder.getQueryPageSQL(tableEntity),
				new String[]{String.valueOf(pageSize),String.valueOf(curPage-1),String.valueOf(pageSize)});
		return CursorUtil.parseList(cursor,mClass);
	}

	/**
	 * 分页查询(自定义条件)
	 */
	public <T> List<T> queryPage(Class<T> mClass,String whereClause,String[] whereArgs,int curPage,int pageSize){
		TableEntity tableEntity = TableEntityManager.getTableEntity(mClass);
		int length = whereArgs == null ? 0 : whereArgs.length;
		String[] newWhereArgs = new String[length + 3];
		if(length > 0) {
			System.arraycopy(whereArgs, 0, newWhereArgs, 0, whereArgs.length);
		}
		newWhereArgs[length] = String.valueOf(pageSize);
		newWhereArgs[length + 1] = String.valueOf(curPage-1);
		newWhereArgs[length + 2] = String.valueOf(pageSize);
		Cursor cursor = rawQuery(SQLBuilder.getQueryPageSQL(tableEntity,whereClause),newWhereArgs);
		return CursorUtil.parseList(cursor,mClass);
	}

	/**
	 * 查询数据总数
     */
	public <T> int queryCount(Class<T> mClass){
		TableEntity tableEntity = TableEntityManager.getTableEntity(mClass);
		Cursor cursor = rawQuery("select count(1) from " + tableEntity.getTableName(), null);
		return CursorUtil.parseCount(cursor);
	}

	/**
	 * 查询数据总数（自定义查询条件）
	 */
	public <T> int queryCount(Class<T> mClass,String whereClause,String[] args){
		TableEntity tableEntity = TableEntityManager.getTableEntity(mClass);
		Cursor cursor = rawQuery("select count(1) from " + tableEntity.getTableName() + " where "+whereClause,args);
		return CursorUtil.parseCount(cursor);
	}

	/**
	 * 新增执行操作
	 */
	private <T> long insert(String sql,Object[] args){
		SQLiteStatement statement = getSQLiteStatement(sql);
		try {
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					statement.bindString(i + 1, String.valueOf(args[i]));
				}
			}
			return statement.executeInsert();
		}
		finally {
//			statement.close();
		}
	}

	/**
	 * 修改或者删除执行操作
	 * @param sql		修改或者删除的语句(参数使用占位符)
	 * @param args		占位符参数
	 */
	public long updateOrDelete(String sql, Object[] args) {
		SQLiteStatement statement = getSQLiteStatement(sql);
		try {
			if(args != null) {
				for(int i = 0; i < args.length; i++) {
					statement.bindString(i+1,String.valueOf(args[i]));
				}
			}
			return statement.executeUpdateDelete();
		} finally {
//			statement.close();
		}
	}

}
