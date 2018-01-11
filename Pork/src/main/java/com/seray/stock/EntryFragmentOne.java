package com.seray.stock;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.seray.entity.MonitorSupplierMsg;
import com.seray.entity.PurchaseSubtotal;
import com.seray.entity.Supplier;
import com.seray.pork.R;
import com.seray.pork.SupplierSelectActivity;
import com.seray.utils.CustomEditInputListener;
import com.seray.utils.FileHelp;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

/**
 * Created by pc on 2017/11/8.
 * 首页记录采购单信息
 */

public class EntryFragmentOne extends BaseTwoFragment {
    private EditText batchNumberEt, detailsNumberEt;
    private TextView suppliersTv, stockDateTv,telTv;
    private String remark;//备注
    private String key;
    private PurchaseSubtotal mSubtotal = new PurchaseSubtotal();

    private ImageView mInputImage;
    /**
     * 进货单图片保存地址
     */
    private String inputImageName = null;
    private static final int INPUT_REQUEST = 688;

    private String dir;
    private Map<String, Object> mParameter = new HashMap<>(6);

    private CustomEditInputListener mEditInputListener = null;
    private CustomTextWatcher mTextWatcher = null;

    private MyDateSetListener mDateSetListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entry_fragment_one, container, false);
        initViewAndListener(view);
        return view;
    }

    public void initViewAndListener(View view) {
        stockDateTv = (TextView) view.findViewById(R.id.entry_frag_one_date);
        telTv = (TextView) view.findViewById(R.id.entry_frag_one_tel);
        suppliersTv = (TextView) view.findViewById(R.id.entry_frag_one_suppliers);

        batchNumberEt = (EditText) view.findViewById(R.id.entry_frag_one_ordernumber);
        detailsNumberEt = (EditText) view.findViewById(R.id.entry_frag_one_number);

        mInputImage = (ImageView) view.findViewById(R.id.entry_frag_one_input_iv);
        Button inputDeleteBtn = (Button) view.findViewById(R.id.entry_frag_one_input_delete);
        TextView mInputImgTv = (TextView) view.findViewById(R.id.entry_frag_one_input);

        stockDateTv.setText(getCurrentDay());

        mInputImgTv.setOnClickListener(this);
        inputDeleteBtn.setOnClickListener(this);
        suppliersTv.setOnClickListener(this);
        telTv.setOnClickListener(this);
        mInputImage.setOnClickListener(this);
        stockDateTv.setOnClickListener(this);

        batchNumberEt.setInputType(InputType.TYPE_NULL);
        detailsNumberEt.setInputType(InputType.TYPE_NULL);


        batchNumberEt.setOnKeyListener(mEditInputListener);
        detailsNumberEt.setOnKeyListener(mEditInputListener);

        CustomEditTouchListener touchListener = new CustomEditTouchListener();

        batchNumberEt.setOnTouchListener(touchListener);
        detailsNumberEt.setOnTouchListener(touchListener);

        mTextWatcher = new CustomTextWatcher();

        batchNumberEt.addTextChangedListener(mTextWatcher);
        detailsNumberEt.addTextChangedListener(mTextWatcher);

    }

    private String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();
        return getStringDate(time);
    }

    private String getStringDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return format.format(date);
    }

    @Override
    void clearEditFocus() {
        batchNumberEt.clearFocus();
        detailsNumberEt.clearFocus();
    }

    @Override
    void pushParameter() {
        if (sInterface != null) {
            if (mSubtotal != null)
                if (!TextUtils.isEmpty(mSubtotal.getSupplier()) && !TextUtils.isEmpty(mSubtotal.getBatchNumber())) {

                    if (TextUtils.isEmpty(mSubtotal.getTel())) {
                        mSubtotal.setTel("");
                    }
                    if (TextUtils.isEmpty(String.valueOf(mSubtotal.getDetailsNumber()))) {
                        mSubtotal.setDetailsNumber(0);
                    }
                    if (mSubtotal.getPurOrderImg() == null) {
                        mSubtotal.setPurOrderImg("");
                    }
                    mSubtotal.setRemark("");
                    sInterface.pushSubtotal(mSubtotal);
                }
        }
    }

    @Override
    void clearViewContent() {
        suppliersTv.setText("请选择供应商");
        batchNumberEt.setText("");
        telTv.setText("选择电话");
        detailsNumberEt.setText("");
        stockDateTv.setText(getCurrentDay());
        mInputImage.setImageResource(R.drawable.default_img);
        initData();
    }

    private void initData() {
        mSubtotal = new PurchaseSubtotal();
        key = KEY_SUPPLIERS;
        mSubtotal.setStockDate(getCurrentDay());
    }

    private class CustomTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String string = editable.toString();
            int length = string.length();
            int index = string.lastIndexOf(".");
            if (index == length - 1)
                string += "0";
            if (length == 0)
                string = "0";
            addSubtotal(key, string);
        }
    }

    private void addSubtotal(String key, String value) {
        if (!TextUtils.isEmpty(key) && value != null) {
            if (mSubtotal != null) {
                switch (key) {
                    case KEY_SUPPLIERS:
                        mSubtotal.setSupplier(value.equals("0") ? "" : value);
                        break;
                    case KEY_BATCH_NUMBER:
                        mSubtotal.setBatchNumber(value);
                        break;
                    case KEY_TEL:
                        mSubtotal.setTel(value);
                        break;
                    case KEY_DETAILS_NUMBER:
                        String regEx = ".";
                        Pattern pat = Pattern.compile(regEx);
                        Matcher mat = pat.matcher(value);
                        String s = mat.replaceAll("");
                        if (TextUtils.isEmpty(s))
                            s = "0";
                        mSubtotal.setDetailsNumber(Integer.valueOf(s));
                        break;
                }
            }

        }
    }

    private class CustomEditTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mMisc.beep();
                switch (view.getId()) {
                    case R.id.entry_frag_one_suppliers:
                        key = KEY_SUPPLIERS;
                        break;
                    case R.id.entry_frag_one_ordernumber:
                        key = KEY_BATCH_NUMBER;
                        break;
                    case R.id.entry_frag_one_tel:
                        key = KEY_TEL;
                        break;
                    case R.id.entry_frag_one_number:
                        key = KEY_DETAILS_NUMBER;
                        break;
                    default:
                        key = KEY_SUPPLIERS;
                        break;
                }
                EditText editText = (EditText) view;
                editText.setText("");
            }
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.entry_frag_one_input_iv:
            case R.id.entry_frag_one_input:
                key = KEY_INPUT;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                inputImageName = createPicName(KEY_INPUT);
                Uri uri = Uri.fromFile(new File(dir + inputImageName));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, INPUT_REQUEST);
                break;
            case R.id.entry_frag_one_input_delete:
                deleteImage(inputImageName);
                mInputImage.setImageResource(R.drawable.default_img);
                mSubtotal.setPurOrderImg("");
                inputImageName = "";
                break;
            case R.id.entry_frag_one_date:
                createDateDialog();
                break;
            case R.id.entry_frag_one_tel:
            case R.id.entry_frag_one_suppliers:
                startActivity(new Intent(getContext(), SupplierSelectActivity.class));
                break;
        }
    }

    private String createPicName(String key) {
        long time = System.currentTimeMillis();
        return time + "_" + key + ".png";
    }

    private void deleteImage(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            showMessage("未拍照");
        } else {
            File file = new File(dir + fileName);
            if (file.exists()) {
                boolean delete = file.delete();
                if (delete) {
                    showMessage("删除成功");
                } else
                    showMessage("删除失败");
            }
        }
    }

    /**
     * 接收相机返回信息
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            FileInputStream fis = null;
            Bitmap bitmap;
            try {
                if (requestCode == INPUT_REQUEST) {
                    fis = new FileInputStream(new File(dir + inputImageName));
                    bitmap = BitmapFactory.decodeStream(fis);
                    mInputImage.setImageBitmap(bitmap);
                    mParameter.put(key, inputImageName);
                    mSubtotal.setPurOrderImg(FileHelp.encodePurchaseImg(inputImageName));
                }
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            } finally {
                try {
                    if (fis != null)
                        fis.close();
                } catch (IOException e) {
                    LogUtil.e(e.getMessage());
                }
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initData();
        mEditInputListener = CustomEditInputListener.getInstance();
        boolean checkParentDir = checkParentDir();
        if (!checkParentDir)
            dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        else
            dir = FileHelp.STOCK_DIR;
    }


    private boolean checkParentDir() {
        File dirFile = new File(FileHelp.STOCK_DIR);
        boolean mkdirs = true;
        if (!dirFile.exists()) {
            mkdirs = dirFile.mkdirs();
        }
        return mkdirs;
    }
    // from SupplierSelectActivity
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveSupplier(Supplier msg) {
        String name = msg.getSupplierName();
        String tel = msg.getSupplierPhone();
        String supplierId = msg.getSupplierId();
        suppliersTv.setText(name);
        telTv.setText(tel);
        mSubtotal.setSupplier(name);
        mSubtotal.setTel(tel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        batchNumberEt.removeTextChangedListener(mTextWatcher);
        detailsNumberEt.removeTextChangedListener(mTextWatcher);
    }

    private void createDateDialog() {
        if (mDateSetListener == null)
            mDateSetListener = new MyDateSetListener();
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), mDateSetListener, calendar.get
                (Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.reprint_ok), new
                DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog.show();
    }

    private class MyDateSetListener implements DatePickerDialog.OnDateSetListener {
        MyDateSetListener() {
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Date data = getData(year, monthOfYear, dayOfMonth);
            String formatDate = getStringFormatDate(data)+" "+getHour();

            LogUtil.e("formatDate",formatDate);
            stockDateTv.setText(formatDate);
            mSubtotal.setStockDate(formatDate);
        }

    }

    private String getStringFormatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(date);
    }

    private Date getData(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year); // 年
        c.set(Calendar.MONTH, month); // 月
        c.set(Calendar.DAY_OF_MONTH, day); // 日
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }
    private String getHour(){
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);//时
        int minute = c.get(Calendar.MINUTE);//分
        int second = c.get(Calendar.SECOND);
        return  hour+":"+minute+":"+second;
    }
}
