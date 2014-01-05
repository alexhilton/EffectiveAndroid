package com.hilton.effectiveandroid;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class GridViewActivity extends Activity {
	private GridView mGridView;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mGridView = new GridView(this);
		mGridView.setAdapter(new MyAdapter(this));
		mGridView.setNumColumns(GridView.AUTO_FIT);
		mGridView.setColumnWidth(100);
		mGridView.setVerticalSpacing(15);
		mGridView.setHorizontalSpacing(2);
		mGridView.setStretchMode(GridView.STRETCH_SPACING);
		mGridView.setFocusable(true);
		mGridView.setFocusableInTouchMode(true);
		mGridView.setSelector(android.R.drawable.gallery_thumb);
		
		setContentView(mGridView);
	}
	
	private class MyAdapter extends BaseAdapter {
		private Context mContext;
		public MyAdapter(Context context) {
			super();
			mContext = context;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater factory = LayoutInflater.from(mContext);
				convertView = factory.inflate(R.layout.grid, null);
			}
			TextView text = (TextView) convertView.findViewById(R.id.text);
			final String msg = "View # " + String.valueOf(position);
			text.setText(msg);
			return convertView;
		}
		
		public long getItemId(int position) {
			return position;
		}
		
		public int getCount() {
			return 9;
		}
		
		public Object getItem() {
			return null;
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
