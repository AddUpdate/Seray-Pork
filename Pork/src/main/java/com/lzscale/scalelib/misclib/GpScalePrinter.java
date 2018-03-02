package com.lzscale.scalelib.misclib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.command.GpUtils;
import com.gprinter.command.LabelCommand;
import com.seray.entity.OrderDetail;
import com.seray.pork.R;
import com.seray.utils.LogUtil;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gprinter.command.LabelCommand.FONTTYPE;
import com.gprinter.command.LabelCommand.DIRECTION;
import com.gprinter.command.LabelCommand.EEC;
import com.gprinter.command.LabelCommand.FONTMUL;
import com.gprinter.command.LabelCommand.MIRROR;
import com.gprinter.command.LabelCommand.ROTATION;
import com.gprinter.command.EscCommand.ENABLE;

/**
 * Created by pc on 2017/12/28.
 */

public class GpScalePrinter {

    private static GpScalePrinter mPrinter = null;
    private GpService mGpService = null;

    ExecutorService printThread = Executors.newSingleThreadExecutor();

    private GpScalePrinter(GpService service) {

        this.mGpService = service;
    }

    public static GpScalePrinter getInstance(GpService service) {
        if (mPrinter == null) {
            synchronized (GpScalePrinter.class) {
                if (mPrinter == null) {
                    try {
                        mPrinter = new GpScalePrinter(service);
                    } catch (Exception e) {
                        LogUtil.e(e.getMessage());
                    }
                }
            }
        }
        return mPrinter;
    }

    public void printOrder(@NonNull final OrderDetail detail, @Nullable final Runnable callBack) {
        printThread.submit(new Runnable() {
            @Override
            public void run() {
                print(detail, callBack);
            }
        });

    }

    private void print(OrderDetail detail, Runnable callback) {
        sendLabel(detail);
        if (callback != null) {
            callback.run();
        }
    }

    private void sendLabel(OrderDetail detail) {
        String name = detail.getProductName();
        String weight = String.valueOf(detail.getBigDecimalWeight());
        int number = detail.getNumber() == 0 ? 1 : detail.getNumber();
        String date = detail.getOrderDate();
        String gongsi = detail.getAlibraryName();
        String barCode = detail.getBarCode();
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(60, 40); // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(4); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(DIRECTION.BACKWARD, MIRROR.NORMAL);// 设置打印方向
        tsc.addReference(0, 0);// 设置原点坐标
        tsc.addTear(ENABLE.ON); // 撕纸模式开启
        tsc.addCls();// 清除打印缓冲区
        // 绘制图片
        //  Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.gprinter);
        //     tsc.addBitmap(20, 50, BITMAP_MODE.OVERWRITE, b.getWidth(), b);
        tsc.addQRCode(20, 10, EEC.LEVEL_L, 6, ROTATION.ROTATION_0, barCode);
        // 绘制简体中文
//        tsc.addText(15, 130, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_0, FONTMUL.MUL_1, FONTMUL.MUL_1,
//                barCode);
        tsc.addText(200, 20, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_0, FONTMUL.MUL_1, FONTMUL.MUL_2,
                name);
        tsc.addText(150, 170, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_0, FONTMUL.MUL_2, FONTMUL.MUL_2,
                "重量:" + weight + "KG");
        tsc.addText(200, 80, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_0, FONTMUL.MUL_1, FONTMUL.MUL_2,
                "件数:" + number);
        tsc.addText(30, 240, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_0, FONTMUL.MUL_1, FONTMUL.MUL_1,
                date);
        tsc.addText(30, 270, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_0, FONTMUL.MUL_1, FONTMUL.MUL_1,
                gongsi);
        // 绘制一维条码
        //   tsc.add1DBarcode(20, 250, BARCODETYPE.CODE128, 100, READABEL.EANBEL, ROTATION.ROTATION_0, "Gprinter");
        tsc.addPrint(1, 1); // 打印标签
        tsc.addSound(2, 100); // 打印标签后 蜂鸣器响
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        try {
            Vector<Byte> datas = tsc.getCommand(); // 发送数据
            byte[] bytes = GpUtils.ByteTo_byte(datas);
            String str = Base64.encodeToString(bytes, Base64.DEFAULT);
            mGpService.sendLabelCommand(0, str);
            LogUtil.d("Exception", str);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
