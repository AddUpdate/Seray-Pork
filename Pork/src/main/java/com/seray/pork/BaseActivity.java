package com.seray.pork;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.cache.CacheHelper;
import com.seray.stock.InBulkQuantityActivity;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by pc on 2017/11/3.
 */

public class BaseActivity extends FragmentActivity implements View.OnClickListener {

    Toast mToast;
    String msg;
    /**
     * 执行http请求
     */
    public static ExecutorService httpQueryThread = Executors.newFixedThreadPool(5);
    /**
     * 执行数据库查询固定数量线程池
     */
    public static ExecutorService sqlQueryThread = Executors.newFixedThreadPool(5);

    public static ExecutorService sqlInsertThread = Executors.newSingleThreadExecutor();

    /**
     * 执行定时任务固定数量线程池
     */
    public static ScheduledExecutorService timerThreads = Executors.newScheduledThreadPool(1);
    /**
     * 蜂鸣器控制器
     */
    public Misc mMisc;
    private Handler mHandler = new Handler();
    Runnable showRun = new Runnable() {
        @Override
        public void run() {
            if (mToast == null) {
                mToast = Toast.makeText(App.getApplication(), msg, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(msg);
            }
            mToast.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMisc = Misc.newInstance();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F3) {
            mMisc.beep();
            shutDown();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            mMisc.beep();
            finish();
        }
        return true;
    }

    /**
     * 显示吐司
     */
    public void showMessage(int msg) {
        try {
            this.showMessage(getResources().getString(msg));
        } catch (Resources.NotFoundException e) {
            LogUtil.w("please check your msg id,make sure it's used string resource id");
        }
    }

    /**
     * 显示吐司
     */
    public void showMessage(final String msg) {
        this.msg = msg;
        mHandler.post(showRun);
    }


    /**
     * 关机操作
     */
    public void shutDown() {
        Intent shutDownIntent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        shutDownIntent.putExtra("android.intent.extra.KEY_CONFIRM", true);
        shutDownIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(shutDownIntent);
    }

    /**
     * 默认模式启动Activity（无对象传递）
     */
    public void startActivity(Class<?> targetActivity) {
        startActivity(getSkipIntent(targetActivity));
    }

    protected Intent getSkipIntent(Class<?> targetActivity) {
        return new Intent(getApplicationContext(), targetActivity);
    }

    @Override
    public void onClick(View v) {
        mMisc.beep();
    }
    /**
     * 随机二道密码
     */
    private String randomPwd;
    /**
     * 创建密码对话框并支持小键盘输入
     */
    public void openManageKey(final int flag) {
        randomPwd = NumFormatUtil.getRandomPassword(flag);
        LayoutInflater factory = LayoutInflater.from(this);
        View view = factory.inflate(R.layout.user_dialog, null);
        final EditText userPwdEdit = (EditText) view.findViewById(R.id.password_edit);
        userPwdEdit.setCursorVisible(false);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.operation_usercode)
                .setView(view)
                .setPositiveButton(R.string.reprint_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userPwd = userPwdEdit.getText().toString();
                        if (userPwd.equals(randomPwd)) {
                            canCloseDialog(dialog, true);
                            switch (flag) {
                                case NumFormatUtil.PASSWORD_TO_OPERATION:
                                    startActivity(OperationActivity.class);
                                    break;
                                case NumFormatUtil.PASSWORD_TO_SETTING:
                                    startActivity(ManagementActivity.class);
                                    break;
                            }
                        } else {
                            canCloseDialog(dialog, false);
                            showMessage(getResources().getString(R.string
                                    .operation_usercode_wrong));
                            userPwdEdit.setText("");
                        }
                    }
                }).setNegativeButton(R.string.reprint_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        canCloseDialog(dialog, true);
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            mMisc.beep();
                            String txt = userPwdEdit.getText().toString();
                            if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent
                                    .KEYCODE_NUMPAD_9) {
                                txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                            } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent
                                    .KEYCODE_9) {
                                txt += keyCode - KeyEvent.KEYCODE_0;
                            } else if (keyCode == KeyEvent.KEYCODE_E) {
                                txt += ".";
                            } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                                if (!txt.isEmpty()) {
                                    txt = txt.substring(0, txt.length() - 1);
                                }
                            } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                                txt = "";
                            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                                InputMethodManager imm = (InputMethodManager) getSystemService
                                        (Context.INPUT_METHOD_SERVICE);
                                if (imm != null)
                                    imm.hideSoftInputFromWindow(userPwdEdit.getWindowToken(),
                                            InputMethodManager.HIDE_NOT_ALWAYS);
                                return true;
                            }
                            userPwdEdit.setText(txt);
                            userPwdEdit.setSelection(txt.length());
                        }
                        return true;
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    /**
     * 设置是否允许关闭Dialog
     */
    public void canCloseDialog(DialogInterface dialogInterface, boolean close) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, close);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }

}
