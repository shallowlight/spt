package com.totrade.spt.mobile.utility;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.annotation.BindViewId;
import com.totrade.spt.mobile.annotation.EditTextValidator;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.view.customize.DecimalEditText;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AnnotateUtility
{

	public static void initBindView(Activity activity)
	{
		bindViewFormId(activity, activity.getWindow().getDecorView());
	}

	public static void initBindView(Fragment fragment)
	{
		bindViewFormId(fragment, fragment.getView());
	}

	public static void bindViewFormId(Object object, View viewSuper)
	{
		Field[] fields = object.getClass().getDeclaredFields();
		if(fields != null && fields.length>0)
		{
			for(Field field : fields)
			{
				try
				{
					BindViewId ann = field.getAnnotation(BindViewId.class);
					if(ann!=null)
					{
						field.setAccessible(true);
						field.set(object, viewSuper.findViewById(ann.value()));
					}
				}
				catch (Exception e)
				{
					//
				}
			}
		}
	}

	/**
	 *  需要校验的 EditText 排序
	 * @param obj class对象
	 * @return 排序接口
	 */
	private static Comparator<Field> softEditText(final Object obj)
	{
		Comparator<Field> comparator = new Comparator<Field>()
		{
			@Override
			public int compare(Field lhs, Field rhs)
			{
				try
				{
					EditText editText1 = (EditText) lhs.get(obj);
					EditText editText2 = (EditText) rhs.get(obj);
					int[] position1 = new int[2];
					editText1.getLocationInWindow(position1);
					int[] position2 = new int[2];
					editText2.getLocationInWindow(position2);
					int validSequence = lhs.getAnnotation(EditTextValidator.class).validSequence()
							- rhs.getAnnotation(EditTextValidator.class).validSequence();
					if(validSequence != 0)		//如果已设置排序顺序则按排序顺序
					{
						return validSequence;
					}
					//位设置排位顺序则按位置比较，优先从上到下
					if(position2[1] - position1[1]> editText2.getHeight()
							|| position1[1] - position2[1]> editText1.getHeight())
					{
						//y值有较大差距，比较y值，不在同一行
						return position1[1] -position2[1];
					}
					else
					{
						//比较x值
						return position1[0] -position2[0];
					}
				}
				catch(Exception e)
				{
					return 1;
				}
			}
		};
		return comparator;
	}


	public static boolean verifactEditText(final Object obj)
	{
		Field[] fields = obj.getClass().getDeclaredFields();
		List<Field> lst = new ArrayList<>();
		if(fields == null || fields.length<=0) return true;
		//先取出需要校验的对象
		for(Field field : fields)
		{
			try
			{
				EditTextValidator ann = field.getAnnotation(EditTextValidator.class);
				if(ann!=null)
				{
					field.setAccessible(true);
					Object object = field.get(obj);
					if(object!=null && object instanceof EditText)
					{
						//如果是EditText
						EditText editText = (EditText) object;
						int[] position = new int[2];
						editText.getLocationInWindow(position);
						int[] position2 = new int[2];
						editText.getLocationInWindow(position2);
						//本身可能并未不可见，但是外层布局可能有不可见，所以不能用Visibility,而用isShown
						if(editText.isShown())
						{
							//只校验显示状态的EditText
							lst.add(field);
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		//对需要校验的对象进行排序
		Collections.sort(lst, softEditText(obj));


		for(Field field : lst)
		{
			EditTextValidator ann = field.getAnnotation(EditTextValidator.class);
			// lst中的EditTextValidator 一定不为null
			field.setAccessible(true);
			EditText editText = null;
			try
			{
				editText = (EditText) field.get(obj);
			}
			catch (Exception e)
			{
				//
			}
			if(editText == null) continue;
			//一定是EditText,其余已过滤
			String number = editText.getText().toString();
			String errorMsg = null;
			if(StringUtility.isNullOrEmpty(number))		//判断空值
			{
				if(!ann.withEmpty())
				{
					errorMsg = ann.viewName()+"不能为空";
				}
			}
			//如果不为空且输入类型是数字类型的，则判断数字范围
			else if(editText instanceof DecimalEditText)
			{
				DecimalEditText decimalEditText = (DecimalEditText) editText;
				double d = Double.valueOf(number);
				if( d< decimalEditText.getMinNum())
				{
					errorMsg = ann.viewName()+"不能小于" + decimalEditText.getMinNum();
				}
				else if(d > decimalEditText.getMaxNum())
				{
					errorMsg = ann.viewName()+"不能大于" + decimalEditText.getMaxNum();
				}
			}
			//如果判断后错误存在错误信息，则提示错误信息
			if(errorMsg!=null)
			{
				if(editText.getContext() instanceof SptMobileActivityBase)
				{
					ToastHelper.showMessage(errorMsg);
				}
				else
				{
					ToastHelper.showMessage(errorMsg);
				}
				editText.requestFocus();
				return false;
			}
		}
		return true;
	}

}

