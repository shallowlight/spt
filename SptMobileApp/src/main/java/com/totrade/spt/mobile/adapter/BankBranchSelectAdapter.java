package com.totrade.spt.mobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.totrade.spt.mobile.entity.BankBranch;
import com.totrade.spt.mobile.view.R;

import java.util.List;

public class BankBranchSelectAdapter extends SptMobileAdapterBase<BankBranch>
{

	public BankBranchSelectAdapter(Context context, List<BankBranch> dataList)
	{
		super(context, dataList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = View.inflate(getContext(), R.layout.item_listview_bank_branch, null);
			vh = new ViewHolder();
			vh.tvLetter = (TextView) convertView.findViewById(R.id.tvLetter);
			vh.tvName = (TextView) convertView.findViewById(R.id.tvName);
			convertView.setTag(vh);
		} else
		{
			vh = (ViewHolder) convertView.getTag();
		}

		BankBranch bankBranch = getDataList().get(position);
		vh.tvName.setText(bankBranch.entity.getBankName());

		if (isIndexFirst(position))
		{
			vh.tvLetter.setVisibility(View.VISIBLE);
			vh.tvLetter.setText(String.valueOf(bankBranch.getFirstLetter()));
		} else
		{
			vh.tvLetter.setVisibility(View.GONE);
		}

		return convertView;
	}

	private ViewHolder vh;

	private static class ViewHolder
	{
		TextView tvName, tvLetter;
	}

	/** 某字母索引下第一个值 */
	private boolean isIndexFirst(int index)
	{
		if (index == 0)
		{
			return true;
		}
		char charLast = getDataList().get(index).getFirstLetter();
		char cBefore = getDataList().get(index - 1).getFirstLetter();
		// 上一项的首字母在A-Z之间，且当前字母与上一字母不相同
		return (cBefore != charLast && cBefore <= 'Z' && cBefore >= 'A');
	}

	/** 特殊字符 */
	private int getPositionForSpecialSharacter()
	{
		int last = getDataList().size();
		for (int i = last - 1; i >= 0; i--)
		{
			char c4 = getDataList().get(i).getFirstLetter();
			if (c4 < 91 && c4 > 64)
			{
				return Math.min(last - 1, i + 1);
			}
		}
		return last;
	}

	/** 通过首字母查找序号 */
	public int getPositionForSection(char c)
	{
		if (c > 91 || c < 64) // 特殊符号
		{
			return getPositionForSpecialSharacter();
		}
		List<BankBranch> lst = getDataList();
		int size = lst.size();
		for (int i = 0; i < size; i++)
		{
			if (lst.get(i).getFirstLetter() >= c || lst.get(i).getFirstLetter() >= 'Z')
			{
				return i;
			}
		}
		// 数据中所有字母之后的索引字母
		return getPositionForSpecialSharacter() - 1;
	}

}
