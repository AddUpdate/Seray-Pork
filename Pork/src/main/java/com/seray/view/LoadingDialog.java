package com.seray.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.seray.pork.R;

/**
 * Created by pc on 2017/12/13.
 */

public class LoadingDialog {
    private Context context;
    private ImageView imageView;
    private Dialog dialog;

    public LoadingDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.Dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
      //  imageView= (ImageView) view.findViewById(R.id.img);
    //    Animation animation= AnimationUtils.loadAnimation(
       //         context, R.anim.dialog_enter);
        // 使用ImageView显示动画
    //    imageView.startAnimation(animation);
        dialog.setCancelable(true);
        dialog.setContentView(view);
    }
    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
