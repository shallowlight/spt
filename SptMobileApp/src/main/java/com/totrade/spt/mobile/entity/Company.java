package com.totrade.spt.mobile.entity;

public class Company
{
	private char firstLetter; // 名称的首字母
	private String companyTag; // 企业标识
	private String companyName; // 企业名称
	private String companyIndustry; // 企业行业类别
	private boolean isUserSupplier=false;
	private String level;			//企业等级


	public boolean isUserSupplier() {
		return isUserSupplier;
	}

	public void setUserSupplier(boolean isUserSupplier) {
		this.isUserSupplier = isUserSupplier;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Company(char c, String companyTag, String companyName, String companyIndustry, String level)
	{
		if(c<'z' && c>'a')
		{
			c-=32;
		}
		this.firstLetter = c;
		this.companyTag = companyTag;
		this.companyName = companyName;
		this.companyIndustry = companyIndustry;
		this.level = level;
	}

	public char getFirstLetter()
	{
		return firstLetter;
	}

	public void setFirstLetter(char firstLetter)
	{
		if(firstLetter<'z' && firstLetter>'a')
		{
			firstLetter-=32;
		}
		this.firstLetter = firstLetter;
	}

	public String getCompanyTag()
	{
		return companyTag;
	}

	public void setCompanyTag(String companyTag)
	{
		this.companyTag = companyTag;
	}

	public String getCompanyName()
	{
		return companyName;
	}

	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}

	public String getCompanyIndustry()
	{
		return companyIndustry;
	}

	public void setCompanyIndustry(String companyIndustry)
	{
		this.companyIndustry = companyIndustry;
	}

}
