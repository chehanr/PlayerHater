/*******************************************************************************
 * Copyright 2013 Chris Rhoden, Rebecca Nesson, Public Radio Exchange
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.prx.android.playerhater.plugins;

import org.prx.android.playerhater.Song;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class LockScreenControlsPlugin extends AudioFocusPlugin {

	private RemoteControlClient mRemoteControlClient;
	private Bitmap mAlbumArt;
	private String mArtist;
	private String mTitle;

	private int mTransportControlFlags = RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE
			| RemoteControlClient.FLAG_KEY_MEDIA_STOP
			| RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
			| RemoteControlClient.FLAG_KEY_MEDIA_NEXT;

	@Override
	public void onAudioStarted() {
		super.onAudioStarted();
		getRemoteControlClient().setPlaybackState(
				RemoteControlClient.PLAYSTATE_PLAYING);
		getAudioManager().registerRemoteControlClient(getRemoteControlClient());
	}

	@Override
	public void onAudioPaused() {
		getRemoteControlClient().setPlaybackState(
				RemoteControlClient.PLAYSTATE_PAUSED);
	}

	@Override
	public void onDurationChanged(int duration) {
		getRemoteControlClient()
				.editMetadata(false)
				.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, duration)
				.apply();
	}

	@Override
	public void onSongChanged(Song song) {

		if (song.getAlbumArt() != null) {
			onAlbumArtChangedToUri(song.getAlbumArt());
		}

		onTitleChanged(song.getTitle());
		onArtistChanged(song.getArtist());
	}

	@Override
	public void onAudioStopped() {
		super.onAudioStopped();
		getAudioManager().unregisterRemoteControlClient(
				getRemoteControlClient());
	}

	@Override
	public void onTitleChanged(String title) {
		mTitle = title;
	}

	@Override
	public void onArtistChanged(String artist) {
		mArtist = artist;
	}

	@Override
	public void onAlbumArtChanged(int resourceId) {
		mAlbumArt = BitmapFactory.decodeResource(getContext().getResources(),
				resourceId);
		getRemoteControlClient().editMetadata(false).putBitmap(
				RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK,
				mAlbumArt);
	}

	@Override
	public void onAlbumArtChangedToUri(Uri url) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChangesComplete() {
		getRemoteControlClient()
				.editMetadata(false)
				.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, mTitle)
				.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, mArtist)
				.putBitmap(
						RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK,
						mAlbumArt).apply();
	}

	private RemoteControlClient getRemoteControlClient() {
		if (mRemoteControlClient == null) {
			Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
			mediaButtonIntent.setComponent(getEventReceiver());
			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					getContext(), 0, mediaButtonIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			mRemoteControlClient = new RemoteControlClient(pendingIntent);
			mRemoteControlClient
					.setTransportControlFlags(mTransportControlFlags);
		}
		return mRemoteControlClient;
	}

}