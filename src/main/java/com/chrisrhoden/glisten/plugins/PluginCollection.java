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
package com.chrisrhoden.glisten.plugins;

import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.util.SparseArray;

import com.chrisrhoden.glisten.PlayerHater;
import com.chrisrhoden.glisten.PlayerHaterPlugin;
import com.chrisrhoden.glisten.Song;
import com.chrisrhoden.glisten.util.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PluginCollection implements PlayerHaterPlugin {

    private final Set<PlayerHaterPlugin> mPlugins;
    private final SparseArray<PlayerHaterPlugin> mPluginTags;
    private final ReadWriteLock mLock = new ReentrantReadWriteLock();

    public PluginCollection() {
        mPlugins = new HashSet<PlayerHaterPlugin>();
        mPluginTags = new SparseArray<PlayerHaterPlugin>();
    }

    public PluginCollection(PlayerHaterPlugin... plugins) {
        this();
        mLock.writeLock().lock();
        try {
            for (PlayerHaterPlugin plugin : plugins) {
                add(plugin);
            }
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public void add(final PlayerHaterPlugin plugin) {
        if (mLock.writeLock().tryLock()) {
            try {
                add(plugin, 0);
            } finally {
                mLock.writeLock().unlock();
            }
        } else {
            new Thread() {

                @Override
                public void run() {
                    add(plugin, 0);
                }

            }.start();
        }
    }

    public void add(PlayerHaterPlugin plugin, int tag) {
        mLock.writeLock().lock();
        try {
            if (tag != 0) {
                mPluginTags.put(tag, plugin);
            }
            mPlugins.add(plugin);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public void remove(PlayerHaterPlugin plugin) {
        mLock.writeLock().lock();
        try {
            mPlugins.remove(plugin);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public PlayerHaterPlugin get(int tag) {
        mLock.readLock().lock();
        try {
            return mPluginTags.get(tag);
        } finally {
            mLock.readLock().unlock();
        }
    }

    public void remove(int tag) {
        mLock.writeLock().lock();
        try {
            if (tag != 0 && mPluginTags.get(tag) != null) {
                mPlugins.remove(mPluginTags.get(tag));
                mPluginTags.delete(tag);
            }
        } finally {
            mLock.writeLock().unlock();
        }
    }

    @Override
    public void onAudioStopped() {
        Log.d("onAudioStopped");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin listener : mPlugins)
                listener.onAudioStopped();
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onTitleChanged(String title) {
        Log.d("onTitleChanged");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin listener : mPlugins)
                listener.onTitleChanged(title);
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onArtistChanged(String artist) {
        Log.d("onArtistChanged");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin listener : mPlugins)
                listener.onArtistChanged(artist);
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onAlbumArtChanged(Uri url) {
        Log.d("onAlbumArtChanged");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin listener : mPlugins)
                listener.onAlbumArtChanged(url);
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onSongChanged(Song song) {
        Log.d("onSongChanged");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins) {
                plugin.onSongChanged(song);
            }
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onDurationChanged(int duration) {
        Log.d("onDurationChanged");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onDurationChanged(duration);
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onAudioLoading() {
        Log.d("onAudioLoading");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onAudioLoading();
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onAudioPaused() {
        Log.d("onAudioPaused");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onAudioPaused();
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onAudioResumed() {
        Log.d("onAudioResumed");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onAudioResumed();
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onAudioStarted() {
        Log.d("onAudioStarted");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onAudioStarted();
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onNextSongAvailable(Song nextSong) {
        Log.d("onNextSongAvailable");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onNextSongAvailable(nextSong);
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onNextSongUnavailable() {
        Log.d("onNextSongUnavailable");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onNextSongUnavailable();
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onPlayerHaterLoaded(Context context, PlayerHater playerHater) {
        Log.d("onPlayerHaterLoaded");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onPlayerHaterLoaded(context, playerHater);
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onPendingIntentChanged(PendingIntent pending) {
        Log.d("onPendingIntentChanged");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onPendingIntentChanged(pending);
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onSongFinished(Song song, int reason) {
        Log.d("onSongFinished");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onSongFinished(song, reason);
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onTransportControlFlagsChanged(int transportControlFlags) {
        Log.d("onTransportControlFlagsChanged");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onTransportControlFlagsChanged(transportControlFlags);
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onChangesComplete() {
        Log.d("onChangesComplete");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onChangesComplete();
        } finally {
            mLock.readLock().unlock();
        }
    }

    public int getSize() {
        mLock.readLock().lock();
        try {
            return mPlugins.size();
        } finally {
            mLock.readLock().unlock();
        }
    }

    public void removeAll() {
        mLock.writeLock().lock();
        try {
            mPluginTags.clear();
            mPlugins.clear();
        } finally {
            mLock.writeLock().unlock();
        }
    }

    @Override
    public void onAlbumTitleChanged(String albumTitle) {
        Log.d("onAlbumTitleChanged");
        mLock.readLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onAlbumTitleChanged(albumTitle);
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void onPlayerHaterShutdown() {
        Log.d("onPlayerHaterShutdown");
        mLock.writeLock().lock();
        try {
            for (PlayerHaterPlugin plugin : mPlugins)
                plugin.onPlayerHaterShutdown();
        } finally {
            mLock.writeLock().unlock();
        }
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PluginCollection@").append(hashCode()).append("[ ");
        mLock.readLock().lock();
        try {
            Iterator<PlayerHaterPlugin> iter = mPlugins.iterator();
            if (iter.hasNext()) {
                sb.append(iter.next().toString());
                while (iter.hasNext()) {
                    sb.append(", ").append(iter.next().toString());
                }
            }
        } finally {
            mLock.readLock().unlock();
        }
        return sb.append("]").toString();
    }

    public void writeLock() {
        mLock.writeLock().lock();
    }

    public void unWriteLock() {
        mLock.writeLock().unlock();
    }
}
