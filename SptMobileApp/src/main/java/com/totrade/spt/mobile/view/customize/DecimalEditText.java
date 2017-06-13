package com.totrade.spt.mobile.view.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.autrade.stage.utility.StringUtility;
import com.totrade.spt.mobile.common.AppConstant;

import java.util.Map;

/**
 * @author wangzq
 *
 */
public class DecimalEditText extends EditText
{
	private double maxNumber = 100000;
	private double minNum;
	private int digits=2;	//小数位数
	private boolean isStartZero = false;
	private String str = "";

	public double getMinNum()
	{
		return minNum;
	}

	public double getMaxNum()
	{
		return maxNumber;
	}

	public void setMinNum(double minNum)
	{
		this.minNum = minNum;
		this.isStartZero = this.minNum <1;
	}
	private void setMaxNumber(double maxNumber)
	{
		if(maxNumber<0)
		{
			return;
		}
		this.maxNumber = maxNumber;
		String string= getText().toString();
		if(!StringUtility.isNullOrEmpty(string)
				&& Double.valueOf(string)>maxNumber)
		{
			setText("");
		}
	}

	public void setNumCfgMap(Map<String, Double> map)
	{
		if(map == null || map.isEmpty()) return;
		double max = map.get(AppConstant.CFG_NUMBERMAX);
		double minNum = map.get(AppConstant.CFG_NUMBERMIN);
		int digit = map.get(AppConstant.CFG_NUMBERPREC).intValue();
		if(max > 0)
		{
			setMaxNumber(max);
			setDecimalDigits(digit);
			setMinNum(minNum);
		}
	}

	public void setDecimalDigits(int digits)
	{
		this.digits = digits;
		String string= getText().toString();
		if(!StringUtility.isNullOrEmpty(string)
				&& (string.contains(".") && (string.length()-1 - string.indexOf(".")> digits)))
		{
			setText("");
		}
	}

	public void setMaxNumAndDigits(double maxNumber,int digits)
	{
		this.maxNumber=maxNumber;
		this.digits = digits;
		String string= getText().toString();
		if(!StringUtility.isNullOrEmpty(string)
				&& (	Double.valueOf(string) >maxNumber
				||(string.contains(".") && string.length()-1 - string.indexOf(".")> digits))
				)
		{
			setText("");
		}
	}



	public DecimalEditText(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

	}

	public DecimalEditText(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public DecimalEditText(Context context)
	{
		super(context);
	}

	private boolean checkedText(String strInput)//空值//在范围内//小数点在范围内
	{
		if (strInput==null || strInput.isEmpty())
		{
			str = "";
			return true;
		}
		if(strInput.startsWith("0")&&!isStartZero)
		{
			str = "";
			return false;
		}
		if(digits<=0 && strInput.contains("."))
		{
			return false;
		}
		try
		{
			double input= Double.valueOf(strInput);
			if(input<= maxNumber && input>=0)
			{
				if((!strInput.contains("."))|| digits>=strInput.length()-1 - strInput.indexOf("."))
				{
					return true;
				}
			}
		}
		catch(Exception e)
		{
		}
		return false;

	}

	@Override
	protected void onTextChanged(CharSequence text, int start,int lengthBefore, int lengthAfter)
	{
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		if(!checkedText(text.toString()))
		{
			this.setText(str);
			this.setSelection(str.length());
		}
		else
		{
			str=text.toString();
		}
	}

}
