package com.totrade.spt.mobile.utility;

import com.totrade.spt.mobile.entity.Day;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarUtil {
	private static Calendar cal;
	static int year;
	static int month;
	public static Day today;

	static {
		cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		today = new Day(year, month, cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK));
	}

	static int oldP;

	public static List<Day> getData(int position) {
		position -= 100;
		cal = Calendar.getInstance();
		// 指定年月日
		List<Day> list = new ArrayList<>();
		month = month + position - oldP;
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, 01);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1号是周几
		int maxDate = cal.getActualMaximum(Calendar.DATE); // 一个月有几天
		for (int i = 0; i < maxDate; i++) {
			Day date = new Day(year, month, i + 1, dayOfWeek);
			list.add(date);
		}
		for (int i = 1; i < dayOfWeek; i++) {
			list.add(0, new Day(getYear(), getMonth(), 0, dayOfWeek));
		}
		oldP = position;
		return list;
	}

	public static int getYear() {
		return cal.get(Calendar.YEAR);
	}

	public static int getMonth() {
		return cal.get(Calendar.MONTH) + 1;
	}

	public static Day getChooseDate(Day dateBean) {
		Calendar cl = Calendar.getInstance();
		cl.set(dateBean.getYear(), dateBean.getmonth(), dateBean.getDay());
//		return new DateBean(cl.get(Calendar.YEAR), cl.get(Calendar.MONTH), cl.get(Calendar.DATE), dateBean.getDayOfWeek());
		return new Day(cl.get(Calendar.YEAR), cl.get(Calendar.MONTH), cl.get(Calendar.DATE), cl.get(Calendar.DAY_OF_WEEK));
	}
}
