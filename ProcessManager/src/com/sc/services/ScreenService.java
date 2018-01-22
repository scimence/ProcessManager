
package com.sc.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class ScreenService extends Service
{
	private static boolean isrunning = false;
	private static Intent intent;
	private static Context context;
	
	/** 启动服务 */
	public static void start(Context context)
	{
		if (!isrunning)
		{
			ScreenService.context = context;
			intent = new Intent(context, ScreenService.class);
			context.startService(intent);
			
			Toast.makeText(context, "ScreenService 服务已启动 !", Toast.LENGTH_SHORT).show();
		}
	}
	
	/** 停止服务 */
	public static void stop()
	{
		if (isrunning)
		{
			context.stopService(intent);
			isrunning = false;
			
			Toast.makeText(context, "ScreenService 服务已停止 !", Toast.LENGTH_SHORT).show();
		}
	}
	
	// --------------
	
	private String TAG = this.getClass().getSimpleName();
	
	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate()
	{
		super.onCreate();
		Log.i(TAG, "onCreate");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (!isrunning)
		{
			if (ScreenService.intent == null) ScreenService.intent = intent;
			if (context == null) context = this;
			
			isrunning = true;
			Log.i(TAG, "onStartCommand");
			
			// 执行服务处理逻辑
			doServicesLogic(20000);
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void doServicesLogic(final long delayMillis)
	{
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				if (isrunning)
				{
					Toast.makeText(ScreenService.this, TAG + " is running !", Toast.LENGTH_SHORT).show();
					ServicesTool.serviceLogic(ScreenService.this.getApplicationContext());	// 执行服务处理逻辑
					
					doServicesLogic(delayMillis);
				}
			}
		};
		new Handler().postDelayed(r, delayMillis);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.i(TAG, "onDestroy");
	}
	
}
