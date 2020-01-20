package com.price.finance_clock.common;

public class CmnDef 
{
// Return value
	public static final short RET_SUCCESS = 0;

	public static final short RET_FAILURE_WARN_BASE = 0x1;
//	public static final short RET_FAILURE_WARN_INDEX_DUPLICATE = RET_FAILURE_WARN_BASE + 1;
//	public static final short RET_FAILURE_WARN_INDEX_IGNORE = RET_FAILURE_WARN_BASE + 2;
//	public static final short RET_FAILURE_WARN_PROCESS_CONTINUE = RET_FAILURE_WARN_BASE + 3;

	public static final short RET_FAILURE_BASE = 0x100;
	public static final short RET_FAILURE_UNKNOWN = RET_FAILURE_BASE + 1;
	public static final short RET_FAILURE_INVALID_ARGUMENT = RET_FAILURE_BASE + 2;
	public static final short RET_FAILURE_INVALID_POINTER = RET_FAILURE_BASE + 3;
	public static final short RET_FAILURE_INSUFFICIENT_MEMORY = RET_FAILURE_BASE + 4;
	public static final short RET_FAILURE_INCORRECT_OPERATION = RET_FAILURE_BASE + 5;
	public static final short RET_FAILURE_NOT_FOUND = RET_FAILURE_BASE + 6;
	public static final short RET_FAILURE_INCORRECT_CONFIG = RET_FAILURE_BASE + 7;
	public static final short RET_FAILURE_HANDLE_THREAD = RET_FAILURE_BASE + 8;
	public static final short RET_FAILURE_INCORRECT_PATH = RET_FAILURE_BASE + 9;
	public static final short RET_FAILURE_IO_OPERATION = RET_FAILURE_BASE + 10;
	public static final short RET_FAILURE_UNEXPECTED_VALUE = RET_FAILURE_BASE + 11;

	public static final String TIME_TABLE_CONFIG_FILENAME = "time_table.conf";

	public static boolean CheckSuccess(short x)
	{
		return (x == RET_SUCCESS ? true : false);
	}

	public static boolean CheckFailure(short x)
	{
		return !CheckSuccess(x);
	}

	private static final String[] ErrorRetDescription = new String[]
	{
		"Failure Base", 
		"Failure Unknown", 
		"Failure Invalid Argument", 
		"Failure Invalid Pointer", 
		"Failure Insufficient Memory", 
		"Failure Incorrect Operation", 
		"Failure Not Found", 
		"Failure Incorrect Config", 
		"Failure Handle Thread", 
		"Failure Incorrect Path", 
		"Failure IO Operation", 
		"Failure Unexpected Value"
	};

	public static String GetErrorDescription(short error_code)
	{
		return ErrorRetDescription[error_code - RET_FAILURE_BASE];
	}

// variable definition
	public static String TIME_TABLE_CONFIG_NEW_ENTRY_FLAG = "[clock]";
	public static String DEF_CONFIG_FOLDER_NAME = "conf";
}
