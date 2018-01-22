
package com.sc.processmanager;

import com.sc.services.ServicesTool;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;


public class ProcessInfoActivity extends Activity
{
	private static ListView listview = null;
	
	public static void setAdapter(ProcessInfoAdapter adapter)
	{
		if (listview != null && adapter != null) listview.setAdapter(adapter);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listview = (ListView) findViewById(R.id.listviewProcess);
		ProcessInfoAdapter adapter = new ProcessInfoAdapter(this);
		listview.setAdapter(adapter);
	}
	
	// 通过定时服务，在锁屏、解屏时展示广告
	public void startService(View v)
	{
		ServicesTool.startService(this);
		// ScreenService.start(this);
		// ScreenReceiver.register(this);
	}
	
	// 通过定时服务，在锁屏、解屏时展示广告
	public void stopService(View v)
	{
		ServicesTool.stopService();
		// ScreenService.stop();
		// ScreenReceiver.unRegister();
	}
	
	// 通过定时服务，在锁屏、解屏时展示广告
	public void killProcess(View v)
	{
		ProcessInfoAdapter adapter = new ProcessInfoAdapter(this);
		adapter.killProcess();
		
		adapter = new ProcessInfoAdapter(this);
		listview.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
