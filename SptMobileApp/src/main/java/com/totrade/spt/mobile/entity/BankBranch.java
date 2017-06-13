package com.totrade.spt.mobile.entity;

import com.autrade.spt.bank.entity.TblBankDetailEntity;

public class BankBranch
{
	private char firstLetter;
	public TblBankDetailEntity entity;

	public BankBranch(char c, TblBankDetailEntity entity)
	{
		if (c < 'z' && c > 'a')
		{
			c -= 32;
		}
		this.firstLetter = c;
		this.entity = entity;
	}

	public char getFirstLetter()
	{
		return firstLetter;
	}

	public void setFirstLetter(char firstLetter)
	{
		if (firstLetter < 'z' && firstLetter > 'a')
		{
			firstLetter -= 32;
		}
		this.firstLetter = firstLetter;
	}

}
