package com.totrade.spt.mobile.entity;


import android.support.annotation.NonNull;

public class NameValueTreeEntity
{
	private int level;
	private String name;
	private String value;
	private String superValue;
	
	public NameValueTreeEntity()
	{
		super();
	}

	public NameValueTreeEntity(int level, String name, String value, String superValue)
	{
		super();
		this.level = level;
		this.name = name;
		this.value = value;
		this.superValue = superValue;
	}
	
	public int getLevel()
	{
		return level;
	}
	public void setLevel(int level)
	{
		this.level = level;
	}
	public @NonNull String getName()
	{
		if(name == null)
		{
			return "";
		}
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getValue()
	{
		return value;
	}
	public void setValue(String value)
	{
		this.value = value;
	}
	public String getSuperValue()
	{
		return superValue;
	}
	public void setSuperValue(String superValue)
	{
		this.superValue = superValue;
	}


	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NameValueTreeEntity other = (NameValueTreeEntity) obj;
		if (level != other.level)
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (superValue == null)
		{
			if (other.superValue != null)
				return false;
		}
		else if (!superValue.equals(other.superValue))
			return false;
		if (value == null)
		{
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
