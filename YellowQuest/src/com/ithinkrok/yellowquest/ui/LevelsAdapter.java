package com.ithinkrok.yellowquest.ui;

import com.ithinkrok.yellowquest.*;

import android.graphics.Color;
import android.graphics.PorterDuff;
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
	
	public int selected = -1;
	
	

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
		return totalBoxes[level] * 30 + level * 200;
	}
	
	//price for level = num boxes to level * 30 + 200 * lnum
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
		select.setText(BoxMath.formatNumberWithoutSuffix(price(position + 1)));
		select.setTag(position + 1);
		select.setOnClickListener(this);
		
		if (selected == (position + 1)){
			view.setBackgroundColor(0xFF666666);
			select.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
		}
		else {
			view.setBackgroundColor(0xFF000000);
			select.getBackground().setColorFilter(null);
		}
		
		
		return view;
	}

	@Override
	public void onClick(View v) {
		int lvl = (Integer) v.getTag();
		if(lvl == selected){
			selected = -1;
		} else {
			if(price(lvl) > context.getGameData().getScorePoints()){
				ToastSystem.showTextToast(R.string.not_enough);
			}
			selected = lvl;
		}
		
		notifyDataSetChanged();

	}

}
