package com.seray.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.InputType;
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
 * Created by pc on 2018/1/10.
 */

public class LoginDialog extends Dialog implements View.OnClickListener{
    private Context mContext;
    private Misc mMisc;
    private LoginDialog mDialog;
    private EditText telEt,passwordEt;
    private TextView titleView;
    private Button posBtn;

    private OnPositiveClickListener positiveClickListener;

    public LoginDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        this.mContext = context;
        this.mMisc = Misc.newInstance();
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
                        mMisc.beep();

                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        mMisc.beep();
                        if (positiveClickListener != null) {
                            positiveClickListener.onPositiveClick(mDialog, "","");
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog);
        mDialog = this;
        initView();
    }

    private void initView() {
        titleView = (TextView) findViewById(R.id.tv_set_ip_dialog_title);
        telEt = (EditText) findViewById(R.id.et_login_tel);
        passwordEt = (EditText) findViewById(R.id.et_login_password);
        posBtn = (Button) findViewById(R.id.bt_login_dialog_positive);
        telEt.setInputType(InputType.TYPE_NULL);
        passwordEt.setInputType(InputType.TYPE_NULL);
        posBtn.setOnClickListener(this);

        telEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    String txt = telEt.getText().toString();
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
                    telEt.setText(txt);
                    return true;
                }
                return false;
            }
        });
        passwordEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    String txt = passwordEt.getText().toString();
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
                    passwordEt.setText(txt);
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
    public void setOnPositiveClickListener(@StringRes int str, OnPositiveClickListener positiveClickListener) {
        Button posBtn = (Button) findViewById(R.id.bt_login_dialog_positive);
        posBtn.setText(getString(str));
        this.positiveClickListener = positiveClickListener;
    }

    @Override
    public void onClick(View v) {
        mMisc.beep();
        switch (v.getId()) {
            case R.id.bt_login_dialog_positive:
                if (positiveClickListener != null) {
                    String tel = telEt.getText().toString();
                    String password = passwordEt.getText().toString();
                    if (TextUtils.isEmpty(tel) || TextUtils.isEmpty(password)) {
                        Toast.makeText(mContext, "手机号和密码不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    positiveClickListener.onPositiveClick(mDialog, tel,password);
                }
                break;

            case R.id.et_login_tel:
                telEt.setTextColor(mContext.getResources().getColor(R.color.red));
                telEt.setHintTextColor(mContext.getResources().getColor(R.color.red));
                passwordEt.setTextColor(mContext.getResources().getColor(R.color.black));
                passwordEt.setHintTextColor(mContext.getResources().getColor(R.color.black));
                break;
            case R.id.et_login_password:
                passwordEt.setTextColor(mContext.getResources().getColor(R.color.red));
                passwordEt.setHintTextColor(mContext.getResources().getColor(R.color.red));
                telEt.setTextColor(mContext.getResources().getColor(R.color.black));
                telEt.setHintTextColor(mContext.getResources().getColor(R.color.black));
                break;
        }
    }

    public interface OnPositiveClickListener {

        void onPositiveClick(LoginDialog dialog, String tel,String password);
    }

    private String getString(@StringRes int id) {
        return mContext.getResources().getString(id);
    }

}
