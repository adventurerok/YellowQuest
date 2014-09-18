package com.ithinkrok.yellowquest.ui;

import java.util.ArrayList;

import com.ithinkrok.yellowquest.MainActivity;
import com.ithinkrok.yellowquest.R;
import com.ithinkrok.yellowquest.entity.power.Power;
import com.ithinkrok.yellowquest.entity.power.PowerBounce;
import com.ithinkrok.yellowquest.entity.trait.TraitBounce;

import android.view.*;
import android.widget.*;

public class PowerAdapter extends BaseAdapter {
	
	public static class PowerInfo {
		
		Class<? extends Power> clazz;
		String name;
		int color;
		int buyCost;
		int upgradeCost;
		int displayName;
		int displayInfo;
		int displayUpgradeInfo;
		
		public PowerInfo(Class<? extends Power> clazz, String name, int color, int buyCost, int upgradeCost,
				int displayName, int displayInfo, int displayUpgradeInfo) {
			super();
			this.clazz = clazz;
			this.name = name;
			this.color = color;
			this.buyCost = buyCost;
			this.upgradeCost = upgradeCost;
			this.displayName = displayName;
			this.displayInfo = displayInfo;
			this.displayUpgradeInfo = displayUpgradeInfo;
		}
		
		
		
	}
	
	MainActivity context;
	
	ArrayList<PowerInfo> data = new ArrayList<PowerAdapter.PowerInfo>();
	

	public PowerAdapter(MainActivity context) {
		super();
		this.context = context;
		data.add(new PowerInfo(PowerBounce.class, "bounce", TraitBounce.PAINT_MAGENTA.getColor(), 5000, 50000, R.string.power_bounce, R.string.power_bounce_desc, R.string.power_bounce_upgrade));
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PowerInfo info = data.get(position);
		LayoutInflater inflater = context.getLayoutInflater();
		View row;
		if(convertView == null){
			row = inflater.inflate(R.layout.power, parent, false);
		} else row = convertView;
		View power_color = row.findViewById(R.id.power_color);
		TextView power_name = (TextView) row.findViewById(R.id.power_name);
		
		power_color.setBackgroundColor(info.color);
		power_name.setText(info.displayName);
		
		return row;
	}

}
