package com.chinaunicom.elemeetingpc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;



/**
 * Copyright Unicom(HN) 2015. All rights reserved.
 *
 * @File: DateUtil.java
 * @Package: com.unicom.core.util
 * @ClassName: DateUtil
 * @Description: 日期工具类
 * @Author Eleazar chenxi chenxu zhangsq zhaoqing
 * @Date 2015年11月3日
 * @Version V1.0
 * 
 */
public class DateUtil
{

    /**
     * @Fields logger: 日志
     * @Since JDK1.6
     */
    private static Logger logger = Logger.getLogger(DateUtil.class.getName());

    /**
     * @Fields DATETIME_FORMAT : 日期格式2015-11-03 16:20
     */
    private static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * @Fields DATE_FORMAT : 日期格式2015-11-03
     */
    private static String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * @Fields TIME_FORMAT : 日期格式2015/11/03/16/20/00
     */
    private static String TIME_FORMAT = "yyyy/MM/dd/HH/mm/ss";

    /**
     * @Fields FULL_DATETIME_FORMAT : 日期格式2015-11-03 16:20:00
     */
    private static String FULL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * @Title: previous
     * @Description: 获取几天前的日期
     * @param days
     * @return
     * @return Date
     * @throws
     *
     */
    public static Date previous(int days)
    {
        return new Date(System.currentTimeMillis() - days * 3600000L * 24L);
    }

    /**
     * @Title: after
     * @Description: 获取几天后的日期
     * @param days
     * @return
     * @return Date
     * @throws
     *
     */
    public static Date after(int days)
    {
        return new Date(System.currentTimeMillis() + days * 3600000L * 24L);
    }

    /**
     * @Title: formatDateTime
     * @Description: 以"yyyy-MM-dd HH:mm"格式获取时间字符串
     * @param d
     * @return
     * @return String
     * @throws
     *
     */
    public static String formatDateTime(Date d)
    {
        return new SimpleDateFormat(DATETIME_FORMAT).format(d);
    }

    /**
     * @Title: formatDateTime
     * @Description: 以"yyyy-MM-dd HH:mm"格式获取时间字符串
     * @param d
     * @return
     * @return String
     * @throws
     *
     */
    public static String formatDateTime(long d)
    {
        return new SimpleDateFormat(DATETIME_FORMAT).format(d);
    }

    /**
     * @Title: formatDate
     * @Description: 以"yyyy-MM-dd"格式获取时间字符串
     * @param d
     * @return
     * @return String
     * @throws
     *
     */
    public static String formatDate(Date d)
    {
        return new SimpleDateFormat(DATE_FORMAT).format(d);
    }

    /**
     * @Title: parseDate
     * @Description: 以"yyyy-MM-dd"格式获取时间字符串
     * @param d
     * @return
     * @return Date
     * @throws
     *
     */
    public static Date parseDate(String d)
    {
        try
        {
            return new SimpleDateFormat(DATE_FORMAT).parse(d);
        } catch (ParseException e)
        {
            e.printStackTrace();
            //logger.error("DateUtil.parseDate抛出异常", e);
        }
        return null;
    }

    /** 
     * @Title: parseDate
     * @Description: 以自定义的格式格式化日期字符串
     * @param dt
     * @param format
     * @return
     * @return Date
     *
     * @See 
     * @Author: Eleazar 
     * @since JDK 1.6 
     *
     */
    public static Date parseDate(String dt, String format)
    {
        try
        {
            return new SimpleDateFormat(format).parse(dt);
        } catch (ParseException e)
        {
            e.printStackTrace();
            //logger..error("DateUtil.parseDateTime抛出异常", e);
        }
        return null;
    }

    /**
     * @Title: parseDateTime
     * @Description: 把"yyyy-MM-dd hh:mm"格式的时间转换成标准时间对象
     * @param dt
     * @return
     * @return Date
     * @throws
     *
     */
    public static Date parseDateTime(String dt)
    {
        try
        {
            return new SimpleDateFormat(DATETIME_FORMAT).parse(dt);
        } catch (ParseException e)
        {
            e.printStackTrace();
            //logger.error("DateUtil.parseDateTime抛出异常", e);
        }
        return null;
    }

    /**
     * @Title: getCurDateTime
     * @Description: 取得当前时间 格式 yyyy-MM-dd HH:mm
     * @return
     * @return String
     * @throws
     *
     */
    public static String getCurDateTime()
    {
        return formatDateTime(new Date());
    }

    /**
     * @Title: getCurDateTimeSend
     * @Description: 取得当前时间 格式 yyyy/MM/dd/HH/mm/ss
     * @return
     * @return String
     * @throws
     *
     */
    public static String getCurDateTimeSend()
    {
        return new SimpleDateFormat(TIME_FORMAT).format(new Date());
    }

    /**
     * @Title: getCurYear
     * @Description: 获取当前服务器年数
     * @return
     * @return String
     * @throws
     *
     */
    public static String getCurYear()
    {
        String date = formatDateTime(new Date());
        String year = date.substring(0, date.indexOf("-"));
        return year;
    }

    /**
     * @Title: getCurmonth
     * @Description: 获取当前服务器月份
     * @return
     * @return int
     * @throws
     *
     */
    @SuppressWarnings("deprecation")
    public static int getCurmonth()
    {
        // String date=formatDateTime(new Date());
        Date date = new Date();
        int month = date.getMonth() + 1;
        return month;
    }

    /**
     * @Title: getCurjd
     * @Description: 获取当前服务器季度
     * @return
     * @return int
     * @throws
     *
     */
    @SuppressWarnings("deprecation")
    public static int getCurjd()
    {
        Date date = new Date();
        int month = date.getMonth() + 1;
        if (month == 1 || month == 2 || month == 3)
            return 1;
        else if (month == 4 || month == 5 || month == 6)
            return 2;
        else if (month == 7 || month == 8 || month == 9)
            return 3;
        else if (month == 10 || month == 11 || month == 12)
            return 4;
        else
            return 0;

    }

    /**
     * @Title: getCurDate
     * @Description: 以"2015-11-03"获取当前日期
     * @return
     * @return String
     * @throws
     *
     */
    public static String getCurDate()
    {
        return new SimpleDateFormat(DATE_FORMAT).format(new Date());
    }

    /**
     * @Title: getWeekOfDate
     * @Description: 获取给定日期是星期几
     * @param date
     * @return
     * @return String
     * @throws
     *
     */
    public static String getWeekOfDate(Date date)
    {
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * @Title: getWeeksOfDate
     * @Description: 得到一周的第几天
     * @param date
     * @return
     * @return int
     * @throws
     *
     */
    public static int getWeeksOfDate(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK);
        return w;
    }

    /**
     * @Title: getDaysOfMonth
     * @Description: 得到一个月份的天数
     * @param date
     * @return
     * @return int
     * @throws
     *
     */
    public static int getDaysOfMonth(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return w;
    }

    /**
     * @Title: getWeeksOfMonth
     * @Description: 得到一个月份的周数
     * @param date
     * @return
     * @return int
     * @throws
     *
     */
    public static int getWeeksOfMonth(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
        return w;
    }

    /**
     * @Title: getNextYear
     * @Description: 返回一年后的时间
     * @param date
     * @return
     * @return String
     * @throws
     *
     */
    public static String getNextYear(Date date)
    {
        long beforeTime = (date.getTime() / 1000) + 60 * 60 * 24 * 365;
        Date nextYear = new Date();
        nextYear.setTime(beforeTime * 1000);
        return formatDate(nextYear);
    }

    /**
     * @Title: getNowOfLastMonth
     * @Description: 返回上个月的今天
     * @return
     * @return String
     * @throws
     *
     */
    public static String getNowOfLastMonth()
    {
        SimpleDateFormat aSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        GregorianCalendar aGregorianCalendar = new GregorianCalendar();
        aGregorianCalendar.set(Calendar.MONTH,
                aGregorianCalendar.get(Calendar.MONTH) - 1);
        String nowOfLastMonth = aSimpleDateFormat
                .format(aGregorianCalendar.getTime());
        return nowOfLastMonth;
    }

    /**
     * @Title: getCompareToday
     * @Description: 获取两个日期相差
     * @param date
     * @param date2
     * @return
     * @return int
     * @throws
     *
     */
    public static int getCompareToday(Date date, Date date2)
    {
        Long result = date2.getTime() / 86400000 - date.getTime() / 86400000; // 用立即数，减少乘法计算的开销
        int t = Long.numberOfLeadingZeros(result);
        return t;
    }

    /**
     * @Title: formatFullDateTime
     * @Description: 把Date对象转成"yyyy-MM-dd HH:mm"的时间字符串
     * @param d
     * @return
     * @return String
     * @throws
     *
     */
    public static String formatFullDateTime(Date d)
    {
        return new SimpleDateFormat(FULL_DATETIME_FORMAT).format(d);
    }

    /**
     * @Title: getFirstDayOfMonth
     * @Description: 获取本月第一天的日期字符串
     * @return
     * @return String
     * @throws
     *
     */
    public static String getFirstDayOfMonth()
    {
        SimpleDateFormat aSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        GregorianCalendar aGregorianCalendar = new GregorianCalendar();
        aGregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
        String nowOfLastMonth = aSimpleDateFormat
                .format(aGregorianCalendar.getTime());
        return nowOfLastMonth;
    }

    /**
     * @Title: dateToString
     * @Description: 把日期对象转为"yyyy-MM-dd HH:mm:ss"格式的字符串
     * @param d
     * @return
     * @return String
     * @throws
     *
     */
    public static String dateToString(Date d)
    {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(d);
    }

    /**
     * @Title: dateToString
     * @Description: 把日期对象转为自定义格式的字符串
     * @param d
     * @param format
     * @return
     * @return String
     * @throws
     *
     */
    public static String dateToString(Date d, String format)
    {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(d);
    }

    /**
     * @Title: addDay
     * @Description: 把给定的日期增加几天
     * @param date
     * @param n
     * @return
     * @return Date
     * @throws
     *
     */
    public static Date addDay(Date date, int n)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, n);
        return calendar.getTime();
    }

    /**
     * @Title: addDay
     * @Description: 得到几天后的日期
     * @param n
     * @return
     * @return Date
     * @throws
     *
     */
    public static Date addDay(int n)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, n);
        return calendar.getTime();
    }

    /**
     * @Title: addMonth
     * @Description: 得到几个月后的日期
     * @param n
     * @return
     * @return Date
     * @throws
     *
     */
    public static Date addMonth(int n)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, n);
        return calendar.getTime();
    }

    /**
     * @Title: getMonthAndLastDay
     * @Description: 获取本月最后一天
     * @param date
     * @return
     * @return Date
     * @throws
     *
     */
    public static Date getMonthAndLastDay(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * @Title: getMonthAndFirstDay
     * @Description: 获取本月第一天
     * @param date
     * @return
     * @return Date
     * @throws
     *
     */
    public static Date getMonthAndFirstDay(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * @Title: addMonth
     * @Description: 得到给定日期几个月后的日期
     * @param date
     * @param n
     * @return
     * @return Date
     * @throws
     *
     */
    public static Date addMonth(Date date, int n)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, n);
        return calendar.getTime();
    }

    /**
     * @Title: addMinute
     * @Description: 得到几分钟后的日期
     * @param date
     * @param n
     * @return
     * @return Date
     * @throws
     *
     */
    public static Date addMinute(Date date, int n)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, n);
        return calendar.getTime();
    }

    /**
     * @Title: addMilliSecond
     * @Description: 得到几毫秒后的日期
     * @param date
     * @param n
     * @return
     * @return Date
     * @throws
     *
     */
    public static Date addMilliSecond(Date date, int n)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, n);
        return calendar.getTime();
    }

    /**
     * @Title: stringToDate
     * @Description: 把字符串根据给定的格式转换为日期对象
     * @param d
     * @param partern
     * @return
     * @throws ParseException
     * @return Date
     * @throws
     *
     */
    public static Date stringToDate(String d, String partern)
            throws ParseException
    {
        SimpleDateFormat format = new SimpleDateFormat(partern);
        return format.parse(d);
    }

    /**
     * @Title: stringToDate
     * @Description: 把字符串根据"yyyy-MM-dd HH:mm:ss"格式转换为日期对象
     * @param d
     * @return
     * @throws ParseException
     * @return Date
     * @throws
     *
     */
    public static Date stringToDate(String d) throws ParseException
    {
        String partern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(partern);
        return format.parse(d);
    }

    /**
     * @Title: StringToLastDate
     * @Description: 得到一天的最后一分钟时间
     * @param d
     * @return
     * @return String
     * @throws
     *
     */
    public static String StringToLastDate(Date d)
    {
        return dateToString(d, "yyyy-MM-dd") + " 23:59:59";
    }

    /**
     * @Title: millisToDate
     * @Description: 把毫秒数转化为时间
     * @param mill
     * @return
     * @return Date
     * @throws
     *
     */
    public static Date millisToDate(long mill)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mill);
        return calendar.getTime();
    }

    /**
     * @Title: DateTomillis
     * @Description: 日期转化为毫秒数
     * @param d
     * @return
     * @return long
     * @throws
     *
     */
    public static long DateTomillis(Date d)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        return calendar.getTimeInMillis();
    }
    /**
     * @Title: afterCurDate
     * @Description: 是否到了截至日期
     * @param closingDate 截至日期
     * @return
     * @return boolean
     * @throws
     *
     */
    public static boolean afterCurDate(String closingDateStr)
    {
        boolean flag = false;
    	Date curDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(FULL_DATETIME_FORMAT);
        Date closingDate;
		try {
			closingDate = sdf.parse(closingDateStr);
			flag = curDate.after(closingDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return flag;
		}
        return flag;
    }
}
