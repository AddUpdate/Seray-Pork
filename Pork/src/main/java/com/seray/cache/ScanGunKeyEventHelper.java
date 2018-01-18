package com.seray.cache;

import android.os.Handler;
import android.view.KeyEvent;

import com.seray.utils.LogUtil;

public class ScanGunKeyEventHelper {

    private final static long MESSAGE_DELAY = 500;
    private StringBuffer mStringBufferResult;
    private boolean mCaps;
    private OnScanSuccessListener mOnScanSuccessListener;
    private Handler mHandler;
    private final Runnable mScanningFishedRunnable;

    public ScanGunKeyEventHelper(OnScanSuccessListener onScanSuccessListener) {
        mOnScanSuccessListener = onScanSuccessListener;
        mStringBufferResult = new StringBuffer();
        mHandler = new Handler();
        mScanningFishedRunnable = new Runnable() {
            @Override
            public void run() {
                performScanSuccess();
            }
        };
    }

    private void performScanSuccess() {
        String barcode = mStringBufferResult.toString();
        if (mOnScanSuccessListener != null)
            mOnScanSuccessListener.onScanSuccess(barcode);
        mStringBufferResult.setLength(0);
    }

    public void analysisKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        checkLetterStatus(event);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            char aChar = getInputCode(event);
            if (aChar != 0) {
                mStringBufferResult.append(aChar);
            }
            if (keyCode == KeyEvent.KEYCODE_NUM_LOCK)   //针对
                return;
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                mHandler.removeCallbacks(mScanningFishedRunnable);
                mHandler.post(mScanningFishedRunnable);
            } else {
                mHandler.removeCallbacks(mScanningFishedRunnable);
                mHandler.postDelayed(mScanningFishedRunnable, MESSAGE_DELAY);
            }
        }
    }

    private void checkLetterStatus(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT || keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
            mCaps = event.getAction() == KeyEvent.ACTION_DOWN;
        }
    }

    private char getInputCode(KeyEvent event) {

        int keyCode = event.getKeyCode();

        char aChar;

        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            aChar = (char) ((mCaps ? 'A' : 'a') + keyCode - KeyEvent.KEYCODE_A);
        } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            aChar = (char) ('0' + keyCode - KeyEvent.KEYCODE_0);
        } else {
            switch (keyCode) {
                case KeyEvent.KEYCODE_PERIOD:
                    aChar = '.';
                    break;
                case KeyEvent.KEYCODE_MINUS:
                    aChar = mCaps ? '_' : '-';
                    break;
                case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:
                    aChar = mCaps ? '_' : '-';
                    break;
                case KeyEvent.KEYCODE_SLASH:
                    aChar = mCaps ? '?' : '/';
                    break;
                case KeyEvent.KEYCODE_BACKSLASH:
                    aChar = mCaps ? '|' : '\\';
                    break;
                case KeyEvent.KEYCODE_SEMICOLON:
                    aChar = mCaps ? ':' : ';';
                    break;
                case KeyEvent.KEYCODE_EQUALS:
                    aChar = '=';
                    break;
                default:
                    aChar = 0;
                    break;
            }
        }
        return aChar;
    }

    public interface OnScanSuccessListener {
        void onScanSuccess(String barcode);
    }

    public void onDestroy() {
        mHandler.removeCallbacks(mScanningFishedRunnable);
        mOnScanSuccessListener = null;
        mStringBufferResult = null;
    }

}