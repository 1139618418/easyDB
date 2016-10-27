# easyDB

easyDB是基于Android原生SQLiteDatabase封装的ORM映射对象SQLite数据库框架<br> 
一行代码实现数据库操作 简单易用 高效环保

##Features
* Library只有23kb左右
* 支持在SD卡创建数据库
* 支持将项目raw文件的数据库copy到SD卡
* 根据实体对象自动建表、新增字段不影响数据(SQLite3不支持更改列名和删除列)
* 支持注解配置表名、字段名、字段默认值、主键
* 一行代码实现增删改查、更新表结构、分页查询等数据库操作
* 支持原生SQL语句操作数据库

##Sample Code Example
```java
@Table("test")
public class Test {
    //主键可以是int、long或者String
    @PrimaryKey(value = "id",isAutoGenerate = true)
    public int id;

    @Column("boyname")
    public String name;

    @Column(defaultValue = "男")
    public String sex;

    @Column(value = "agee",defaultValue = "18")
    public int age;

    //不配置注解默认获取字段名
    public String likeBook;

    public boolean isGay;
}

//创建数据库
SQLiteDB.getInstance().openOrCreateDB(this,"easy.db","easy");

//创建表
SQLiteDB.getInstance().createTable(Test.class);

//新增表字段
SQLiteDB.getInstance().alterTableColumn(Test.class);

//新增数据（对象或者集合）
SQLiteDB.getInstance().insert(test);

//删除指定表
SQLiteDB.getInstance().dropTable(tableName)

//新增或者修改
SQLiteDB.getInstance().insertOrUpdate(test);

//修改数据（对象或者集合）
SQLiteDB.getInstance().update(test);

//删除数据
SQLiteDB.getInstance().delete(test);

//查询单个
SQLiteDB.getInstance().queryOne(Test.class,"1")

//查询集合
SQLiteDB.getInstance().queryList(Test.class);

//查询分页
SQLiteDB.getInstance().queryPage(Test.class,1,5);

//查询总数
SQLiteDB.getInstance().queryCount(Test.class);

```
##Add to project
Step 1.Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
Step 2. Add the dependency
```
dependencies {
    compile 'com.github.1139618418:easyDB:1.0.0'
}
```

###补充
以上只是简单使用 更多用法请下载代码有详细注释

###参考
[TigerDB](https://github.com/huyongli/TigerDB)<br>

[我的博客](http://blog.csdn.net/u011507982/article)
