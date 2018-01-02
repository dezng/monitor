package com.dezng.monitor.utils;

import android.os.Process;
import android.util.Log;

import com.dezng.monitor.MonitorManager;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dengzhe on 17-12-29.
 */

public class CpuUtils {
    public static final String TAG = "CpuUtils";

    static char[] buffer = new char[4096];
    static StringBuilder alloc = new StringBuilder(4096);

    private static CpuUsageInfo last = new CpuUsageInfo();
    private static CpuUsageInfo current = new CpuUsageInfo();


    public static void readProcessCpuStat() {
        int pid = Process.myPid();
        String cpuStatPath = "/proc/" + pid + "/stat"; // less than 256 char
        FileReader fr = null;
        int len;
        alloc.setLength(0);
        try {
            fr = new FileReader(cpuStatPath);
            String line;
            while ((len = fr.read(buffer)) != -1) {
                alloc.append(buffer, 0, len);
            }
            String content = alloc.toString();
            String[] tok = content.split(" ");
            current.process = Long.parseLong(tok[13]) + Long.parseLong(tok[14]);
        } catch (FileNotFoundException e) {
            Log.i(TAG, "readCpuStat: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void readCpuStat() {
        ArrayList<Long> idleCpu = new ArrayList<>();
        ArrayList<Long> totalCpu = new ArrayList<>();

        FileReader fr = null;
        alloc.setLength(0);
        try {


            // monitor total and idle cpu stat of certain process
            fr = new FileReader("/proc/stat"); // less than 4098 (less than 2048 and more than 2000)
            int len = 0;
            while ((len = fr.read(buffer)) != -1) {
                alloc.append(buffer, 0, len);
            }
            String content = alloc.toString();
            String[] lines = content.split("\\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (!line.startsWith("cpu")) {
                    break;
                }
                String[] nums = line.split("\\s+");
                long idle = Long.parseLong(nums[4]);
                long all = Long.parseLong(nums[1]) + Long.parseLong(nums[2]) + Long.parseLong(nums[3]) + Long.parseLong(nums[4])
                        + Long.parseLong(nums[6]) + Long.parseLong(nums[5]) + Long.parseLong(nums[7]);
                if (i == 0) {
                    current.idle = idle;
                    current.all = all;
                }
                idleCpu.add(idle);
                totalCpu.add(all);
            }

            long processTime = current.process - last.process;
            long idleTime = current.idle - last.idle;
            long allTime = current.all - last.all;
            float proc = processTime * 100f / allTime;
            float device = 100 - idleTime * 100f / allTime;
            float rate = processTime * 100f / (allTime - idleTime);
            last.process = current.process;
            last.idle = current.idle;
            last.all = current.all;
            Log.i(TAG, "readCpuStat: " + String.format("%.2f/%.2f/%.2f", proc, device, rate));
            MonitorManager.getInstance().updateView(String.format("%.2f/%.2f/%.2f", proc, device, rate), null, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class CpuUsageInfo {
        public long process;
        public long idle;
        public long all;
    }

}
