package com.dezng.monitor.utils;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.dezng.monitor.MonitorManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by dengzhe on 17-12-29.
 */

public class FinalizerUtils {

    public static final String TAG = "FinalizerUtil";

    public void startMonitor(Context config) {

    }

    public static void dumpFinalizerCount() {
        int threshold = MonitorManager.getConfig().getFinalizerConfig().threshold;

        String clazzName = "java.lang.ref.FinalizerReference";
        String fieldName = "head";
        Object obj = null;

        long start = SystemClock.elapsedRealtimeNanos();
        try {
            obj = ReflectUtils.getStaticFieldValue(clazzName, fieldName);
        } catch (Exception e) {
            Log.e(TAG, "reflect field error: " + clazzName + "@" + fieldName);
        }
        int count = 0;
        while (obj != null) {
            count++;
            try {
                obj = ReflectUtils.getFieldValue(obj, "next");
            } catch (NoSuchFieldException e) {
                Log.e(TAG, "reflect field error: " + clazzName + "@next");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (count >= threshold) {
            int top = MonitorManager.getConfig().getFinalizerConfig().top;
            if (top > 0) {
                MonitorManager.getInstance().startFinalizerDump();
            }
        }
        MonitorManager.getInstance().updateView(null, null, String.valueOf(count));

        Log.e(TAG, "FinalizerReference in queue: \t" + count + " \t" + TimeUnit.NANOSECONDS.toMillis(SystemClock.elapsedRealtimeNanos() - start));
    }

    public static void dumpFinalzerTop() {

        int threshold = MonitorManager.getConfig().getFinalizerConfig().threshold;
        int top = MonitorManager.getConfig().getFinalizerConfig().top;

        String clazzName = "java.lang.ref.FinalizerReference";
        String fieldName = "head";
        Object obj = null;
        HashMap<String, Integer> countMap = new HashMap<>();


        long start = SystemClock.elapsedRealtimeNanos();
        try {
            obj = ReflectUtils.getStaticFieldValue(clazzName, fieldName);
        } catch (Exception e) {
            Log.e(TAG, "reflect field error: " + clazzName + "@" + fieldName);
        }
        int count = 0;
        while (obj != null) {
            count++;
            Object o = null;
            try {
                o = ReflectUtils.getFieldValue(obj, "referent");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (o == null) {
                    try {
                        o = ReflectUtils.getFieldValue(obj, "zombie");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                String type;
                if (o == null) {
                    type = null;
                } else {
                    type = o.getClass().getCanonicalName();
                }
                Integer num = countMap.get(type);
                if (num == null) {
                    num = 1;
                } else {
                    num++;
                }
                countMap.put(type, num);

                try {
                    obj = ReflectUtils.getFieldValue(obj, "next");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchFieldException e) {
                Log.e(TAG, "reflect field error: " + clazzName + "@next");
            }
        }
        Log.e(TAG, "FinalizerReference in queue: \t" + count + " \t" + TimeUnit.NANOSECONDS.toMillis(SystemClock.elapsedRealtimeNanos() - start));
        start = SystemClock.elapsedRealtimeNanos();
        List<Map.Entry<String, Integer>> data = new ArrayList<>(countMap.entrySet());
        Collections.sort(data, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.size() && i < top; i++) {
            Map.Entry<String, Integer> entry = data.get(i);
            sb.append("\n");
            sb.append(entry.getValue());
            sb.append("\t\t");
            sb.append(entry.getKey());
        }
        Log.e(TAG, "FinalizerReference table: " + TimeUnit.NANOSECONDS.toMillis(SystemClock.elapsedRealtimeNanos() - start));
        Log.i(TAG, sb.toString());
        if (count < threshold) {
            MonitorManager.getInstance().startFinalizerCount();
        }
        countMap.clear();
    }
}
