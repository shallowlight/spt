package com.totrade.spt.mobile.utility.lettersort;

import java.util.Comparator;

import com.totrade.spt.mobile.entity.Company;

public class PinyinComparator implements Comparator<Company>
{

	public int compare(Company c1, Company c2)
	{
		if (c1.getFirstLetter()=='@' || c2.getFirstLetter()=='#')
			return -1;
		else if (c1.getFirstLetter()=='#'|| c2.getFirstLetter()=='@')
			return 1;
		else
			return c1.getFirstLetter()-c2.getFirstLetter();
	}
}
