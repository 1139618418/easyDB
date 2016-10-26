package me.happy.easydemo;

import me.happy.easydb.annotation.Column;
import me.happy.easydb.annotation.PrimaryKey;
import me.happy.easydb.annotation.Table;

/**
 * Created by Administrator on 2016/10/13 0013.
 */
@Table("test")
public class Test {

    @PrimaryKey(value = "id",isAutoGenerate = true)
    public int id;

    @Column("boyname")
    public String name;

    @Column(defaultValue = "男")
    public String sex;

    @Column(value = "agee",defaultValue = "18")
    public int age;

    public boolean isGay;

    //新增一个字段
    @Column("book")
    public String likeBook;

}
