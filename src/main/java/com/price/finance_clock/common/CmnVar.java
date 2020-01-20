package com.price.finance_clock.common;

public class CmnVar 
{
	private static String time_table_config_filename = null;

	public static String get_time_table_config_filename() 
	{
		if (CmnVar.time_table_config_filename == null)
			throw new RuntimeException("time_table_config_filename NOT set");
		return time_table_config_filename;
	}

	public static void set_time_table_config_filename(String time_table_config_filename) 
	{
		if (CmnVar.time_table_config_filename != null)
			throw new RuntimeException(String.format("time_table_config_filename already set: %s", CmnVar.time_table_config_filename));
		CmnVar.time_table_config_filename = time_table_config_filename;
	}
	
}
