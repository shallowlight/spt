package com.totrade.spt.mobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.totrade.spt.mobile.adapter.CalendarLvAdapter;
import com.totrade.spt.mobile.adapter.CalendarLvAdapter.OnCalendarOrderListener;
import com.totrade.spt.mobile.base.SptMobileActivityBase;
import com.totrade.spt.mobile.entity.Day;
import com.totrade.spt.mobile.view.customize.CommonTitle3;

public class CalendarActivity extends SptMobileActivityBase {

    private ListView listview;
    private String dateStr;
    private CalendarLvAdapter adapter;
    protected int DATESELECT_RESULTCODE = 77;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        listview = (ListView) findViewById(R.id.lvCalendar);
        adapter = new CalendarLvAdapter(this);
        listview.setAdapter(adapter);
        listview.setSelection(100);
        adapter.setOnCalendarOrderListener(new OnCalendarOrderListener() {
            @Override
            public void onOrder(Day day) {
                // 过滤gridview 1号之前的date
                if (day.getDayOfWeek() != 0) {
                    dateStr = day.getYear() + "/" + (day.getmonth() + 1) + "/" + day.getDay();
                }
            }
        });

        CommonTitle3 title = (CommonTitle3) findViewById(R.id.title);
        title.getImgBack().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                putAndFinish();
            }

        });

        title.getTitleRightImg(R.drawable.today).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 回到今天
                listview.setSelection(100);
            }
        });

        setDay();
    }

    /**
     * 设置默认值
     */
    private void setDay() {
        String datastr = getIntent().getStringExtra("datastr");
        String[] split = datastr.split("/");
        Day day = new Day(Integer.valueOf(split[0]), Integer.valueOf(split[1]) - 1, Integer.valueOf(split[2]), Integer.valueOf(split[0]));
        adapter.setChooseDay(day);
    }


    private void putAndFinish() {
        Intent i = new Intent();
        i.putExtra("dateselect", dateStr);
        setResult(DATESELECT_RESULTCODE, i);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            putAndFinish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
