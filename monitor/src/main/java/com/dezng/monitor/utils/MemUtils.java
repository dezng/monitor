package com.dezng.monitor.utils;

import android.os.Build;
import android.os.Debug;
import android.util.Log;

import com.dezng.monitor.MonitorManager;

import java.io.IOException;

/**
 * Created by dengzhe on 17-12-29.
 */

public class MemUtils {
    public static final String TAG = "MemUtils";

    public static void dumpHprofData(String path) {
        try {
            Debug.dumpHprofData(path);
        } catch (IOException e) {
            Log.e(TAG, "dumpHprofData: error! -> " + e.getMessage());
        }
    }

    public static void openOomDump() {
        try {
            ReflectUtils.invokeStatic("android.os.SystemProperties", "set", new Object[]{"debug.flyme.oomdump", "true"});
            Log.i(TAG, "openOomDump: debug.flyme.oomdump: " + ReflectUtils.invokeStatic("android.os.SystemProperties", "get", "debug.flyme.oomdump"));
        } catch (Exception e) {
            Log.e(TAG, "openOomDump: error! -> " + e.getMessage());
        }
    }

    // FIXME: 17-12-29
    public static void dumpMeminfo() {
        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memoryInfo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "dumpMeminfo: " + memoryInfo.getMemoryStat("summary.java-heap"));
        }
        Log.i(TAG, "dumpMeminfo: pss: " + memoryInfo.getTotalPss());
        MonitorManager.getInstance().updateView(null, String.valueOf(memoryInfo.getTotalPss()), null);
    }
}
