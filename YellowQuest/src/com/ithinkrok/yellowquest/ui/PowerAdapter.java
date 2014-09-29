package com.ithinkrok.yellowquest.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
		//context.getGameData().addScorePoints(1000000);
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
		
		String displayName = context.getString(info.displayName);
		String lvl = "";
		int lvlNum = context.getGameData().getPowerUpgradeLevel(info.name);
		if(lvlNum > 0) lvl = Integer.toString(lvlNum + 1);
		displayName = String.format(displayName, lvl);
		
		power_name.setText(displayName);
		power_buy.setText(BoxMath.formatNumber(info.buyCost));
		power_upgrade.setText(BoxMath.formatNumber(info.upgradeCost(lvlNum)));

		power_name.setOnClickListener(this);
		power_buy.setOnClickListener(this);
		power_upgrade.setOnClickListener(this);

		power_name.setTag(position);
		power_buy.setTag(position);
		power_upgrade.setTag(position);

		power_buy.setEnabled(!using);
		power_upgrade.setEnabled(lvlNum < info.maxUpgrade);

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
	
	public void showUpgradeDialog(final PowerInfo info){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		String msg = context.getString(info.warnInfo);
		msg = String.format(msg, context.getGameData().getPowerUpgradeLevel(info.name) + 2);
		builder.setMessage(msg);
		builder.setPositiveButton(R.string.upgrade, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GameData data = context.getGameData();
				int lvl = data.getPowerUpgradeLevel(info.name);
				if(!data.subtractScorePoints(info.upgradeCost(lvl))){
					Toast.makeText(context, R.string.not_enough, Toast.LENGTH_SHORT).show();
					return;
				}
				data.setNextPower("");
				data.setPowerUpgradeLevel(info.name, ++lvl);
				notifyDataSetChanged();
			}
		});
		
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//do nothing
			}
		});
		
		builder.create().show();
	}

	@Override
	public void onClick(View v) {
		if(!v.isEnabled()) return;
		int pos = (Integer) v.getTag();
		PowerInfo info = PowerInfo.getData(pos);
		switch (v.getId()) {
		case R.id.power_name:
			pos = (Integer) v.getTag();
			if (expanded == pos)
				expanded = -1;
			else
				expanded = pos;
			break;
		case R.id.power_buy:
			if(info.buyCost > context.getGameData().getScorePoints()){
				Toast.makeText(context, R.string.not_enough, Toast.LENGTH_SHORT).show();
				return;
			}
			context.getGameData().setNextPower(info.name);
			break;
		case R.id.power_upgrade:
			int lvlNum = context.getGameData().getPowerUpgradeLevel(info.name);
			if(info.upgradeCost(lvlNum) > context.getGameData().getScorePoints()){
				Toast.makeText(context, R.string.not_enough, Toast.LENGTH_SHORT).show();
				return;
			}
			showUpgradeDialog(info);
		}

		notifyDataSetChanged();
	}

}
