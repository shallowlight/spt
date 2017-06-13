package com.totrade.spt.mobile.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EditTextValidator
{
	boolean withEmpty() default false;		//是否允许空值
	int validSequence() default 0;			//校验排序,相同时按位置从上到下 从左到右排序
	String viewName();						//EditText对应名称，用于错误信息提示
}
