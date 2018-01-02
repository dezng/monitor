package com.dezng.monitor;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by dengzhe on 17-12-28.
 */

public class MonitorInitProvider extends ContentProvider {
    public static final String TAG = "MonitorInitProvider";
    Context mContext;

    @Override
    public boolean onCreate() {
        mContext = getContext().getApplicationContext();
        MonitorManager.getInstance().init(mContext);
        Log.i(TAG, "onCreate: startMonitor: ");
        if (MonitorManager.getConfig().enable) {
            MonitorManager.getInstance().startMonitor();
        }
        return false;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        Log.d(TAG, "openFile() called with: uri = [" + uri + "], mode = [" + mode + "]");
        String path = uri.getPath();
        if (!TextUtils.isEmpty(path) && path.length() > 0) {
            path = path.substring(1);
        }
        File file = new File(path);
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query() called with: uri = [" + uri + "], projection = [" + projection + "], selection = [" + selection + "], selectionArgs = [" + selectionArgs + "], sortOrder = [" + sortOrder + "]");
        String path = uri.getPath();
        if ("/finalizer".equals(path)) {
            if ("start".equals(selection)) {
                MonitorManager.getInstance().switchFinalizerMonitor(true);
            } else if ("stop".equals(selection)) {
                MonitorManager.getInstance().switchFinalizerMonitor(false);
            }
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "getType() called with: uri = [" + uri + "]");
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert() called with: uri = [" + uri + "], values = [" + values + "]");
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete() called with: uri = [" + uri + "], selection = [" + selection + "], selectionArgs = [" + selectionArgs + "]");
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update() called with: uri = [" + uri + "], values = [" + values + "], selection = [" + selection + "], selectionArgs = [" + selectionArgs + "]");
        return 0;
    }

}
