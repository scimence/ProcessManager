

package com.sc.services;

import com.sc.processmanager.ProcessInfoActivity;
import com.sc.processmanager.ProcessInfoAdapter;

import android.content.Context;


public class ServicesTool
{
	// 通过定时服务，在锁屏、解屏时展示广告
	public static void startService(Context context)
	{
		ScreenService.start(context);
		ScreenReceiver.register(context);
	}
	
	// 通过定时服务，在锁屏、解屏时展示广告
	public static void stopService()
	{
		ScreenService.stop();
		ScreenReceiver.unRegister();
	}
	
	// 定义服务处理逻辑
	public static void serviceLogic(Context context)
	{
		ProcessInfoAdapter adapter = new ProcessInfoAdapter(context);
		adapter.killProcess();
		
		adapter = new ProcessInfoAdapter(context);
		ProcessInfoActivity.setAdapter(adapter);
		// Toast.makeText(ScreenService.this, TAG + " is running !", Toast.LENGTH_SHORT).show();
	}
}
