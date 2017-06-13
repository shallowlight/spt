package com.totrade.spt.mobile.entity;




import android.text.TextUtils;

import com.netease.nimlib.sdk.team.model.TeamMember;


public class SoftTeamMember
{
	public TeamMember teamMember;
	public String iconUrl;
	public String pinyin;
	public String petName;
	public String getNameInT()
	{
		if(!TextUtils.isEmpty(teamMember.getTeamNick()))
		{
			return teamMember.getTeamNick();
		}
		else
		{
			return petName;
		}
	}
//	
//	public void setTeamMember(TeamMember teamMember) 
//	{
//		this.teamMember = teamMember;
//	}
//	public TeamMember getTeamMember() 
//	{
//		return teamMember;
//	}
//	public String getIconUrl() 
//	{
//		return iconUrl;
//	}
//	public void setIconUrl(String iconUrl)
//	{
//		this.iconUrl = iconUrl;
//	}
//	public String getPinyin() 
//	{
//		return pinyin;
//	}
//	public void setPinyin(String pinyin) 
//	{
//		this.pinyin = pinyin;
//	}
//	
}
