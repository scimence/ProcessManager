
package com.sc.processmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;


// 存储进程信息
class processInfo
{
	public Drawable appIcon;  		// 应用程序图像
	public String appName;    		// 应用名称
	public String pkgName;    		// 应用包名
	
	public int pid;  				// 该应用程序所在的进程号
	public String processName;  	// 该应用程序所在的进程名
	public String memmorySize;  	// 该应用程序占用的内存
	public boolean checked = true;
	
	public processInfo()
	{}
	
	public processInfo(ApplicationInfo app, PackageManager pm, int pid, String processName, String memmorySize)
	{
		this.appName = (String) app.loadLabel(pm);
		this.appIcon = app.loadIcon(pm);
		this.pkgName = app.packageName;
		
		this.pid = pid;
		this.processName = processName;
		this.memmorySize = memmorySize;
	}
}


class processInfoObj
{
	ImageView appIcon;
	TextView appName;
	TextView packageName;
	TextView processId;
	TextView processName;
	TextView textMemorySize;
	CheckBox checkBox;
	
	public processInfoObj(final View view)
	{
		this.appIcon = (ImageView) view.findViewById(R.id.imageIcon);
		this.appName = (TextView) view.findViewById(R.id.textAppName);
		this.packageName = (TextView) view.findViewById(R.id.textPackageName);
		this.processId = (TextView) view.findViewById(R.id.textProcessId);
		this.processName = (TextView) view.findViewById(R.id.textProcessName);
		this.textMemorySize = (TextView) view.findViewById(R.id.textMemorySize);
		this.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
		
		this.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				ProcessTool.setValue(view.getContext(), processName.getText().toString(), isChecked + "");	// 记录选中信息
			}
		});
	}
	
	public void setInfo(processInfo info)
	{
		appIcon.setImageDrawable(info.appIcon);
		appName.setText(info.appName);
		packageName.setText(info.pkgName);
		processId.setText(info.pid + "");
		processName.setText(info.processName);
		textMemorySize.setText(info.memmorySize);
		checkBox.setChecked(info.checked);
	}
}


// 进程信息列表项适配，提供给listView的自定义view
public class ProcessInfoAdapter extends BaseAdapter
{
	public List<processInfo> processInfos = null;			// 存储进程信息
	LayoutInflater infater = null;
	Context context;
	
	public ProcessInfoAdapter(Context context)
	{
		this.context = context;
		infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.processInfos = getAllRunningAppInfo(context);
	}
	
	private void Reinit()
	{
		infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.processInfos = getAllRunningAppInfo(context);
	}
	
	// 获取列表项数目
	public int getCount()
	{
		System.out.println("系统正在运行的进程数目：" + processInfos.size());
		return processInfos.size();
	}
	
	// 获取列表项信息
	public Object getItem(int position)
	{
		return processInfos.get(position);
	}
	
	public long getItemId(int position)
	{
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// System.out.println("getView at " + position);
		
		View view;
		processInfoObj obj;
		
		if (convertView == null || convertView.getTag() == null)
		{
			view = infater.inflate(R.layout.process_item, null);
			obj = new processInfoObj(view);
			view.setTag(obj);
		}
		else
		{
			view = convertView;
			obj = (processInfoObj) convertView.getTag();
			
			if (this.context == null)
			{
				this.context = convertView.getContext().getApplicationContext();
				Reinit();
			}
		}
		
		processInfo info = (processInfo) getItem(position);
		obj.setInfo(info);
		
		return view;
	}
	
	// ------------------------
	
	// 获取所有正在运行的应用程序信息
	@SuppressLint("NewApi")
	private List<processInfo> getAllRunningAppInfo(Context context)
	{
		PackageManager pm = context.getPackageManager();
		
		// 查询所有已经安装的应用程序
		List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(pm));	// 排序
		
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);		// 获取ActivityManager
		List<ActivityManager.RunningAppProcessInfo> runningProcess = activityManager.getRunningAppProcesses();		// 正在运行的进程
		
		// 保存所有正在运行的包名及它所在的进程信息
		Map<String, ActivityManager.RunningAppProcessInfo> processMap = new HashMap<String, ActivityManager.RunningAppProcessInfo>();
		for (ActivityManager.RunningAppProcessInfo process : runningProcess)
		{
			for (String pkgName : process.pkgList)
				processMap.put(pkgName, process);
		}
		
		// 保存所有正在运行的应用程序信息
		List<processInfo> runningAppInfos = new ArrayList<processInfo>(); // 保存过滤查到的AppInfo
		for (ApplicationInfo app : listAppcations)
		{
			// 如果该包名存在 则构造一个RunningAppInfo对象
			if (processMap.containsKey(app.packageName))
			{
				ActivityManager.RunningAppProcessInfo processInfo = processMap.get(app.packageName);			// 获取正在运行的进程信息
				String memorySize = getMemoryUsed(processInfo.pid, activityManager);							// 获取占用内存
				
				processInfo info = new processInfo(app, pm, processInfo.pid, processInfo.processName, memorySize);
				runningAppInfos.add(info);
				
				info.checked = isChecked(context, processInfo.processName);
			}
		}
		
		return runningAppInfos;
	}
	
	// 获取对应进程的设置信息
	private boolean isChecked(Context context, String processName)
	{
		boolean checked = true;
		
		String isChecked = ProcessTool.getValue(context, processName);		// 获取设置信息
		if (processName.equals("com.sc.processmanager") && isChecked.equals(""))
			checked = false;	// 当前应用的进程默认不选中
		else checked = (isChecked.equals("true") || isChecked.equals(""));
		
		return checked;
	}
	
	// 结束进程信息
	public void killProcess(/* Context context */)
	{
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);// 获取ActivityManager
		// String packageName = ((Activity) context).getApplicationInfo().packageName;
		for (processInfo info : processInfos)
		{
			boolean checked = isChecked(context, info.processName);
			if (checked)
			{
				activityManager.killBackgroundProcesses(info.pkgName);
				activityManager.killBackgroundProcesses(info.processName);
				android.os.Process.killProcess(info.pid);
			}
		}
	}
	
	// 获取进程pid占用的内存
	private String getMemoryUsed(int pid, ActivityManager activityManager)
	{
		// 获得该进程占用的内存
		int[] pids = new int[] { pid };
		Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(pids);	// 获取进程的内存信息
		
		// 获取进程占内存用信息 kb单位
		// float memSize = memoryInfo[0].dalvikPrivateDirty / 1024;
		
		// DecimalFormat formater = new java.text.DecimalFormat("#.00");
		// return formater.format(memSize) + " MB";
		
		return memoryInfo[0].dalvikPrivateDirty + " KB";
	}
	
	// // 某一特定进程里所有正在运行的应用程序
	// private List<processInfo> querySpecailPIDRunningAppInfo(Context context)
	// {
	// // 保存所有正在运行的应用程序信息
	// List<processInfo> processInfos = new ArrayList<processInfo>(); // 保存过滤查到的进程信息
	// PackageManager pm = context.getPackageManager();
	//
	// Intent intent = ((Activity) context).getIntent(); // 查询某一特定进程的所有应用程序
	// int pid = intent.getIntExtra("EXTRA_PROCESS_ID", -1); // 是否查询某一特定pid的应用程序
	//
	// if (pid != -1)
	// {
	// String[] pkgNameList = intent.getStringArrayExtra("EXTRA_PKGNAMELIST");
	// String processName = intent.getStringExtra("EXTRA_PROCESS_NAME");
	//
	// // textTittle.setText("进程id为" + pid + " 运行的应用程序共有  :  " + pkgNameList.length);
	// for (String packageName : pkgNameList)
	// {
	// try
	// {
	// ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0); // 查询包名对应的Application信息
	// processInfos.add(new processInfo(appInfo, pm, pid, processName));
	// }
	// catch (NameNotFoundException e)
	// {}
	// }
	// }
	//
	// return processInfos;
	// }
}
