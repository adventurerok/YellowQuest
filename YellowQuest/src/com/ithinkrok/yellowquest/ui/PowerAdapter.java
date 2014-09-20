package com.ithinkrok.yellowquest.ui;

import android.view.*;
import android.widget.*;

import com.ithinkrok.yellowquest.*;

public class PowerAdapter extends BaseAdapter implements View.OnClickListener {

	MainActivity context;


	private int expanded = -1;

	// private ListView view;

	public PowerAdapter(MainActivity context) {
		super();
		this.context = context;
	}

	@Override
	public int getCount() {
		return PowerInfo.getPowerCount();
	}

	@Override
	public Object getItem(int position) {
		return PowerInfo.getData(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PowerInfo info = PowerInfo.getData(position);
		boolean using = info.name.equals(context.getGameData().getNextPower());
		LayoutInflater inflater = context.getLayoutInflater();
		View row = null;
		if (convertView != null) {
			boolean wasExpanded = convertView.getId() == R.id.power_expanded;
			if (wasExpanded == (position == expanded))
				row = convertView;
		}
		if (row == null) {
			if (position != expanded)
				row = inflater.inflate(R.layout.power, parent, false);
			else
				row = inflater.inflate(R.layout.power_expanded, parent, false);
		}
		View power_color = row.findViewById(R.id.power_color);
		TextView power_name = (TextView) row.findViewById(R.id.power_name);
		Button power_buy = (Button) row.findViewById(R.id.power_buy);
		Button power_upgrade = (Button) row.findViewById(R.id.power_upgrade);

		if (using)
			row.setBackgroundColor(0xFF666666);
		else row.setBackgroundColor(0xFF000000);
		power_color.setBackgroundColor(info.color);
		power_name.setText(info.displayName);
		power_buy.setText(BoxMath.formatNumber(info.buyCost));
		power_upgrade.setText(BoxMath.formatNumber(info.upgradeCost));

		power_name.setOnClickListener(this);
		power_buy.setOnClickListener(this);
		power_upgrade.setOnClickListener(this);

		power_name.setTag(position);
		power_buy.setTag(position);

		power_buy.setEnabled(!using);

		if (position == expanded) {
			TextView power_info = (TextView) row.findViewById(R.id.power_info);
			StringBuilder infoText = new StringBuilder();
			infoText.append(context.getString(info.displayInfo));
			infoText.append("\n");
			infoText.append(context.getString(info.displayUpgradeInfo));
			power_info.setText(infoText.toString());
		}

		return row;
	}

	public void setView(ListView view) {
		// this.view = view;
	}

	@Override
	public void onClick(View v) {
		int pos;
		switch (v.getId()) {
		case R.id.power_name:
			pos = (Integer) v.getTag();
			if (expanded == pos)
				expanded = -1;
			else
				expanded = pos;
			break;
		case R.id.power_buy:
			if(!v.isEnabled()) return;
			pos = (Integer) v.getTag();
			PowerInfo info = PowerInfo.getData(pos);
			if(info.buyCost > context.getGameData().getScorePoints()){
				Toast.makeText(context, R.string.not_enough, Toast.LENGTH_SHORT).show();
				return;
			}
			context.getGameData().setNextPower(PowerInfo.getData((Integer) v.getTag()).name);
			break;
		}

		notifyDataSetChanged();
	}

}
