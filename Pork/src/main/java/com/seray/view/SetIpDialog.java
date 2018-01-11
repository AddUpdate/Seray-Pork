package com.seray.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.pork.R;
import com.seray.utils.LogUtil;

/**
 * Created by pc on 2018/1/9.
 */

public class SetIpDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private Misc mMisc;
    private SetIpDialog ipDialog;
    private OnPositiveClickListener positiveClickListener;

    private OnNegativeClickListener negativeClickListener;
    private Button posBtn, negBtn;
    private TextView titleView;
    private EditText ipTv,portTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_ip_dialog);
        ipDialog = this;
        initView();
    }


    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    public SetIpDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        this.mContext = context;
        this.mMisc = Misc.newInstance();
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
                        mMisc.beep();
                        if (negativeClickListener != null)
                            negativeClickListener.onNegativeClick(ipDialog);
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        mMisc.beep();
                        if (positiveClickListener != null) {
                            positiveClickListener.onPositiveClick(ipDialog, "");
                        }
                        return true;
                    }
//                    String txt = "".equals(mLastSelectedTextView.getText().toString()) ? "" : mLastSelectedTextView.getText().toString();
//                    if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent
//                            .KEYCODE_NUMPAD_9) {
////                        int i = txt.indexOf(".");
////                        if (i < 0 || (i > -1 && i > txt.length() - 4))
//                        txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
//                    } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent
//                            .KEYCODE_9) {
////                        int i = txt.indexOf(".");
////                        if (i < 0 || (i > -1 && i > txt.length() - 4))
//                        txt += keyCode - KeyEvent.KEYCODE_0;
//                    } else if (keyCode == KeyEvent.KEYCODE_E) {
//                        //  if (!txt.contains("."))
//                        txt += ".";
//                    } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
//                        if (!txt.isEmpty())
//                            txt = txt.substring(0, txt.length() - 1);
//                    } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
//                        txt = "";
//                    } else {
//                        return false;
//                    }
//                    mLastSelectedTextView.setText(txt);
//                    return true;
                }
                return false;
            }
        });
    }

    private void initView() {
        titleView = (TextView) findViewById(R.id.tv_set_ip_dialog_title);
        posBtn = (Button) findViewById(R.id.bt_set_ip_dialog_positive);
        negBtn = (Button) findViewById(R.id.bt_set_ip_dialog_negative);
        ipTv = (EditText) findViewById(R.id.tv_set_ip_dialog_ip);
        portTv = (EditText) findViewById(R.id.tv_set_ip_dialog_port);
        posBtn.setOnClickListener(this);
        negBtn.setOnClickListener(this);
        ipTv.setOnClickListener(this);
        portTv.setOnClickListener(this);

      //  mLastSelectedTextView = ipTv;

        ipTv.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    String txt = ipTv.getText().toString();
                    if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent
                            .KEYCODE_NUMPAD_9) {
                        mMisc.beep();
                        txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                    } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent
                            .KEYCODE_9) {
                        mMisc.beep();
                        txt += keyCode - KeyEvent.KEYCODE_0;
                    } else if (keyCode == KeyEvent.KEYCODE_E) {
                        mMisc.beep();
                        txt += ".";
                    } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                        mMisc.beep();
                        txt = txt.substring(0, txt.length() - 1);
                    } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                        mMisc.beep();
                        txt = "";
                    } else {
                        return false;
                    }
                    ipTv.setText(txt);
                    return true;
                }
                return false;
            }
        });
        portTv.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    String txt = portTv.getText().toString();
                    if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent
                            .KEYCODE_NUMPAD_9) {
                        mMisc.beep();
                        txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                    } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent
                            .KEYCODE_9) {
                        mMisc.beep();
                        txt += keyCode - KeyEvent.KEYCODE_0;
                    } else if (keyCode == KeyEvent.KEYCODE_E) {
                        mMisc.beep();
                        txt += "";
                    } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                        mMisc.beep();
                        txt = txt.substring(0, txt.length() - 1);
                    } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                        mMisc.beep();
                        txt = "";
                    } else {
                        return false;
                    }
                    portTv.setText(txt);
                    return true;
                }
                return false;
            }
        });
    }

    public void setTitle(@StringRes int id) {
        if (titleView != null)
            this.titleView.setText(getString(id));
    }

    public void setTitle(@NonNull String title) {
        if (titleView != null) {
            this.titleView.setText(title);
        }
    }

    @Override
    public void onClick(View v) {
        mMisc.beep();
        switch (v.getId()) {
            case R.id.bt_set_ip_dialog_positive:
                if (positiveClickListener != null) {
                    String tvIp = ipTv.getText().toString();
                    String tvPort = portTv.getText().toString();
                    if (TextUtils.isEmpty(tvIp) || TextUtils.isEmpty(tvPort)) {
                        Toast.makeText(mContext, "输入框不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String ip = tvIp + ":" + tvPort;
                    positiveClickListener.onPositiveClick(ipDialog, ip);
                }
                break;
            case R.id.bt_set_ip_dialog_negative:
                if (negativeClickListener != null)
                    negativeClickListener.onNegativeClick(ipDialog);
                break;

            case R.id.tv_set_ip_dialog_ip:
                ipTv.setTextColor(mContext.getResources().getColor(R.color.red));
                ipTv.setHintTextColor(mContext.getResources().getColor(R.color.red));
                portTv.setTextColor(mContext.getResources().getColor(R.color.black));
                portTv.setHintTextColor(mContext.getResources().getColor(R.color.black));
                break;
            case R.id.tv_set_ip_dialog_port:
                portTv.setTextColor(mContext.getResources().getColor(R.color.red));
                portTv.setHintTextColor(mContext.getResources().getColor(R.color.red));
                ipTv.setTextColor(mContext.getResources().getColor(R.color.black));
                ipTv.setHintTextColor(mContext.getResources().getColor(R.color.black));
                break;
        }
    }

    public void setOnPositiveClickListener(@StringRes int str, OnPositiveClickListener positiveClickListener) {
        Button posBtn = (Button) findViewById(R.id.bt_set_ip_dialog_positive);
        posBtn.setText(getString(str));
        this.positiveClickListener = positiveClickListener;
    }

    public void setOnNegativeClickListener(@StringRes int str, OnNegativeClickListener negativeClickListener) {
        Button negBtn = (Button) findViewById(R.id.bt_set_ip_dialog_negative);
        negBtn.setText(getString(str));
        this.negativeClickListener = negativeClickListener;
    }

    public interface OnPositiveClickListener {

        void onPositiveClick(SetIpDialog dialog, String Ip);
    }

    public interface OnNegativeClickListener {
        void onNegativeClick(SetIpDialog dialog);
    }

    private String getString(@StringRes int id) {
        return mContext.getResources().getString(id);
    }
}
