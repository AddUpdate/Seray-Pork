package com.seray.card;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;
import com.skyworth.splicing.SerialPortUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RFIDPay extends PayBase {

    private RFIDPay that = this;
    private SerialPortUtil mSerialPortUtil;
    private Misc mMisc;

    public RFIDPay() {
        super(48 * 1000); // 48 秒
        mMisc = Misc.newInstance();
    }

    /**
     * 读取卡
     */
    @Override
    public void readCardNumber(ReturnValueCallback callback) {
        this.setAutoReadBlockData(callback, new ReturnValueCallback() {

            @Override
            public void run(ReturnValue rv) {

                if (rv.getIsSuccess()) {

                    final String remark = "读卡";

                            that.readData(remark, new ReadDataListener() {

                                @Override
                                public void readDataCallback(ReturnValue result) {

                            if (result.getIsSuccess()) {//读取成功

                                mMisc.beepEx(100, 50, 3);

                                Object tag = result.getTag();//卡号
                                // TODO: 2018/3/9 卡号
                                String userCardNum = (String) tag;

                                postReadCardInfo(userCardNum);

                            } else {
                                mMisc.beepWarn();
                                that.callReturnValueCallback(result);
                            }
                        }
                    });
                } else {
                    that.callReturnValueCallback(rv);
                }
            }
        });
    }

    /**
     * 取消读取卡操作
     */
    @Override
    public void cancelReadCard(@NonNull ReturnValueCallback callback) {
        if (this.mSerialPortUtil == null) {
            callback.run(new ReturnValue(false, "", "未发起读卡请求"));
            return;
        }
        this.returnValueCallback = callback;
        this.callReturnValueCallback(new ReturnValue(false, "", "已取消读卡"));
    }

    // 初始化串口, 如果已经打开则先关闭
    private String initSerialPortUtil() {
        try {
            this.closeSerialPort();
            this.mSerialPortUtil = new SerialPortUtil("/dev/ttymxc2", 9600);
            return null;
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return "初始化串口失败:" + e.getMessage();
        }
    }

    private boolean sendData(byte[] data, String explain) {
        return this.sendData(data, explain, true);
    }

    // 发送数据
    private boolean sendData(byte[] data, String explain, boolean autoCallReturn) {
        if (this.mSerialPortUtil.sendBuffer(data)) {
            mMisc.beepEx(100, 50, 1);
            return true;
        }
        if (autoCallReturn) {
            ReturnValue rv = new ReturnValue(false, "-2", "发送" + explain + "数据失败");
            this.callReturnValueCallback(rv);
        }
        return false;
    }

    private void readData(String explain, ReadDataListener listener) {
        this.readData(this.timeout, explain, listener);
    }

    private void readData(int tTimeout, final String explain, final ReadDataListener listener) {

        this.mSerialPortUtil.readBuffer(tTimeout, new SerialPortUtil.OnDataReceiveListener() {

            @Override
            public void onDataReceive(SerialPortUtil.DataReceType type, byte[] buffer, int size) {

                ReturnValue result;
                String faildStr = "";
                boolean flag = false;
                if (SerialPortUtil.DataReceType.timeout == type) {
                    faildStr = "超时";
                } else if (SerialPortUtil.DataReceType.success != type) {
                    faildStr = "失败";
                } else {
                    flag = true;
                }
                if (!flag) {
                    //105 原 等于 -2
                    result = new ReturnValue(false, "105", "读取" + explain + faildStr);
                    listener.readDataCallback(result);
                    return;
                }

                String hex = that.getHexStr(buffer, size);

                if (that.isOk(buffer, size)) {

                    byte[] cont = that.getContent(buffer, size);
                    String strRes = that.getHexStr(cont, 0, cont.length).replace(" ", "");

                    result = new ReturnValue(true, "0", strRes);

                } else {
                    result = new ReturnValue(false, "-1", "校验" + explain + "应答数据有误:" + hex);
                }

                listener.readDataCallback(result);
            }
        });
    }

    private void hand(ReturnValueCallback callback, final Runnable run) {
        if (!this.isCanLaunchRequest(callback))
            return;
        run.run();
    }

    /**
     * 解析读取到的卡内信息
     */
    private SparseArray<?> parseCardInfo(String jsonStr) {
        try {
            JSONArray array = new JSONArray(jsonStr);
            SparseArray<?> users = new SparseArray<>();
            NumFormatUtil numUtil = NumFormatUtil.getInstance();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                int type = jsonObject.getInt("QueryType");
                String cardNum = jsonObject.getString("CardNum");
                String cardId = jsonObject.getString("CardId");
                String plateNumber = jsonObject.getString("PlateNumber");
                String workUnit = jsonObject.getString("WorkUnit");
                String contactNum = jsonObject.getString("ContactNum");
                String compellation = jsonObject.getString("Compellation");
                double balance = jsonObject.getDouble("CurrentBalance");
                int integral = jsonObject.getInt("CurrentIntegral");
                boolean enablePassword = jsonObject.getBoolean("EnablePassword");
                String remark = jsonObject.getString("Remark");
//                MemberCardInfo info = new MemberCardInfo();
//                info.setType(type);
//                info.setCardId(cardId);
//                info.setPlateNumber(plateNumber);
//                info.setCardNum(cardNum);
//                info.setWorkUnit(workUnit);
//                info.setCompellation(compellation);
//                info.setContactNum(contactNum);
//                info.setBalance(numUtil.getDecimalAmount(Double.toString(balance)));
//                info.setCurrentIntegral(integral);
//                info.setEnablePassword(enablePassword);
//                info.setRemark(remark);
                users.put(type, null);
            }
            return users;
        } catch (JSONException e) {
            LogUtil.e(e.getMessage());
        }
        return null;
    }

    private void postReadCardInfo(String cardNum) {
        ReturnValue value = new ReturnValue(true, String.valueOf(100), cardNum);
//        ReturnValue rv = getCardInfoFromHttp(cardNum);
//        that.callReturnValueCallback(rv);
        that.callReturnValueCallback(value);
    }

    /**
     * 联网获取卡内信息
     */
    private ReturnValue getCardInfoFromHttp(String cardId) {
//        ApiResult result = RtfPost.readCardPost(cardId, apiUserName, apiPassword);
        ReturnValue rv = null;
//        if (result.Result || result.ResultCode == 1 || result.ResultCode == -12) {
//            rv = getReadCardInfo(result.ResultMessage);
//            rv.setIsSuccess(result.Result);
//            rv.setRemark("在线交易");
//            if (rv.getIsSuccess()) {

//            }
//        } else {
//            rv = new ReturnValue(false, "", result.ResultMessage);
//            rv.setCardId(cardId);
//            rv.setRemark("离线交易");
//        }
//        rv.setCode(String.valueOf(result.ResultCode));
        return rv;
    }

    private void closeSerialPort() {
        if (mSerialPortUtil != null) {
            mSerialPortUtil.closeSerialPort();
            mSerialPortUtil = null;
        }
    }

    @Override
    protected boolean isCanLaunchRequest(ReturnValueCallback callback) {
        if (super.isCanLaunchRequest(callback)) {
            String msg = this.initSerialPortUtil();
            if (msg == null) {
                return true;
            }
            this.callReturnValueCallback(new ReturnValue(false, "-3", msg));
        }
        return false;
    }

    @Override
    protected void callReturnValueCallback(ReturnValue result) {
        this.closeSerialPort();
        super.callReturnValueCallback(result);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.closeSerialPort();
    }

    private void setAutoReadBlockData(ReturnValueCallback finalCallback, final ReturnValueCallback callback) {

        this.hand(finalCallback, new Runnable() {

            @Override
            public void run() {

                final String remark = "设置自动读卡模式";

                if (!that.sendData(that.getSetAutoReadBlockData(), remark))
                    return;

                that.readData(remark, new ReadDataListener() {

                    @Override
                    public void readDataCallback(ReturnValue result) {
                        callback.run(result);
                    }
                });
            }
        });
    }

    private byte[] getSetAutoReadBlockData() {
        int offset = 0;
        byte[] data = new byte[255];
        // SEQNR
        data[offset++] = 0x00;
        // CMD
        data[offset++] = 0x26;
        // Length
        data[offset++] = 0x04;
        // 数据正文 DATA
        data[offset++] = 0x00;
        data[offset++] = 0x00;
        data[offset++] = 0x19;
        data[offset++] = 0x01;
        return this.getSendData(data, offset);
    }

    // 获取发送数据
    private byte[] getSendData(byte[] data, int offset) {
        int index = 0;
        byte[] sendData = new byte[offset + 3];
        // STX (start)
        sendData[index++] = 0x20;
        System.arraycopy(data, 0, sendData, index, offset);
        index += offset;
        // BCC
        sendData[index++] = this.getXORBack(sendData, 1, index);
        // ETX (end)
        sendData[index] = 0x03;
        return sendData;
    }

    private boolean isOk(byte[] oData, int size) {
        // 先判断长度
        if (size < 6)
            return false;
        byte[] data;
        if (oData[0] == 0x06) { // 去除 06
            data = new byte[size - 1];
            System.arraycopy(oData, 1, data, 0, data.length);
        } else {
            data = oData;
        }
        // 再判断开始与结束 标志
        if (data[0] == 0x20 && data[size - 1] == 0x03) {
            // 再较验 BCC
            if (data[size - 2] == this.getXORBack(data, 1, size - 2)) {
                return true;
            }
        }
        return false;
    }

    // 获取报文正文数据
    protected byte[] getContent(byte[] data, int size) {
        byte[] cont = new byte[size - 6];
        if (cont.length > 0) {
            System.arraycopy(data, 4, cont, 0, cont.length);
        }
        return cont;
    }

    interface ReadDataListener {
        void readDataCallback(ReturnValue result);
    }
}
