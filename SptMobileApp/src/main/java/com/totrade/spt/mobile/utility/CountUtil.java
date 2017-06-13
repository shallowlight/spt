package com.totrade.spt.mobile.utility;

/**
 * Created by Timothy on 2017/2/28.
 */

public class CountUtil {

    public enum IndexSegments {
        TENTHOUSAND, THOUSAND, HUNDRED, TEN, INDIVIDUAL
    }

    public static int searchMax(int[] array) {
        int maxIndex = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > array[maxIndex])
                maxIndex = i;
        }
        return maxIndex;
    }

    public static int searchMin(int[] array) {
        int minIndex = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < array[minIndex])
                minIndex = i;
        }
        return minIndex;
    }

    public static int getMaxBoundary(int max) {

        int maxBoundarys = 0;
        IndexSegments segments = scope(max);
        switch (segments) {
            case INDIVIDUAL:
            case TEN:
                maxBoundarys = upInt(max, 10);
                break;
            case HUNDRED:
                maxBoundarys = upInt(max, 10);
                break;
            case THOUSAND:
                maxBoundarys = upInt(max, 100);
                break;
            case TENTHOUSAND:
                maxBoundarys = upInt(max, 1000);
                break;
        }
        return maxBoundarys;
    }

    public static int getMinBoundary(int min) {

        int minBoundary = 0;
        IndexSegments segments = scope(min);
        switch (segments) {
            case INDIVIDUAL:
                minBoundary = 0;
                break;
            case TEN:
            case HUNDRED:
                minBoundary = downInt(min, 10);
                break;
            case THOUSAND:
                minBoundary = downInt(min, 100);
                break;
            case TENTHOUSAND:
                minBoundary = downInt(min, 1000);
                break;
        }
        return minBoundary;
    }

    /**
     * 此处根据传进来的值,和等距差值，得到区间
     * @param value
     * @return
     */
    private static IndexSegments scope(int value) {
        IndexSegments indexsegment = null;
        if (value < 10 && value >= 0) {
            indexsegment = IndexSegments.INDIVIDUAL;
        } else if (value < 100 && value >= 10) {
            indexsegment = IndexSegments.TEN;
        } else if (value < 1000 && value >= 100) {
            indexsegment = IndexSegments.HUNDRED;
        } else if (value < 10000 && value >= 1000) {
            indexsegment = IndexSegments.THOUSAND;
        } else if (value < 100000 && value >= 10000) {
            indexsegment = IndexSegments.TENTHOUSAND;
        }
        return indexsegment;
    }

    private static int upInt(int max, int offset) {
        if (offset == 0) throw new IllegalArgumentException("偏移值不可为0，只能为1，10，100....");
        return max + offset - max % offset;
    }

    private static int downInt(int min, int offset) {
        if (offset == 0) throw new IllegalArgumentException("偏移值不可为0，只能为1，10，100....");
        return min - min % offset;
    }

}
