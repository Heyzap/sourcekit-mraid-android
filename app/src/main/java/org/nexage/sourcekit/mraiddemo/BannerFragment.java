package org.nexage.sourcekit.mraiddemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BannerFragment extends ListFragment {

	private final static String TAG = "BannerFragment";
	private final static String PREFIX = "banner";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				inflater.getContext(),
				android.R.layout.simple_list_item_1,
				getTestFiles());
		setListAdapter(adapter);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		String name = (String)getListAdapter().getItem(position);
		String filename = PREFIX + "." + name + ".html";
		Log.d(TAG, name);
		String content = null;
		try {
			InputStream is = getResources().getAssets().open(filename);
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(is));
		    String line;
		    StringBuffer sb = new StringBuffer();
		    while ((line = bufferReader.readLine()) != null) {
		        sb.append(line).append(System.getProperty("line.separator"));
		    }
		    bufferReader.close();
			content = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (content != null) {
			Intent intent = new Intent(getActivity(), BannerActivity.class);
			intent.putExtra("name", name);
			intent.putExtra("content", content);
			startActivity(intent);
		}
	}

	private String[] getTestFiles() {
		String[] files = null;
		List<String> testFiles = new ArrayList<String>();
		try {
			files = getResources().getAssets().list("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String file = files[i];
				if (file.startsWith(PREFIX)) {
					String displayName = file.replace(PREFIX + ".", "");
					displayName = displayName.replace(".html",  "");
					Log.d(TAG, files[i]);
					testFiles.add(displayName);
				}
			}
		}
		return testFiles.toArray(new String[testFiles.size()]);
	}

}
