package me.happy.easydb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主键注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryKey {
	/**
	 * 配置该字段映射到数据库中的列名，不配置默认为字段名
	 */
	String value() default "";
	/**
	 * 该主键是否设置为自增长，默认为否
	 */
	boolean isAutoGenerate() default false;
}
