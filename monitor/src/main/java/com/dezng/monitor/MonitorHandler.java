package com.dezng.monitor;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dezng.monitor.utils.CpuUtils;
import com.dezng.monitor.utils.FinalizerUtils;
import com.dezng.monitor.utils.MemUtils;

/**
 * Created by dengzhe on 17-12-28.
 */

public class MonitorHandler extends Handler {
    public static final String TAG = "MonitorHandler";

    public static final int MSG_FINALIZER = 1;
    public static final int MSG_FINALIZER_DUMP = 2;
    public static final int MSG_MEM_MONITOR = 11;
    public static final int MSG_CPU_MONITOR = 21;

    public MonitorHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what) {
            case MSG_FINALIZER:
                removeMessages(MSG_FINALIZER);
                sendEmptyMessageDelayed(MSG_FINALIZER, MonitorManager.getConfig().getFinalizerConfig().frequency);
                FinalizerUtils.dumpFinalizerCount();

                break;
            case MSG_FINALIZER_DUMP:
                removeMessages(MSG_FINALIZER_DUMP);
                sendEmptyMessageDelayed(MSG_FINALIZER_DUMP, MonitorManager.getConfig().getFinalizerConfig().frequency);
                FinalizerUtils.dumpFinalzerTop();
                break;
            case MSG_MEM_MONITOR:
                removeMessages(MSG_MEM_MONITOR);
                sendEmptyMessageDelayed(MSG_MEM_MONITOR, MonitorManager.getConfig().getMemConfig().frequency);
                MemUtils.dumpMeminfo();
                break;
            case MSG_CPU_MONITOR:
                removeMessages(MSG_CPU_MONITOR);
                sendEmptyMessageDelayed(MSG_CPU_MONITOR, MonitorManager.getConfig().getMemConfig().frequency);
                CpuUtils.readProcessCpuStat();
                CpuUtils.readCpuStat();
                break;
        }

    }
}
