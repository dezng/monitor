package com.dezng.monitor.config;

import android.content.Context;

import com.dezng.monitor.R;


/**
 * Created by dengzhe on 17-12-29.
 */

public class MonitorConfig {

    public boolean enable;

    private DeviceInfo mDeviceInfo;
    private CpuConfig mCpuConfig;
    private MemConfig mMemConfig;
    private FinalizerConfig mFinalizerConfig;

    public MonitorConfig(Context context) {
        enable = context.getResources().getBoolean(R.bool.monitor);
        mFinalizerConfig = new FinalizerConfig(context);
        mMemConfig = new MemConfig(context);
        mCpuConfig = new CpuConfig(context);
    }

    public final FinalizerConfig getFinalizerConfig() {
        return mFinalizerConfig;
    }

    public final MemConfig getMemConfig() {
        return mMemConfig;
    }

    public final CpuConfig getCpuConfig() {
        return mCpuConfig;
    }
}
