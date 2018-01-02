package com.dezng.monitor.config;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by dengzhe on 17-12-29.
 */
public class CpuConfig extends BaseConfig {
    boolean detail;

    public CpuConfig(Context context) {
        Resources resources = context.getResources();
        enable = true;
        frequency = 1000;
        detail = true;
    }
}
