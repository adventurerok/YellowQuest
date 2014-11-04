package com.ithinkrok.yellowquest.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.*;
import android.widget.*;

import com.ithinkrok.yellowquest.*;
import com.ithinkrok.yellowquest.challenge.Challenge;

public class PowerAdapter extends BaseAdapter implements View.OnClickListener {

	MainActivity context;


	private String expanded = null;
	
	
	private ArrayList<String> showing = new ArrayList<String>();
	
	

	// private ListView view;

	public PowerAdapter(MainActivity context) {
		super();
		this.context = context;
		//context.getGameData().addScorePoints(1000000);
	}

	@Override
	public int getCount() {
		return showing.size();
	}

	@Override
	public Object getItem(int position) {
		String at = showing.get(position);
		if(at.startsWith("#") || at.startsWith("!")){
			return null;
		}
		return PowerInfo.getData(showing.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void refreshItems(){
		showing.clear();
		showing.add("!");
		for(int d = 0; d < PowerInfo.getPowerCount(); ++d){
			if(!context.getGameData().hasPowerUnlock(d)) continue;
			else showing.add(PowerInfo.getData(d).name);
		}
		String tip = Tip.getTip(context);
		if(tip != null){
			showing.add(tip);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String name = showing.get(position);
		if(name.startsWith("!")) return getChallengeView(convertView, parent);
		if(name.startsWith("#")) return getTipView(name, convertView, parent);
		else return getPowerView(name, convertView, parent);
		
	}
	
	public View getChallengeView(View convertView, ViewGroup parent){
		Challenge chal = context.getGameData().statTracker.currentChallenge;
		
		View row = null;
		if(convertView != null && convertView.getId() == R.id.challenge){
			row = convertView;
		} 
		if(row == null){
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.challenge, parent, false);
		}
		
		ImageView icon = (ImageView) row.findViewById(R.id.challenge_icon);
		TextView title = (TextView) row.findViewById(R.id.challenge_title);
		TextView progress = (TextView) row.findViewById(R.id.challenge_text);
		TextView skip = (TextView) row.findViewById(R.id.challenge_skip);
		
		icon.setImageDrawable(context.loadDrawable(chal.getIconResource()));
		
		String titleText = chal.getTitleText(context);
		titleText = StringFormatter.format(context.getString(R.string.challenge_info), titleText);
		if(titleText.length() > 50){
			title.setTextAppearance(context, R.style.RelSmaller);
		}
		title.setText(titleText);
		
		skip.setText(StringFormatter.format(context.getString(R.string.skip_cost), BoxMath.formatNumberWithoutSuffix(chal.skipCost)));
		skip.setTag("!");
		skip.setOnClickListener(this);
		
		progress.setText(chal.getProgressText(context));
		
		return row;
	}
	
	public View getTipView(String name, View convertView, ViewGroup parent){
		View row = null;
		if(convertView != null && convertView.getId() == R.id.tip){
			row = convertView;
		} 
		if(row == null){
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.tip, parent, false);
		}
		TextView tip_message = (TextView) row.findViewById(R.id.tip_message);
		int messageId = Tip.getTipMessage(name);
		tip_message.setText(messageId);
		
		Button tip_hide = (Button) row.findViewById(R.id.tip_hide);
		tip_hide.setOnClickListener(this);
		tip_hide.setTag(name);
		return row;
	}
	
	public View getPowerView(String name, View convertView, ViewGroup parent){
		PowerInfo info = PowerInfo.getData(name);
		if(info == null) throw new RuntimeException("Power invalid");
		boolean using = info.name.equals(context.getGameData().getNextPower());
		View row = null;
		if (convertView != null) {
			if(convertView.getId() == R.id.power_expanded || convertView.getId() == R.id.power){
				boolean wasExpanded = convertView.getId() == R.id.power_expanded;
				if (wasExpanded == (info.name.equals(expanded)))
					row = convertView;
			}
		}
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			if (!info.name.equals(expanded))
				row = inflater.inflate(R.layout.power, parent, false);
			else
				row = inflater.inflate(R.layout.power_expanded, parent, false);
		}
		View power_color = row.findViewById(R.id.power_color);
		TextView power_name = (TextView) row.findViewById(R.id.power_name);
		Button power_buy = (Button) row.findViewById(R.id.power_buy);
		Button power_upgrade = (Button) row.findViewById(R.id.power_upgrade);

		if (using){
			row.setBackgroundColor(0xFF666666);
			power_buy.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
			//power_buy.setBackgroundColor(0xFF666666);
		}
		else {
			row.setBackgroundColor(0xFF000000);
			//power_buy.setBackgroundResource(R.drawable.);
			power_buy.getBackground().setColorFilter(null);
		}
		power_color.setBackgroundColor(info.color);
		
		String displayName = context.getString(info.displayName);
		String lvl = "";
		int lvlNum = context.getGameData().getPowerUpgradeLevel(info.name);
		if(lvlNum > 0) lvl = Integer.toString(lvlNum + 1);
		displayName = StringFormatter.format(displayName, lvl);
		
		power_name.setText(displayName);
		power_buy.setText(BoxMath.formatNumber(info.buyCost));
		power_upgrade.setText(BoxMath.formatNumber(info.upgradeCost(lvlNum)));

		power_name.setOnClickListener(this);
		power_buy.setOnClickListener(this);
		power_upgrade.setOnClickListener(this);

		power_name.setTag(info.name);
		power_buy.setTag(info.name);
		power_upgrade.setTag(info.name);

		//power_buy.setEnabled(!using);
		power_upgrade.setEnabled(lvlNum < info.maxUpgrade);

		if (info.name.equals(expanded)) {
			TextView power_info = (TextView) row.findViewById(R.id.power_info);
			StringBuilder infoText = new StringBuilder();
			infoText.append(context.getString(info.displayInfo));
			infoText.append("\n");
			infoText.append(context.getString(info.displayUpgradeInfo));
			infoText.append("\n\n");
			infoText.append(context.getString(info.displayUnlock));
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
		msg = StringFormatter.format(msg, context.getGameData().getPowerUpgradeLevel(info.name) + 2);
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
				//data.setNextPower("");
				data.setPowerUpgradeLevel(info.name, ++lvl);
				context.saveData();
				notifyDataSetChanged();
				context.updateScorePointCounter();
				
				context.getGame().addAchievement(R.string.achievement_time_for_an_upgrade);
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
	
	public void onTipClick(View v, String name){
		switch(v.getId()){
		case R.id.tip_hide:
			showing.remove(name);
			notifyDataSetChanged();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		if(!v.isEnabled()) return;
		String name = (String) v.getTag();
		if(name.startsWith("#")){
			onTipClick(v, name);
			return;
		} else if(name.startsWith("!")){
			onChallengeClick();
		}
		PowerInfo info = PowerInfo.getData(name);
		switch (v.getId()) {
		case R.id.power_name:
			if (name.equals(expanded))
				expanded = null;
			else
				expanded = name;
			break;
		case R.id.power_buy:
			int totalSp = context.getGameData().getScorePoints();
			String nextPower = context.getGameData().getNextPower();
			if(nextPower != null && !nextPower.isEmpty()){
				totalSp += PowerInfo.buyCost(nextPower);
			}
			if(info.name.equals(nextPower)){
				context.getGameData().setNextPower("");
				context.getGameData().setScorePoints(totalSp);
				context.saveAll();
			} else {
				if(info.buyCost > totalSp){
					Toast.makeText(context, R.string.not_enough, Toast.LENGTH_SHORT).show();
					return;
				}
				context.getGameData().setNextPower(info.name);
				context.getGameData().setScorePoints(totalSp - info.buyCost);
				context.saveAll();
			}
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
		context.updateScorePointCounter();
	}

	private void onChallengeClick() {
		final int cost = context.getGameData().statTracker.currentChallenge.skipCost;
		if(context.getGameData().getScorePoints() < cost){
			ToastSystem.showTextToast(R.string.not_enough);
			return;
		}
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		String msg = context.getString(R.string.skip_warn);
		msg = StringFormatter.format(msg, cost);
		builder.setMessage(msg);
		builder.setPositiveButton(R.string.skip, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GameData data = context.getGameData();
				data.subtractScorePoints(cost);
				data.statTracker.skipChallenge();
				ToastSystem.showTextToast(R.string.challenge_skipped);
				context.loadPlayView();
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

}
