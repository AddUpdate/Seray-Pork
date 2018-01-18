package com.seray.utils;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.pork.App;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CustomEditInputListener implements View.OnKeyListener {

    private static CustomEditInputListener mEditInputListener = null;

    private Misc mMisc;

    private CustomEditInputListener() {
        mMisc = Misc.newInstance();
    }

    public static CustomEditInputListener getInstance() {
        if (mEditInputListener == null) {
            synchronized (CustomEditInputListener.class) {
                if (mEditInputListener == null) {
                    mEditInputListener = new CustomEditInputListener();
                }
            }
        }
        return mEditInputListener;
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return false;
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            mMisc.beep();
            EditText et = (EditText) view;
            String txt = et.getText().toString();
            if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent
                    .KEYCODE_NUMPAD_9) {
                int i = txt.indexOf(".");
                if (i < 0 || (i > -1 && i > txt.length() - 3))
                    txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
            } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent
                    .KEYCODE_9) {
                int i = txt.indexOf(".");
                if (i < 0 || (i > -1 && i > txt.length() - 3))
                    txt += keyCode - KeyEvent.KEYCODE_0;
            } else if (keyCode == KeyEvent.KEYCODE_E) {
                if (!txt.contains("."))
                    txt += ".";
            } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                if (!txt.isEmpty())
                    txt = txt.substring(0, txt.length() - 1);
            } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                txt = "";
            } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER || keyCode == KeyEvent
                    .KEYCODE_ENTER) {
                InputMethodManager imm = (InputMethodManager) App.getApplication().getSystemService
                        (INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_SUBTRACT) {
                return true;
            }
            et.setText(txt);
        }
        return true;
    }
}
