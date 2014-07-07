/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.contacts;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

public class NativeContactsSyncService extends Service {
	
    private static final String TAG = "[EASIIO]NativeContactsSyncService";
    private final LocalSyncBinder mBinder = new LocalSyncBinder();
    private final Object mLock = new Object();
    private LocalSyncControl mLocalSync;
    private boolean mActive = true;
    
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (LogLevel.DEV) {
            try {
                DevLog.d(TAG, "Service bind (" + ((Context) this).toString() + ")");
            } catch (Exception ex) {
            }
        }
        return (mBinder);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (LogLevel.DEV) {
            try {
                DevLog.w(TAG, "Sync service shutdown (" + ((Context) this).toString() + ")");
            } catch (Exception ex) {
            }
        }
        
        synchronized (mLock) {
            mActive = false;
            if(mLocalSync != null) {
            	mLocalSync.cancel();
            	mLocalSync = null;
            }
        }
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
    	super.onStart(intent, startId);
    	if (LogLevel.DEV) {
            try {
                DevLog.d(TAG, "Service onStart (" + ((Context) this).toString() + ")");
            } catch (Exception ex) {
            }
        }
        if (intent == null) {
            return;
        }
    }
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return START_STICKY;
    }
    
    public static final void startNativeContactsSync(Context context) {
        if (LogLevel.DEV) {
            DevLog.i(TAG, "startNativeContactsSync...");
        }
        Intent serviceIntent = new Intent(context, NativeContactsSyncService.class);
        context.startService(serviceIntent);
    }
    

    /**
     * Command tag.
     */
    private static final String COMMAND_TAG = "quickcall_sync_command";
    
    private static final int CALL_LOG_SYNC_COMMAND = 0x17;
    
    /**
     * Service binder. 
     */
    public class LocalSyncBinder extends Binder {
    	public NativeContactsSyncService getService() {
            return (NativeContactsSyncService.this);
        }

        public void syncMessages(long userId) {
            synchronized (mLock) {
                if (mActive) {
                    if (mLocalSync != null) {
                    	mLocalSync.cancel();
                    }
                    mLocalSync = new LocalSyncControl(mLock, userId, NativeContactsSyncService.this, getNextSyncId());
                    new MessagesSyncTask().execute(mLocalSync);
                }
            }
        }
        
    }
    
    /**
     * Keeps next sync identifier.
     */
    private static int mNextSyncId = 1;
    
    /**
     * Defines max. sync identifier.
     */
    private static int MAX_SYNC_ID = 0x7FFF;

    /**
     * Returns next sync identifier.
     * 
     * @return next sync identifier
     */
    private static int getNextSyncId() {
        int id = mNextSyncId;
        mNextSyncId++;
        if (mNextSyncId >= MAX_SYNC_ID) {
            mNextSyncId = 1;
        }
        return id;
    }
    
    /**
     * Sync control. 
     */
    public static final class LocalSyncControl {
    	
        public LocalSyncControl(Object lock, long userId, Context context, int id) {
            mLock = lock;
            mUserId = userId;
            mContext = context;
            mId = id;
            mRequestTime = SystemClock.elapsedRealtime();
            mNewRequestTime = mRequestTime;
        }

        public long getUserId() {
            return mUserId;
        }

        public void cancel() {
            synchronized (mLock) {
                mCancel = true;
            }
        }
        
        public boolean takeRequest() {
            synchronized (mLock) {
                if (!mActive || mCancel) {
                    return false;
                }
                mNewRequestTime = SystemClock.elapsedRealtime();
                return true;
            }
        }

        public boolean continueOrStop() {
            synchronized (mLock) {
                if (!mActive || mCancel) {
                    return false;
                }
                if (mNewRequestTime > mRequestTime) {
                    mRequestTime = mNewRequestTime;
                    return true;
                } else {
                    mCancel = true;
                    return false;
                }
            }
        }

        public boolean isActive() {
            synchronized (mLock) {
                return mActive;
            }
        }

        public boolean isTerminated() {
            synchronized (mLock) {
                return mCancel;
            }
        }
        
        public Context getContext() {
            return mContext;
        }

        private volatile Object mLock;
        private volatile boolean mActive = true;
        private volatile boolean mCancel = false;
        private volatile long mUserId;
        private volatile Context mContext;
        private volatile int mId;
        private volatile long mRequestTime;
        private volatile long mNewRequestTime;
        
        public String toString() {
            try {
                StringBuffer sb = new StringBuffer("(id:");
                sb.append(mId);
                sb.append("; userId:");
                sb.append(mUserId);
                sb.append("; cancel:");
                sb.append(mCancel);
                sb.append(")");
                return sb.toString();
            } catch (java.lang.Throwable error) {
            }
            return "sync(null)";
        }
    }
    
    private class MessagesSyncTask extends AsyncTask<LocalSyncControl, Void, Void> {
        @Override
        protected Void doInBackground(LocalSyncControl... syncs) {
            LocalSyncControl sync = syncs[0];
            long startTime = 0L;
            if (LogLevel.MARKET) {
                startTime = SystemClock.elapsedRealtime();
                try {
                    MarketLog.d(TAG, "Messages sync started " + sync.toString());
                } catch (Exception ex) {
                }
            }

            try {
//                if (MessagesSync.sync(sync)) {
//                    if (LogLevel.MARKET) {
//                        MarketLog.i(TAG, "Messages updated.");
//                    }
//                }
            } catch (Throwable t) {
                if (LogLevel.MARKET) {
                    MarketLog.e(TAG, "Exception in MessagesSyncTask sync " + sync.toString());
                    MarketLog.e(TAG, "Exception in MessagesSyncTask:", t);
                }
            }

            if (LogLevel.MARKET) {
                try {
                    MarketLog.d(TAG, "Messages sync finished [time="
                            + (SystemClock.elapsedRealtime() - startTime) + "] " + sync.toString());
                } catch (Exception ex) {
                }
            }

            return (null);
        }

        @Override
        protected void onProgressUpdate(Void... unused) {
        }

        @Override
        protected void onPostExecute(Void unused) {
        }
    }
    
}
