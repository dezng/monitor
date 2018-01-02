package com.dezng.monitor.config;

import android.content.Context;
import android.content.res.Resources;

import com.dezng.monitor.R;


/**
 * Created by dengzhe on 17-12-29.
 */
public class MemConfig extends BaseConfig {
    // dump mem if oom
    public boolean dump;
    // dump file if oom
    public String file;

    public MemConfig(Context context) {
        Resources resources = context.getResources();
        enable = resources.getBoolean(R.bool.moni_mem);
        frequency = resources.getInteger(R.integer.moni_mem_freq);
        dump = resources.getBoolean(R.bool.moni_mem_dump);
        file = resources.getString(R.string.moni_mem_file);
    }
}
