
package com.sc.processmanager;

import android.content.Context;
import android.content.SharedPreferences;


public class ProcessTool
{
	/** 在context中记录键值信息 */
	public static void setValue(Context context, String key, String value)
	{
		SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(ProcessTool.class.getSimpleName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).commit();
	}
	
	/** 获取存储在context中记录键值信息 */
	public static String getValue(Context context, String key)
	{
		SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(ProcessTool.class.getSimpleName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
	}
}
