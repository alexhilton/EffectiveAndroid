package com.hilton.effectiveandroid.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.hilton.effectiveandroid.R;

public class PopupWindowDemo extends Activity {
    private PopupWindow window;
    private Button submit, cancel;
    private ListView list;
    private Button show, off;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.popup_demo_activity);
	show = (Button) findViewById(R.id.myButton1);
	show.setOnClickListener(bPop);
	off = (Button) findViewById(R.id.myButton2);
	off.setOnClickListener(boff);
    }

    private void popAwindow(View parent) {
	if (window == null) {
	    LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View v = lay.inflate(R.layout.popupwindow, null);

	    // 初始化按钮
	    submit = (Button) v.findViewById(R.id.submit);
	    submit.setOnClickListener(submitListener);
	    cancel = (Button) v.findViewById(R.id.cancel);
	    cancel.setOnClickListener(cancelListener);

	    // 初始化listview，加载数据。
	    list = (ListView) v.findViewById(R.id.lv);
	    list.setAdapter(new ArrayAdapter<String>(this,
	                android.R.layout.simple_list_item_1,
	                new String[] {"English", "beingin", "fuck"}));
	    list.setItemsCanFocus(false);
	    list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

	    window = new PopupWindow(v, 500, 260);
	}

	// 设置整个popupwindow的样式。
	// Remember to add background to PopupWindow otherwise, areas outside of popupwindow won't be clickable.
	window.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corners_pop));
	// 使窗口里面的空间显示其相应的效果，比较点击button时背景颜色改变。
	// 如果为false点击相关的空间表面上没有反应，但事件是可以监听到的。
	// listview的话就没有了作用。
	window.setFocusable(true);// 如果不设置setFocusable为true，popupwindow里面是获取不到焦点的，那么如果popupwindow里面有输入框等的话就无法输入。
	window.update();
	window.showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0);
    }

    OnClickListener submitListener = new OnClickListener() {
	@Override
	public void onClick(View v) {
	    // 这儿可以写提交数据的代码。
	    closeWindow();
	}
    };

    OnClickListener cancelListener = new OnClickListener() {
	@Override
	public void onClick(View v) {
	    closeWindow();
	}
    };

    private void closeWindow() {
	if (window != null) {
	    window.dismiss();
	}
    }

    OnClickListener bPop = new OnClickListener() {
	@Override
	public void onClick(View v) {
	    popAwindow(v);
	}
    };

    OnClickListener boff = new OnClickListener() {
	@Override
	public void onClick(View v) {
	    if(window!=null){
		window.dismiss();
	    }
	}
    };

}
