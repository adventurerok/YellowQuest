package com.ithinkrok.yellowquest.ui;

import com.ithinkrok.yellowquest.*;

import android.view.*;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LevelsAdapter extends BaseAdapter implements OnClickListener {
	
	private static int[] totalBoxes = new int[100];
	
	static {
		totalBoxes[0] = 0;
		//totalBoxes[1] = 8;
		for(int d = 1; d < 100; ++d){
			totalBoxes[d] = totalBoxes[d-1] + 8 + 3 * (d - 1);
		}
	}
	
	private MainActivity context;
	
	

	public LevelsAdapter(MainActivity context) {
		super();
		this.context = context;
	}

	@Override
	public int getCount() {
		return context.getGameData().getHighestLevel() - 2;
	}

	@Override
	public Object getItem(int position) {
		return position + 1;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public int price(int level){
		return totalBoxes[level] * 15 + level * 200;
	}
	
	//price for level = num boxes to level * 15 + 200 * lnum
	//num boxes in level b=8+3l
	//

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if(convertView != null){
			view = convertView;
		} 
		if(view == null){
			LayoutInflater inflater = context.getLayoutInflater();
			view = inflater.inflate(R.layout.level, parent, false);
		}
		
		TextView name = (TextView) view.findViewById(R.id.level_name);
		String text = context.getString(R.string.level_x);
		text = String.format(text, position + 2);
		name.setText(text);
		
		TextView select = (TextView) view.findViewById(R.id.level_select);
		select.setText(BoxMath.formatNumberWithoutSuffix(price(position + 2)));
		select.setTag(position + 2);
		select.setOnClickListener(this);
		
		
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
