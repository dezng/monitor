package com.dezng.monitor.config;

import android.content.Context;
import android.content.res.Resources;

import com.dezng.monitor.R;


/**
 * Created by dengzhe on 17-12-29.
 */
public class FinalizerConfig extends BaseConfig {
    @Deprecated
    public boolean dump;
    public int top;
    public int threshold;

    public FinalizerConfig(Context context) {
        Resources resources = context.getResources();
        enable = resources.getBoolean(R.bool.moni_fina);
        frequency = resources.getInteger(R.integer.moni_fina_freq);
        dump = resources.getBoolean(R.bool.moni_fina_dump);
        top = resources.getInteger(R.integer.moni_fina_top);
        threshold = resources.getInteger(R.integer.moni_fina_thre);
    }
}
