package com.totrade.spt.mobile.view.customize;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 限制非法字符(限制输入128-160范围内的字符)，暂时
 * @author Administrator
 *
 */
public class MyEditText extends EditText
{

	public MyEditText(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public MyEditText(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public MyEditText(Context context)
	{
		super(context);
	}
	private String cs="";
	@Override
	protected void onTextChanged(CharSequence text, int start,int lengthBefore, int lengthAfter)
	{
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		if(!isLawfulString(text.toString()))
		{
			if(cs==null)
				cs="";
			this.setText(cs);
		}
		else
		{
			cs=text.toString();
		}
	}

	public static boolean isLawfulString(String memo)
	{
		if(memo==null || memo.isEmpty())
		{
			return true;
		}
		try
		{
			char[] charArr = memo.toCharArray();
			for(char c:charArr)
			{
				int a=c;
				if(a>128 && a<=160)
				{
					return false;
				}
			}
			return true;
		}
		catch(Exception e)
		{
			return false;
		}

	}

}
