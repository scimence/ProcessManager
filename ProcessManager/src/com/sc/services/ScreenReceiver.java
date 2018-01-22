
package com.sc.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;


public class ScreenReceiver extends BroadcastReceiver
{
	private String TAG = this.getClass().getSimpleName();
	private static ScreenReceiver receiver;
	private static Context context;
	private static boolean isregistered = false;
	
	/** 注册广播信息接收 */
	public static void register(Context context)
	{
		if (!isregistered)
		{
			receiver = new ScreenReceiver();
			ScreenReceiver.context = context;
			
			// 注册广播接收器
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			filter.addAction(Intent.ACTION_USER_PRESENT);
			filter.addAction(Intent.ACTION_BOOT_COMPLETED);
			
			context.registerReceiver(receiver, filter);
			
			isregistered = true;
			Toast.makeText(context, "ScreenReceiver 广播接收已启动 !", Toast.LENGTH_SHORT).show();
		}
	}
	
	/** 解除注册广播信息接收 */
	public static void unRegister()
	{
		if (isregistered)
		{
			context.unregisterReceiver(receiver);
			isregistered = false;
			Toast.makeText(context, "ScreenReceiver 广播接收已停止!", Toast.LENGTH_SHORT).show();
		}
	}
	
	//--------------------
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		
		if (action.equals(Intent.ACTION_SCREEN_ON))			// 开屏
		{
			Log.i(TAG, "screen on");
			ScreenService.start(context);
		}
		else if (action.equals(Intent.ACTION_SCREEN_OFF))	// 锁屏
		{
			Log.i(TAG, "screen off");
			ScreenService.start(context);
		}
		else if (action.equals(Intent.ACTION_USER_PRESENT))	// 解锁
		{
			Log.i(TAG, "user present");
			ScreenService.start(context);
		}
		else if (action.equals(Intent.ACTION_BOOT_COMPLETED))// 开机
		{
			Toast.makeText(context, "ACTION_BOOT_COMPLETED 启动服务!", Toast.LENGTH_SHORT).show();
			ScreenService.start(context);
//			StartApp();
		}
	}
	
//	/** 启动应用 */
//	public static void StartApp()
//	{
//		ComponentName component = new ComponentName("com.sc.services", "com.sc.services.MainActivity");
//		
//		Intent intent = new Intent();
//		intent.setComponent(component);
//		context.startActivity(intent);
//	}
}
