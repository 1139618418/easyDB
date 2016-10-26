package me.happy.easydb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
	/**
	 * 自定义表名
	 * Author: hyl
	 * Time: 2015-8-14下午9:18:12
	 * @return
	 */
	String value() default "";
}
