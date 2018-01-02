package com.dezng.monitor;

import android.content.Context;
import android.os.HandlerThread;
import android.util.Log;

import com.dezng.monitor.config.MemConfig;
import com.dezng.monitor.config.MonitorConfig;
import com.dezng.monitor.utils.MemUtils;

import static com.dezng.monitor.MonitorHandler.MSG_CPU_MONITOR;
import static com.dezng.monitor.MonitorHandler.MSG_FINALIZER;
import static com.dezng.monitor.MonitorHandler.MSG_FINALIZER_DUMP;
import static com.dezng.monitor.MonitorHandler.MSG_MEM_MONITOR;

/**
 * Created by dengzhe on 17-12-29.
 */

public class MonitorManager {

    public static final String TAG = "MonitorManager";
    private static final MonitorManager sInstance = new MonitorManager();
    Context mContext;
    MonitorConfig mConfig;
    MonitorHandler mHandler;
    MonitorViewManager mViewManager;

    HandlerThread mWorkThread;

    private MonitorManager() {

    }

    public static MonitorManager getInstance() {
        return sInstance;
    }

    // -----------------
    public static MonitorConfig getConfig() {
        return getInstance().mConfig;
    }

    public void init(Context context) {
        mContext = context;
        mConfig = new MonitorConfig(mContext);
        mViewManager = new MonitorViewManager(mContext);
    }

    public void startMonitor() {
        mWorkThread = new HandlerThread("monitor-thread");
        mWorkThread.start();
        mHandler = new MonitorHandler(mWorkThread.getLooper());

        if (mConfig.getFinalizerConfig().enable) {
            startFinalizerCount();
        }

        if (mConfig.getMemConfig().enable) {
            startMemMonitor();
        }

        if (mConfig.getCpuConfig().enable) {
            startCpuMonitor();
        }

        mViewManager.createWindows(mContext);
    }

    public void stopMonitor() {
        mWorkThread.quitSafely();
        mWorkThread.start();
    }

    public void startFinalizerCount() {
        mHandler.removeMessages(MSG_FINALIZER);
        mHandler.removeMessages(MSG_FINALIZER_DUMP);
        mHandler.sendEmptyMessage(MSG_FINALIZER);
    }

    public void startFinalizerDump() {
        mHandler.removeMessages(MSG_FINALIZER);
        mHandler.removeMessages(MSG_FINALIZER_DUMP);
        mHandler.sendEmptyMessage(MSG_FINALIZER_DUMP);
    }

    public void stopFinalizerMonitor() {
        mHandler.removeMessages(MSG_FINALIZER);
        mHandler.removeMessages(MSG_FINALIZER_DUMP);
    }

    public void switchFinalizerMonitor(boolean isStart) {
        Log.d(TAG, "switchFinalizerMonitor() called with: isStart = [" + isStart + "]");
        if (mHandler == null) {
            return;
        }
        if (isStart) {
            startFinalizerCount();
        } else {
            stopFinalizerMonitor();
        }
    }

    public void startMemMonitor() {
        MemConfig config = mConfig.getMemConfig();
        if (!config.enable) {
            return;
        }
        openOomDump();

        if (config.dump) {
            mHandler.sendEmptyMessage(MSG_MEM_MONITOR);
        }

    }

    public void stopMemMonitor() {
        mHandler.removeMessages(MSG_MEM_MONITOR);
    }

    public void dumpMemInfo() {

    }

    public void openOomDump() {
        MemUtils.openOomDump();
    }

    public void dumpMemHprofInfo() {
        dumpMemHprofInfo(mConfig.getMemConfig().file);
    }

    public void dumpMemHprofInfo(String path) {
        MemUtils.dumpHprofData(path);
    }


    private void startCpuMonitor() {
        mHandler.sendEmptyMessage(MSG_CPU_MONITOR);
    }

    public void updateView(String cpu, String mem, String finalizer) {

        mViewManager.updateView(cpu, mem, finalizer);
    }

}
