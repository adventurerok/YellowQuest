package com.ithinkrok.yellowquest.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.ithinkrok.yellowquest.MainActivity;
import com.ithinkrok.yellowquest.R;

public class BuyAdapter extends BaseAdapter implements View.OnClickListener {

	private static ArrayList<String> items = new ArrayList<String>();
	private static HashMap<String, Integer> rewards = new HashMap<String, Integer>();
	private static String[] prices = new String[4];
	private static String[] titles = new String[4];
	MainActivity context;

	public BuyAdapter(final MainActivity context) {
		this.context = context;
		if (items.isEmpty()) {
			items.add("scorepoints50");
			items.add("scorepoints100");
			items.add("scorepoints200");
			items.add("scorepoints500");
		}
		if (rewards.isEmpty()) {
			rewards.put("scorepoints50", 50000);
			rewards.put("scorepoints100", 110000);
			rewards.put("scorepoints200", 230000);
			rewards.put("scorepoints500", 600000);
		}
		query();
	}

	public void query() {
		if (prices[0] != null)
			return;
		final Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("ITEM_ID_LIST", items);

		Thread skuFetch = new Thread() {

			@Override
			public void run() {
				Bundle details;
				try {
					details = context.buyService.getSkuDetails(3, context.getPackageName(), "inapp", querySkus);
				} catch (RemoteException e) {
					e.printStackTrace();
					toast(R.string.error_getting_purchase_details);
					return;
				}

				int response = details.getInt("RESPONSE_CODE");
				if (response == 0) {
					ArrayList<String> responseList = details.getStringArrayList("DETAILS_LIST");

					for (String thisResponse : responseList) {
						try {
							JSONObject object = new JSONObject(thisResponse);
							String sku = object.getString("productId");
							String title = object.getString("title");
							String price = object.getString("price");
							// Log.i("YellowQuest", "A price: " + price);
							for (int d = 0; d < items.size(); ++d) {
								if (sku.equals(items.get(d))) {
									prices[d] = price;
									titles[d] = title;
								}
							}
						} catch (JSONException e) {
							Log.w("YellowQuest", "InAppBilling", e);
							toast(R.string.error_getting_purchase_details);
							return;
						}
					}
					// Log.i("YellowQuest", "Recieved data");
					// Log.i("YellowQuest", "prices[0]: " + prices[0]);
					// Log.i("YellowQuest", "prices[1]: " + prices[1]);
					// Log.i("YellowQuest", "prices[2]: " + prices[2]);
					// Log.i("YellowQuest", "prices[3]: " + prices[3]);
					dataChanged();
				} else {
					// Log.i("YelloqQuest", "Response not 0");
				}
			}
		};

		skuFetch.start();
	}

	public void dataChanged() {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				notifyDataSetChanged();
				// Log.i("YellowQuest", "Updated UI");

			}
		};
		context.runOnUiThread(run);
	}

	public void invalidateAll() {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				context.invalidateUI();
				//Log.i("YellowQuest", "Invalidate All");

			}
		};
		context.runOnUiThread(run);
	}
	
	
	public void toast(final int resId){
		Runnable run = new Runnable() {

			@Override
			public void run() {
				try{
					Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
				} catch (Exception e){
					//Someone called toast from the wrong location
				}

			}
		};
		context.runOnUiThread(run);
	}

	public void toast(final String toastText) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				try{
					Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
				} catch (Exception e){
					//Someone called toast from the wrong location
				}

			}
		};
		context.runOnUiThread(run);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView != null)
			view = convertView;
		else {
			LayoutInflater inflater = context.getLayoutInflater();
			view = inflater.inflate(R.layout.product, parent, false);
		}

		TextView purchase_name = (TextView) view.findViewById(R.id.purchase_name);
		purchase_name.setText(items.get(position));
		Button purchase_buy = (Button) view.findViewById(R.id.purchase_buy);
		purchase_buy.setTag(position);
		if (prices[0] == null)
			return view;
		purchase_name.setText(titles[position]);
		purchase_buy.setText(prices[position]);
		purchase_buy.setOnClickListener(this);
		return view;
	}

	private void buy(int pos) {
		if (context.buyService == null)
			return;
		try {
			Bundle buy = context.buyService.getBuyIntent(3, context.getPackageName(), items.get(pos), "inapp",
					"#banter");
			if (buy.getInt("RESPONSE_CODE") == 0) {
				PendingIntent pendingIntent = buy.getParcelable("BUY_INTENT");
				context.startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
			}
		} catch (RemoteException e) {
			//Log.w("YellowQuest", e);
		} catch (SendIntentException e) {
			//Log.w("YellowQuest", e);
		}
	}

	public void consume(final String productId, final String purchaseToken) {
		if (context.buyService == null)
			return;
		Thread consumer = new Thread() {

			@Override
			public void run() {
				try {
					int response = context.buyService.consumePurchase(3, context.getPackageName(), purchaseToken);
					if (response == 0) {
						final int reward = rewards.get(productId);
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								context.getGameData().setMadePurchase();
								context.getGameData().addScorePoints(reward);
								context.saveData();
								String text = context.getString(R.string.achievement_reward);
								text = String.format(text, reward);
								toast(text);
							}
						});

						invalidateAll();
					} else {
						//toast("Error code: " + response + "PID: " + productId);
						//Log.w("YellowQuest", "Failed to consume purchase");
					}
				} catch (Exception e) {
					//toast("Dang: " + e.getMessage());
					//Log.w("YellowQuest", e);
				}
			}
		};
		consumer.start();
	}

	@Override
	public void onClick(View v) {
		buy((Integer) v.getTag());
	}

}
