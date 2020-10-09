package com.price.finance_clock.libs;

import com.price.finance_clock.common.CmnDef;
import com.price.finance_clock.common.CmnFunc;
import com.price.finance_clock.common.CmnVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DailyTimeTable 
{
	static enum DateTimeUnit
	{
		DateTimeUnit_Undefined(-1),
		DateTimeUnit_Time(0), // 12:35
		DateTimeUnit_Weekday(1), // Sun(1), Mon(2), Tue(3), Wed(4), Thur(5), Fri(6), Sat(7)
		DateTimeUnit_Date(2), // 12/15
		DateTimeUnit_Day(3), // 12; for repeat mode only
		DateTimeUnit_Size(4);

		private int value = 0;
		
		private DateTimeUnit(int value)
		{
			this.value = value;
		}
		
		static DateTimeUnit valueOf(int value)
		{
			switch (value)
			{
				case 0:
					return DateTimeUnit_Time;
				case 1:
					return DateTimeUnit_Weekday;
				case 2:
					return DateTimeUnit_Date;
				case 3:
					return DateTimeUnit_Day;
				default:
					return null;
			}
		}
		
		public int value()
		{
			return this.value;
		}
	};

	public static class ClockEntry
	{
		private String timeStr;
		private boolean repeat;
		private int action;
		private String description;

		public String getTimeStr() {
			return timeStr;
		}
		public void setTimeStr(String timeStr) {
			this.timeStr = timeStr;
		}

		public boolean isRepeat() {
			return repeat;
		}
		public void setRepeat(boolean repeat) {
			this.repeat = repeat;
		}

		public int getAction() {
			return action;
		}
		public void setAction(int action) {
			this.action = action;
		}

		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
		public ClockEntry(String time_str, boolean repeat, int action, String description)
		{
			this.time_str = time_str;
			this.repeat = repeat;
			this.action = action;
			this.description = description;
		}

		public ClockEntry(String time_str, boolean repeat, int action)
		{
			this(time_str, repeat, action, "");
		}
	};


	public static class ClockRuntimeEntry
	{
		private Date datetime;
		private int action;
		private String description;

		ClockRuntimeEntry(Date datetime, int action, String description)
		{
			this.datetime = datetime;
			this.action = action;
			this.description = description;
		}
		
		public Date getDatetime() {
			return datetime;
		}
		public int getAction() {
			return action;
		}
		public String getDescription() {
			return description;
		}
	};
	
	
	private static interface DateTimeUnitInf
	{
		static int DEF_HOUR = 0;
		static int DEF_MINUTE = 0;
		static int DEF_SECOND = 0;
		
		void parse_param(String datetime_str);
		DateTimeUnit get_unit();
		Date get_date_obj();
	}
	
	private static class DateTimeUnitTime implements DateTimeUnitInf
	{
		static Pattern pattern = Pattern.compile("([\\d]{2}):([\\d]{2})");
		int hour;
		int minute;
//		int second;
	
		public void parse_param(String datetime_str)
		{
			Matcher matcher = pattern.matcher(datetime_str);
			if (!matcher.find())
				throw new IllegalArgumentException(String.format("Incorrect time string format: %s", datetime_str));
			hour = Integer.parseInt(matcher.group(1));
			minute = Integer.parseInt(matcher.group(2));
		}
		
		public DateTimeUnit get_unit(){return DateTimeUnit.DateTimeUnit_Time;}

		public Date get_date_obj()
		{
			Calendar cal = (Calendar)Calendar.getInstance();
//	        cal.set(Calendar.HOUR, hour); // Calendar.HOUR is strictly for 12 hours.
	        cal.set(Calendar.HOUR_OF_DAY, hour); // Calendar.HOUR_OF_DAY is used for the 24-hour clock
	        cal.set(Calendar.MINUTE, minute);
	        cal.set(Calendar.SECOND, DEF_SECOND); 
//	        cal.set(Calendar.SECOND, second); 
	        return cal.getTime();
		}
	}
	
	private static class DateTimeUnitWeekday implements DateTimeUnitInf
	{
		static String[] week_name_arr = new String[]{"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
		int weekday; // Start from 0(Sun)
		
		public void parse_param(String datetime_str)
		{
			boolean weekday_found = false;
			weekday = 0;
			for (String week_name : week_name_arr)
			{
				Pattern p = Pattern.compile(week_name, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(datetime_str);
				weekday++;
				if (m.find())
				{
					weekday_found = true;
					break;
				}
			}
			if (!weekday_found)
				throw new IllegalArgumentException(String.format("Incorrect weekday string format: %s", datetime_str));
		}

		public DateTimeUnit get_unit(){return DateTimeUnit.DateTimeUnit_Weekday;}

		public Date get_date_obj()
		{
			Calendar cal = (Calendar)Calendar.getInstance();
			int cur_weekday = cal.get(Calendar.DAY_OF_WEEK);
			int day_offset = 0;
			if (weekday > cur_weekday)
				day_offset = weekday - cur_weekday;
			else if (weekday < cur_weekday)
				day_offset = 7 + weekday - cur_weekday;
	        cal.add(Calendar.DATE, day_offset); 
	        cal.set(Calendar.HOUR_OF_DAY, DEF_HOUR); // Calendar.HOUR_OF_DAY is used for the 24-hour clock
	        cal.set(Calendar.MINUTE, DEF_MINUTE);
	        cal.set(Calendar.SECOND, DEF_SECOND); 
	        return cal.getTime();
		}
	}
	
	private static class DateTimeUnitDate implements DateTimeUnitInf
	{
		static Pattern pattern = Pattern.compile("([\\d]{1,2})/([\\d]{1,2})");
//		int year;
		int month;
		int day;
		
		public void parse_param(String datetime_str)
		{
			Matcher matcher = pattern.matcher(datetime_str);
			if (!matcher.find())
				throw new IllegalArgumentException(String.format("Incorrect date string format: %s", datetime_str));
			month = Integer.parseInt(matcher.group(1));
			day = Integer.parseInt(matcher.group(2));
		}

		public DateTimeUnit get_unit(){return DateTimeUnit.DateTimeUnit_Date;}

		public Date get_date_obj()
		{
			Calendar cal = (Calendar)Calendar.getInstance(); 
//	        cal.set(Calendar.YEAR, year); 
	        cal.set(Calendar.MONTH, month); 
	        cal.set(Calendar.DATE, day); 
	        cal.set(Calendar.HOUR_OF_DAY, DEF_HOUR); // Calendar.HOUR_OF_DAY is used for the 24-hour clock
	        cal.set(Calendar.MINUTE, DEF_MINUTE);
	        cal.set(Calendar.SECOND, DEF_SECOND); 
	        return cal.getTime();
		}
	}
	
	private static class DateTimeUnitDay implements DateTimeUnitInf
	{
		static Pattern pattern = Pattern.compile("([\\d]{1,2})");
		int day;
		
		public void parse_param(String datetime_str)
		{
			Matcher matcher = pattern.matcher(datetime_str);
			if (!matcher.find())
				throw new IllegalArgumentException(String.format("Incorrect day string format: %s", datetime_str));
			day = Integer.parseInt(matcher.group(1));
		}

		public DateTimeUnit get_unit(){return DateTimeUnit.DateTimeUnit_Day;}

		public Date get_date_obj()
		{
			Calendar cal = (Calendar)Calendar.getInstance(); 
	        cal.set(Calendar.DATE, day);
	        cal.set(Calendar.HOUR_OF_DAY, DEF_HOUR); // Calendar.HOUR_OF_DAY is used for the 24-hour clock
	        cal.set(Calendar.MINUTE, DEF_MINUTE);
	        cal.set(Calendar.SECOND, DEF_SECOND); 
	        return cal.getTime();
		}
	}

	private static Logger logger = LoggerFactory.getLogger(DailyTimeTable.class);

	private static DateTimeUnit parse_date_time_unit_from_string(String datetime_str)
	{
// time
		Pattern p_time = Pattern.compile("[\\d]{2}:[\\d]{2}");
		Matcher m_time = p_time.matcher(datetime_str);
		if (m_time.matches())
			return DateTimeUnit.DateTimeUnit_Time;
// week
		Pattern p_week = Pattern.compile("sun|mon|tue|wed|thu|fri|sat", Pattern.CASE_INSENSITIVE);
		Matcher m_week = p_week.matcher(datetime_str);
		if (m_week.matches())
			return DateTimeUnit.DateTimeUnit_Weekday;
// date
		Pattern p_date = Pattern.compile("[\\d]{1,2}/[\\d]{1,2}");
		Matcher m_date = p_date.matcher(datetime_str);
		if (m_date.matches())
			return DateTimeUnit.DateTimeUnit_Date;
// day
		Pattern p_day = Pattern.compile("[\\d]{1,2}");
		Matcher m_day = p_day.matcher(datetime_str);
		if (m_day.matches())
			return DateTimeUnit.DateTimeUnit_Day;
		return DateTimeUnit.DateTimeUnit_Undefined;
	}
	
	static private DateTimeUnitInf get_date_time_obj_from_string(String datetime_str)
	{
		DateTimeUnit date_time_unit = parse_date_time_unit_from_string(datetime_str);
		DateTimeUnitInf date_time_obj = null;
		switch (date_time_unit)
		{
		case DateTimeUnit_Time:
		{
			date_time_obj = new DateTimeUnitTime();
		}
		break;
		case DateTimeUnit_Weekday:
		{
			date_time_obj = new DateTimeUnitWeekday();
		}
		break;
		case DateTimeUnit_Date:
		{
			date_time_obj = new DateTimeUnitDate();
		}
		break;
		case DateTimeUnit_Day:
		{
			date_time_obj = new DateTimeUnitDay();
		}
		break;
		default:
		{
			throw new IllegalArgumentException(String.format("Incorrect time unit: %d", date_time_unit.ordinal()));
		}
		}
// Update the parameters
		date_time_obj.parse_param(datetime_str);
		return date_time_obj;
	}
	
	private DailyTimeTable(){}
	public Object clone() throws CloneNotSupportedException {throw new CloneNotSupportedException();}

	private static DailyTimeTable instance = null;
	public static DailyTimeTable get_instance()
	{
		if (instance == null)
			allocate();
		return instance;
	}

	private static synchronized void allocate() // For thread-safe
	{
		if (instance == null)
		{
			instance = new DailyTimeTable();
			short ret = instance.initialize();
			if (CmnDef.CheckFailure(ret))
			{
				String errmsg = String.format("Fail to initialize the DailyTimeTable object, due to: %s", CmnDef.GetErrorDescription(ret));
				throw new RuntimeException(errmsg);
			}
		} 
	}

	private ArrayList<ClockEntry> clock_entry_list = null;
	private ArrayList<DateTimeUnitInf> clock_data_time_list = null;
	private LinkedList<ClockRuntimeEntry> daily_clock_entry_list = null;
	
	private short initialize()
	{
		short ret = CmnDef.RET_SUCCESS;
		String time_table_config_name = CmnVar.get_time_table_config_filename();

		ArrayList<String> line_list = new ArrayList<String>();
		ret = CmnFunc.read_file_lines(time_table_config_name, line_list);
		if (CmnDef.CheckFailure(ret))
			return ret;
		clock_entry_list = new ArrayList<ClockEntry>();
		ClockEntry clock_entry = null;
		clock_data_time_list = new ArrayList<DateTimeUnitInf>();
		DateTimeUnitInf clock_data_time = null;
		for (String line : line_list)
		{
			if(line.equals(CmnDef.TIME_TABLE_CONFIG_NEW_ENTRY_FLAG))
			{
				clock_entry = new ClockEntry();
				clock_entry_list.add(clock_entry);
			}
			else
			{
				if (Pattern.matches("time .+", line))
				{
					String[] time_elem_array = line.split(" ");
					if (time_elem_array.length != 2)
					{
						logger.error(String.format("Incorrect time config setting: %s", line));
						return CmnDef.RET_FAILURE_INCORRECT_CONFIG;						
					}
					String time_str = time_elem_array[1];
					clock_entry.setTimeStr(time_str);
					clock_data_time = get_date_time_obj_from_string(time_str);
					clock_data_time_list.add(clock_data_time);
				}
				else if (Pattern.matches("repeat .+", line))
				{
					String[] repeat_elem_array = line.split(" ");
					if (repeat_elem_array.length != 2)
					{
						logger.error(String.format("Incorrect repeat config setting: %s", line));
						return CmnDef.RET_FAILURE_INCORRECT_CONFIG;						
					}
					String repeat_str = repeat_elem_array[1];
					Pattern p = Pattern.compile("y|yes|n|no", Pattern.CASE_INSENSITIVE);
					Matcher m = p.matcher(repeat_str);
					if (!m.matches())
					{
						logger.error(String.format("Incorrect config value in 'repeat': %s", repeat_str));
						return CmnDef.RET_FAILURE_INCORRECT_CONFIG;
					}
					boolean repeat = repeat_str.toLowerCase().startsWith("y");
					clock_entry.setRepeat(repeat);
				}
				else if (Pattern.matches("action .+", line))
				{
					String[] action_elem_array = line.split(" ");
					if (action_elem_array.length != 2)
					{
						logger.error(String.format("Incorrect action config setting: %s", line));
						return CmnDef.RET_FAILURE_INCORRECT_CONFIG;						
					}
					String action_str = action_elem_array[1];
					int action;
					try
					{
						action = Integer.parseInt(action_str);
					}
					catch (NumberFormatException e)
					{
						logger.error(String.format("Incorrect config value in 'action': %s", action_str));
						return CmnDef.RET_FAILURE_INCORRECT_CONFIG;						
					}
					clock_entry.setAction(action);
				}
				else if (Pattern.matches("description .+", line))
				{
					String[] description_elem_array = line.split(" ", 2); // Splits the string into 2 parts
					if (description_elem_array.length != 2)
					{
						logger.error(String.format("Incorrect description config setting: %s", line));
						return CmnDef.RET_FAILURE_INCORRECT_CONFIG;						
					}
					String description_raw = description_elem_array[1];
					String description = description_raw.substring(1, description_raw.length() - 1);
					clock_entry.setDescription((description.length() == 0 ? null : description));
				}
				else
				{
					logger.error(String.format("Incorrect config field/setting: %s", line));
					return CmnDef.RET_FAILURE_INCORRECT_CONFIG;	
				}
			}
		}
		if (clock_entry_list.size() != clock_data_time_list.size())
		{
			throw new IllegalArgumentException(String.format("The list lengths are NOT identical; clock_entry_list: %d,  clock_data_time_list: %d", clock_entry_list.size(), clock_data_time_list.size()));
		}
		
		return ret;
	}

	public synchronized short generate_daily_clock()
	{
		short ret = CmnDef.RET_SUCCESS;
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
// Get the current date
		Calendar cal = (Calendar)Calendar.getInstance(); 
        Date cur_date = cal.getTime();
        String cur_date_string = fmt.format(cur_date);
// If the clock date is identical to the current date, insert into list
		int clock_size = clock_entry_list.size();
		daily_clock_entry_list = new LinkedList<ClockRuntimeEntry>();
		for (int i = 0 ; i < clock_size ;i++)
		{
			ClockEntry clock_entry = clock_entry_list.get(i);
			Date clock_date = clock_data_time_list.get(i).get_date_obj();
			if (cur_date_string.equals(fmt.format(clock_date)))
			{
				daily_clock_entry_list.add(new ClockRuntimeEntry(clock_date, clock_entry.getAction(), clock_entry.getDescription()));
			}
		}
		
		Collections.sort(daily_clock_entry_list, new Comparator<ClockRuntimeEntry>() {
		    @Override
		    public int compare(ClockRuntimeEntry lhs, ClockRuntimeEntry rhs) {
		    	int res = lhs.getDatetime().compareTo(rhs.getDatetime());
		    	// -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
		        return res < 0 ? -1 : (res > 0) ? 1 : 0;
		    }
		});

		return ret;
	}


	public synchronized final LinkedList<ClockRuntimeEntry> get_daily_clock()
	{
		if (daily_clock_entry_list == null)
		{
			throw new IllegalStateException("Daily Clock is NOT generated");
		}
		return daily_clock_entry_list;
	}
}
