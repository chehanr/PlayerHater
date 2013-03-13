package org.prx.android.playerhater.plugins;

import org.prx.android.playerhater.R;
import org.prx.android.playerhater.Song;
import org.prx.android.playerhater.service.IPlayerHaterService;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class NotificationPlugin extends AbstractPlugin {

	protected static final int NOTIFICATION_NU = 9747245;
	private static final String TAG = "NotificationPlugin";
	protected IPlayerHaterService mService;
	protected NotificationManager mNotificationManager;
	protected PendingIntent mContentIntent;
	protected String mNotificationTitle = "PlayerHater";
	protected String mNotificationText = "Version 0.1.0";
	private boolean mIsVisible = false;
	private Notification mNotification;

	public NotificationPlugin(IPlayerHaterService service) {
		mService = service;
		Context c = mService.getBaseContext().getApplicationContext();
		PackageManager packageManager = c.getPackageManager();
		mNotificationManager = (NotificationManager) c
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent resumeActivityIntent = packageManager
				.getLaunchIntentForPackage(c.getPackageName());
		resumeActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resumeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mContentIntent = PendingIntent.getActivity(c, NOTIFICATION_NU,
				resumeActivityIntent, 0);
	}

	@Override
	public void onSongChanged(Song song) {
		onTitleChanged(song.getTitle());
		onArtistChanged(song.getArtist());
		onAlbumArtChangedToUri(song.getAlbumArt());
	}

	@Override
	public void onPlaybackStarted() {
		Log.d(TAG, "Starting up our notification");
		mService.startForeground(NOTIFICATION_NU, getNotification());
		mIsVisible = true;
	}

	@SuppressWarnings("deprecation")
	protected Notification getNotification() {
		if (mNotification == null)
			mNotification = new Notification(R.drawable.zzz_ph_ic_notification,
					"Playing: " + mNotificationTitle, 0);

		mNotification.setLatestEventInfo(mService.getBaseContext(),
				mNotificationTitle, mNotificationText, mContentIntent);
		return mNotification;
	}

	@Override
	public void onPlaybackStopped() {
		mIsVisible = false;
		mService.stopForeground(true);
	}

	@Override
	public void onTitleChanged(String notificationTitle) {
		mNotificationTitle = notificationTitle;
		updateNotification();
	}

	public void onIntentActivityChanged(PendingIntent contentIntent) {
		mContentIntent = contentIntent;
		if (mNotification != null) {
			mNotification.contentIntent = mContentIntent;
		}
		updateNotification();
	}

	@Override
	public void onArtistChanged(String notificationText) {
		mNotificationText = notificationText;
		updateNotification();
	}

	protected void updateNotification() {
		if (mIsVisible) {
			mNotificationManager.notify(NOTIFICATION_NU, getNotification());
		}
	}

}
