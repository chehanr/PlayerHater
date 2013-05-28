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
package org.prx.playerhater.util;

import org.prx.playerhater.service.IPlayerHaterBinder;

import android.media.AudioManager;
import android.os.RemoteException;

public class OnAudioFocusChangeListener implements
		AudioManager.OnAudioFocusChangeListener {

	// 5 seconds
	public static final int REWIND_ON_RESUME_DURATION = 5000;

	// 5 minutes
	public static final int SKIP_RESUME_AFTER_DURATION = 300000;

	private IPlayerHaterBinder mService;
	private long pausedAt;
	private boolean isBeingDucked;

	private boolean isBeingPaused;

	public OnAudioFocusChangeListener(IPlayerHaterBinder binder) {
		mService = binder;
		isBeingDucked = false;
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		try {
			switch (focusChange) {
			case AudioManager.AUDIOFOCUS_GAIN:
				// Good, glad to hear it.
				if (isBeingPaused && !mService.isPlaying()) {
					isBeingPaused = false;
					if (pausedAt + (SKIP_RESUME_AFTER_DURATION) > System
							.currentTimeMillis()) {
						try {
							mService.resume();
						} catch (Exception e) {
							// Probably illegal state, don't care.
						}
					}
				}

				if (isBeingDucked) {
					isBeingDucked = false;
					mService.unduck();
				}
				break;
			case AudioManager.AUDIOFOCUS_LOSS:
				// Oh, no! Ok, let's handle that.
				if (mService.isPlaying()) {
					mService.pause();
				}
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
				// Let's pause, expecting it to come back.
				if (mService.isPlaying()) {
					pausedAt = System.currentTimeMillis();
					isBeingPaused = true;
					mService.pause();
					mService.seekTo(Math.max(0, mService.getCurrentPosition()
							- REWIND_ON_RESUME_DURATION));
				}
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
				if (mService.isPlaying() && !isBeingDucked) {
					isBeingDucked = true;
					mService.unduck();
				}
				break;
			default:
				// Dunno.
			}
		} catch (RemoteException e) {}
	}

}