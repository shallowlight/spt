package com.totrade.spt.mobile.entity;

public class Day {
	int year;
	int month;
	int day;
	int dayOfWeek;

	public Day(int year, int month, int day, int dayOfWeek) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.dayOfWeek = dayOfWeek;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getmonth() {
		return month;
	}

	public void setmonth(int month)
	{
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	@Override
	public String toString() {
		return "DateBean [" + year + "年" + (month + 1) + "月" + day + "日   " + dayOfWeek + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + dayOfWeek;
		result = prime * result + month;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(getDay() == 0){
			return false;
		}
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Day other = (Day) obj;
		if (day != other.day)
			return false;
//		if (dayOfWeek != other.dayOfWeek)
//			return false;
		if (month != other.month)
			return false;
		return year == other.year;
	}

}
