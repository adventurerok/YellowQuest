package com.ithinkrok.yellowquest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.net.Uri;

public class AppRater {

	// Change for your needs!
	private final static String APP_PACKAGE_NAME = "com.ithinkrok.yellowquest";

	public final static int DAYS_UNTIL_PROMPT = 2;
	public final static int LAUNCH_UNTIL_PROMPT = 5;

	public static void app_launched(MainActivity mContext) {
		SharedPreferences prefs = mContext.rateSettings;
		if (prefs.getBoolean("dontshowagain", false)) {
			return;
		}

		SharedPreferences.Editor editor = prefs.edit();

		// Add to launch Counter
		long launch_count = prefs.getLong("launch_count", 0) + 1;
		editor.putLong("launch_count", launch_count);

		// Get Date of first launch
		Long date_firstLaunch = prefs.getLong("date_first_launch", 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("date_first_launch", date_firstLaunch);
		}

		// Wait at least X days to launch
		if (launch_count >= LAUNCH_UNTIL_PROMPT) {
			if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
				showRateDialog(mContext, editor);
			}
		}

		editor.apply();

	}

	public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
		Dialog dialog = new Dialog(mContext);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(R.string.rate_message).setTitle(R.string.rate_title).setIcon(mContext.getApplicationInfo().icon)
				.setCancelable(false).setPositiveButton(R.string.rate_positive, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						editor.putBoolean("dontshowagain", true);
						editor.apply();
						mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
								+ APP_PACKAGE_NAME)));
						dialog.dismiss();
					}
				}).setNeutralButton(R.string.rate_neutral, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				}).setNegativeButton(R.string.rate_negative, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (editor != null) {
							editor.putBoolean("dontshowagain", true);
							editor.apply();
						}
						dialog.dismiss();

					}
				});
		dialog = builder.create();

		dialog.show();
	}
}