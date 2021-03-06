package com.chinaece.gaia.gui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.chinaece.gaia.R;
import com.chinaece.gaia.constant.Gaia;
import com.chinaece.gaia.db.DataStorage;
import com.chinaece.gaia.http.OAHttpApi;
import com.chinaece.gaia.types.AppType;
import com.chinaece.gaia.types.PendingType;

public class PendingsActivity extends ListActivity {
	HashMap<String, String> maps = new HashMap<String, String>();
	private boolean flag = true;
	private ArrayList<String> wrong = new ArrayList<String>();
	private Collection<PendingType> pendings = new ArrayList<PendingType>();
	ArrayList<String> names = new ArrayList<String>();
	
	private String token;
	private URL formatUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pendinglist);
		
		 try {
			token = DataStorage.properties.getProperty("token");
			formatUrl = new URL(DataStorage.properties.getProperty("url"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 if(token==null){

			 Toast.makeText(getApplicationContext(), "请检查网络连接是否正常", Toast.LENGTH_LONG).show(); 
			 Intent intent = new Intent(PendingsActivity.this, GaiaActivity.class);	
			 startActivity(intent);			 
			 
		 }else {
		 		 
		boolean isflow = false;
		try{
			isflow = getIntent().getExtras().getBoolean("flag");
			String name = getIntent().getExtras().getString("appname");
			names.add(name);
		}catch (Exception e) {
		}
		if(isflow == true){
			final List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			Collection<PendingType> pending = new ArrayList<PendingType>();
			for (PendingType pet : pendings) {
				pending.add(pet);
			}
			for (PendingType pet : pending) {
				if(names.toString().indexOf(pet.getSummary()) != -1){
					pendings.remove(pet);
				}
			}
			pending = null;
			for (PendingType pet : pendings) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("title", pet.getSummary());
				map.put("appname", pet.getName());
				map.put("info", pet.getDate());
				list.add(map);
			}
			SimpleAdapter adapter = new SimpleAdapter(PendingsActivity.this,
					list, R.layout.pendings, new String[] { "title","appname" ,"info"},
					new int[] { R.id.title ,R.id.appname, R.id.info});
			ListView listview = (ListView) findViewById(android.R.id.list);
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					PendingType aPending = ((ArrayList<PendingType>) pendings)
							.get(arg2);
					Bundle bundle = new Bundle();
					bundle.putString("appname", aPending.getName());
					bundle.putString("docid", aPending.getDocid());
					bundle.putString("formid", aPending.getFormid());
					bundle.putString("appid", aPending.getAppid());
					bundle.putString("summary", aPending.getSummary());
					Intent intent = new Intent(PendingsActivity.this,
							DocumentActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
		}
			else{
				refreshData();
			}
	}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		boolean isflow = false;
		try{
			isflow = intent.getExtras().getBoolean("flag");
			String name = intent.getExtras().getString("appname");
			names.add(name);
		}catch (Exception e) {
		}
		System.err.println(isflow);
		if(isflow == true){
			final List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			Collection<PendingType> pending = new ArrayList<PendingType>();
			for (PendingType pet : pendings) {
				pending.add(pet);
			}
			for (PendingType pet : pending) {
				if(names.toString().indexOf(pet.getSummary()) != -1){
					pendings.remove(pet);
				}
			}
			pending = null;
					for (PendingType pet : pendings) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("title", pet.getSummary());
						map.put("appname", pet.getName());
						map.put("info", pet.getDate());
						list.add(map);
					}
					SimpleAdapter adapter = new SimpleAdapter(PendingsActivity.this,
							list, R.layout.pendings, new String[] { "title","appname" ,"info"},
							new int[] { R.id.title ,R.id.appname, R.id.info});
					ListView listview = (ListView) findViewById(android.R.id.list);
					listview.setAdapter(adapter);
					listview.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							PendingType aPending = ((ArrayList<PendingType>) pendings)
									.get(arg2);
							Bundle bundle = new Bundle();
							bundle.putString("appname", aPending.getName());
							bundle.putString("docid", aPending.getDocid());
							bundle.putString("formid", aPending.getFormid());
							bundle.putString("appid", aPending.getAppid());
							bundle.putString("summary", aPending.getSummary());
							Intent intent = new Intent(PendingsActivity.this,
									DocumentActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					});
		}
		else{
			refreshData();
		}
	}
	
	private void refreshData(){
		try {
			String token = DataStorage.properties.getProperty("token");
			URL formatUrl = new URL(DataStorage.properties.getProperty("url"));
			ApiTask task = new ApiTask();
			JSONArray appids = new JSONArray();
			for (AppType app : Gaia.APPLIST) {
				appids.put(app.getAppid());
				maps.put(app.getAppid(), app.getName());
			}
			task.execute(formatUrl.toString(), token.toString(),
					appids.toString(),maps.toString());
			
		} catch (MalformedURLException e) {
		}
	}
	
	class ApiTask extends AsyncTask<String, Integer, Collection<PendingType>> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(PendingsActivity.this, "请稍等...",
					"正在初始化...");
		}

		@Override
		protected Collection<PendingType> doInBackground(String... params) {
			Collection<PendingType> pendinglists = new ArrayList<PendingType>();
			try {
				JSONArray jsa = new JSONArray(params[2]);
				for(int i = 0;i<jsa.length();i++){
					JSONArray appids = new JSONArray();
					appids.put(jsa.get(i));
					OAHttpApi OaApi = new OAHttpApi(params[0]);
					Collection<PendingType> pendinglist = OaApi.getPending(params[1],
							appids.toString());
					if(pendinglist != null){
						for(PendingType pet : pendinglist){
							pet.setName(maps.get(jsa.get(i).toString()));
						}
						pendinglists.addAll(pendinglist);
					}
					else{
						flag = false;
						wrong.add(maps.get(jsa.get(i).toString()));
					}
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return pendinglists;
		}

		@Override
		protected void onPostExecute(final Collection<PendingType> pendinglist) {
			if(flag == false){
				Toast.makeText(getApplicationContext(), wrong.toString()+"数据错误请稍候再试", Toast.LENGTH_LONG).show();
				flag =true;
				wrong.clear();
			}
			if(pendinglist!=null){
				if(pendinglist!=null && pendinglist.size()>0) {
					names.clear();
					pendings = pendinglist;
					final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
					for (PendingType pet : pendinglist) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("title", pet.getSummary());
						map.put("appname", pet.getName());
						map.put("info", pet.getDate());
						list.add(map);
					}
					SimpleAdapter adapter = new SimpleAdapter(PendingsActivity.this,
							list, R.layout.pendings, new String[] { "title","appname" ,"info"},
							new int[] { R.id.title ,R.id.appname, R.id.info});
					ListView listview = (ListView) findViewById(android.R.id.list);
					listview.setAdapter(adapter);
					dialog.dismiss();
					listview.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							PendingType aPending = ((ArrayList<PendingType>) pendinglist)
									.get(arg2);
							Bundle bundle = new Bundle();
							bundle.putString("docid", aPending.getDocid());
							bundle.putString("formid", aPending.getFormid());
							bundle.putString("appid", aPending.getAppid());
							bundle.putString("summary", aPending.getSummary());
							Intent intent = new Intent(PendingsActivity.this,
									DocumentActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					});
				} 
				else{
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "沒有待办事项!", Toast.LENGTH_LONG).show();
				}
			}
			else
			{
				dialog.dismiss();
				Toast.makeText(getApplicationContext(), "数据错误请稍候再试...", Toast.LENGTH_LONG).show();
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "刷新").setIcon(
				android.R.drawable.ic_menu_rotate);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			refreshData();
			break;
		}
		return false;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(PendingsActivity.this,MainActivity.class);
			startActivity(intent);
			PendingsActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}