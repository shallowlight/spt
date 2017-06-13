package com.totrade.spt.mobile.view;

import com.autrade.spt.common.constants.SptConstant;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest
{
    @Test
    public void addition_isCorrect() throws Exception
    {
        String str = "AAA|BBB|CCC|DDD|EEE|FFFF|GGGG";
        String[] strs = str.split(SptConstant.SEP);
        for (String s:strs)
        {
            System.out.println(s);
        }
    }
}