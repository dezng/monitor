package com.dezng.monitor;

import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


/**
 * Created by dengzhe on 17-12-29.
 */

public class MonitorViewManager {
    public static final String TAG = "MonitorViewManager";

    Context mContext;
    WindowManager wm;
    WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
    TextView tvAcce;
    TextView tvMagn;
    TextView tvComp;

    Handler mUiHandler = new Handler(Looper.getMainLooper());


    View floatWindows;
    float x, y;
    int statusBarHeight;
    private float mTouchStartX;
    private float mTouchStartY;

    public MonitorViewManager(Context context) {
        mContext = context;

    }

    public void createWindows(Context context) {
        Log.e(TAG, "createWindows: ");
        floatWindows = LayoutInflater.from(context).inflate(R.layout.float_windows, null);

        if (statusBarHeight == 0) {
            statusBarHeight = getStatusBarHeight();
        }
        floatWindows.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getRawX();
                y = event.getRawY() - statusBarHeight;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateViewPosition();
                        break;
                    case MotionEvent.ACTION_UP:
                        updateViewPosition();
                        mTouchStartX = mTouchStartY = 0;
                        break;
                }
                return true;
            }
        });

        tvAcce = (TextView) floatWindows.findViewById(R.id.tvAcce);
        tvMagn = (TextView) floatWindows.findViewById(R.id.tvMagn);
        tvComp = (TextView) floatWindows.findViewById(R.id.tvComp);

        tvAcce.setText("haha");
        tvMagn.setText("hehe");
        tvComp.setText("hihi");


        wm = (WindowManager) context.getApplicationContext().getSystemService(Service.WINDOW_SERVICE);
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 500;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.format = 1;
        wm.addView(floatWindows, wmParams);
    }

    public void updateView(final String cpu, final String mem, final String finalizer) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (cpu != null && tvAcce != null) {
                    tvAcce.setText(String.format("cpu:%s", cpu));
                }
                if (mem != null && tvMagn != null) {
                    tvMagn.setText(String.format("mem:%s", mem));
                }
                if (finalizer != null && tvComp != null) {
                    tvComp.setText(String.format("finalizer: %s", finalizer));
                }
            }
        });
    }


    private void updateViewPosition() {
        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        if (floatWindows != null) {
            wm.updateViewLayout(floatWindows, wmParams);
        }
    }

    /**
     * get height of status bar
     *
     * @return height of status bar, if default method does not work, return 25
     */
    public int getStatusBarHeight() {
        // set status bar height to 25
        int barHeight = 25;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            barHeight = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return barHeight;
    }
}
