package com.price.finance_clock.common;

//import java.util.Date;
import java.util.List;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CmnFunc 
{
	private static Logger logger = LoggerFactory.getLogger(CmnFunc.class);
//	private static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	
	public static boolean check_file_exist(final String filepath)
	{
//		logger.debug(String.format("Check the file exist: %s", filepath));
		logger.debug("Check the file exist: {}", filepath);
		File f = new File(filepath);
		boolean exist = f.exists();
		return exist;
	}

	public static short read_file_lines(BufferedReader br, List<String> line_list)
	{
		short ret = CmnDef.RET_SUCCESS;
		// Read the conf file
		try
		{
			String line = null;
			while ((line = br.readLine()) != null)
			{
				if (line.startsWith("#"))
					continue;
				String line_strip = line.replace("\n", "").replace("\r", "");
				if (line_strip.isEmpty())
					continue;
				line_list.add(line_strip);
			}
		}
		catch (IOException e)
		{
			logger.error(String.format("IO Error occur while parsing the config file, due to: %s", e.toString()));
			ret = CmnDef.RET_FAILURE_IO_OPERATION;
		}
		catch (Exception e)
		{
			logger.error(String.format("Error occur while parsing the config file, due to: %s", e.toString()));
			ret = CmnDef.RET_FAILURE_UNKNOWN;
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (Exception e)
				{
				}
				br = null;
			}
		}
		return ret;
	}

	public static short read_file_lines(String filepath, List<String> line_list)
	{
		File f = new File(filepath);
		if (!f.exists())
		{
			logger.error(String.format("The configration file[%s] does NOT exist", filepath));
			return CmnDef.RET_FAILURE_NOT_FOUND;
		}

		// Open the config file for reading
		BufferedReader br = null;
		try
		{
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
		}
		catch (IOException e)
		{
			logger.error(String.format("Fails to open %s file, due to: %s", filepath, e.toString()));
			return CmnDef.RET_FAILURE_IO_OPERATION;
		}

		return read_file_lines(br, line_list);
	}
}
