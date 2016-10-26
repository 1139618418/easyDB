package me.happy.easydb.bean;

import java.util.List;

public class TableEntity {
	/**
	 * 实体类Class对象
	 */
//	private Class<?> mClass;
	/**
	 * 实体类对应的表主键
	 */
	private PrimaryKeyEntity primaryKey;

	/**
	 * 实体类对应的表名
	 */
	private String tableName;
	/**
	 * 实体类对应的列名集合
	 */
	private List<ColumnEntity> columnList;

//	public Class<?> getmClass() {
//		return mClass;
//	}
//
//	public void setmClass(Class<?> mClass) {
//		this.mClass = mClass;
//	}

	public PrimaryKeyEntity getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(PrimaryKeyEntity primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<ColumnEntity> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<ColumnEntity> columnEntityList) {
		this.columnList = columnEntityList;
	}
}
