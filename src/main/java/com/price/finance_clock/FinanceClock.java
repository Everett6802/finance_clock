package com.price.finance_clock;

import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import com.typesafe.config.Config;
//import com.typesafe.config.ConfigFactory;
//import javax.mail.*;  
//import javax.mail.internet.*;  

import com.price.finance_clock.common.CmnDef;
import com.price.finance_clock.common.CmnVar;
import com.price.finance_clock.libs.DailyTimeTable;
import com.price.finance_clock.libs.DailyTimeTable.ClockRuntimeEntry;

//import com.price.finance_scheduler.common.CmnDef;

public class FinanceClock 
{
	private static Logger logger = LoggerFactory.getLogger(FinanceClock.class);
	
	public static void main(String args[])
	{
		short ret = CmnDef.RET_SUCCESS;
		String time_table_config_filepath = CmnDef.TIME_TABLE_CONFIG_FILENAME;
		if (args.length > 1)
		{
			time_table_config_filepath = args[1];
		}
		else
		{
//		    System.out.println("Working Directory = " + System.getProperty("user.dir"));		
			time_table_config_filepath = String.format("%s/%s/%s", System.getProperty("user.dir"), CmnDef.DEF_CONFIG_FOLDER_NAME, CmnDef.TIME_TABLE_CONFIG_FILENAME);
		}
		CmnVar.set_time_table_config_filename(time_table_config_filepath);
		DailyTimeTable daily_time_table = DailyTimeTable.get_instance();
		ret = daily_time_table.generate_daily_clock();
		if (CmnDef.CheckFailure(ret))
		{
			logger.error(String.format("generate_daily_clock() fails, due to: %s", CmnDef.GetErrorDescription(ret)));
		}

		Iterator<ClockRuntimeEntry> daily_clock_iter = daily_time_table.get_daily_clock().iterator();
		while (daily_clock_iter.hasNext()) 
		{
			ClockRuntimeEntry clock_entry = daily_clock_iter.next();
			System.out.println(clock_entry.getDatetime());
		}
		
//		String time_table_config_filepath = args.length > 1 ? args[1] : CmnDef.SCHEDULER_CONFIG_FILENAME;
////		Config config = ConfigFactory.load();
////		String testcase1 = config.getString("testcase1");
////		logger.error("testcase1: {}", testcase1);
////		String testcase2 = config.getString("testcase2");
////		logger.error("testcase2: {}", testcase2);
		logger.error("testcase1: {}", "Fuck");
		System.exit(0);
	}
}
