package com.seray.stock;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.seray.entity.PurchaseDetail;
import com.seray.pork.ProductsSelectActivity;
import com.seray.pork.R;
import com.seray.utils.CustomEditInputListener;
import com.seray.utils.FileHelp;
import com.seray.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class EntryFragmentTwo extends BaseTwoFragment {

    private TextView productNameTv, pluCodeTv, unitTv;
    private EditText quantityEt, numberEt, unitPriceEt;
    private LinearLayout llQuantityEt, llNumber;
    private Button keepBt;
    private Spinner unitSpinner;

    private CustomEditInputListener mEditInputListener = null;
    private CustomTextWatcher mTextWatcher = null;

    private PurchaseDetail mDetail;

    private String key = null;
    private ImageView mCerImage;

    private List<String> unitDataList = new ArrayList<>();
    private ArrayAdapter<String> unit_adapter;
    /**
     * 合格证图片保存地址
     */
    private String cerImageName = null;
    private static final int CER_REQUEST = 677;

    private String dir;
    private PurchaseDetail mLastDetail = new PurchaseDetail();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entry_fragment_two, container, false);
        initViewAndListener(view);
        return view;
    }

    public void initViewAndListener(View view) {
        unitDataList.add("KG");
        unitDataList.add("袋");
        unitDataList.add("箱");

        productNameTv = (TextView) view.findViewById(R.id.entry_frag_two_product_name);
        pluCodeTv = (TextView) view.findViewById(R.id.entry_frag_two_plucode);
        unitTv = (TextView) view.findViewById(R.id.entry_frag_two_online_unit);

        unitSpinner = (Spinner) view.findViewById(R.id.entry_frag_two_spinner);
        unitSpinner.setGravity(Gravity.CENTER);
        quantityEt = (EditText) view.findViewById(R.id.entry_frag_two_quantity);
        numberEt = (EditText) view.findViewById(R.id.entry_frag_two_number);
        unitPriceEt = (EditText) view.findViewById(R.id.entry_frag_two_unit_piece);
        mCerImage = (ImageView) view.findViewById(R.id.entry_frag_two_cer_iv);
        Button cerDeleteBtn = (Button) view.findViewById(R.id.entry_frag_two_cer_delete);
        TextView mCerImgTv = (TextView) view.findViewById(R.id.entry_frag_two_cer);
        keepBt = (Button) view.findViewById(R.id.entry_frag_two_keep);

        llQuantityEt = (LinearLayout) view.findViewById(R.id.ll_frag_two_quantity);
        llNumber = (LinearLayout) view.findViewById(R.id.ll_frag_two_number);

        mCerImage.setOnClickListener(this);
        cerDeleteBtn.setOnClickListener(this);
        mCerImgTv.setOnClickListener(this);
        keepBt.setOnClickListener(this);

        productNameTv.setOnClickListener(this);
        pluCodeTv.setOnClickListener(this);

        quantityEt.setInputType(InputType.TYPE_NULL);
        numberEt.setInputType(InputType.TYPE_NULL);
        unitPriceEt.setInputType(InputType.TYPE_NULL);

        quantityEt.setOnKeyListener(mEditInputListener);
        numberEt.setOnKeyListener(mEditInputListener);
        unitPriceEt.setOnKeyListener(mEditInputListener);

        CustomEditTouchListener touchListener = new CustomEditTouchListener();

        quantityEt.setOnTouchListener(touchListener);
        numberEt.setOnTouchListener(touchListener);
        unitPriceEt.setOnTouchListener(touchListener);

        mTextWatcher = new CustomTextWatcher();

        quantityEt.addTextChangedListener(mTextWatcher);
        numberEt.addTextChangedListener(mTextWatcher);
        unitPriceEt.addTextChangedListener(mTextWatcher);
        //适配器
        unit_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, unitDataList);
        //设置样式
        unit_adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unit_adapter);

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!TextUtils.isEmpty(mDetail.getUnit())) {
                    mMisc.beep();
                }
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(R.color.white));    //设置颜色
                tv.setTextSize(25.0f);    //设置大小
                tv.setGravity(Gravity.CENTER_HORIZONTAL);   //设置居中
                mDetail.setUnit(unitDataList.get(position));
                if (unitDataList.get(position).equals("KG")) {
                    llQuantityEt.setVisibility(View.VISIBLE);
                    llNumber.setVisibility(View.GONE);
                } else {
                    llQuantityEt.setVisibility(View.GONE);
                    llNumber.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
            addDetail(key, string);
        }
    }

    private class CustomEditTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mMisc.beep();
                switch (view.getId()) {
//                        detail = new PurchaseDetail("大白菜" + count, "1001", new BigDecimal(200d),
//                                new BigDecimal(5.00d),  new BigDecimal(200d),"合格", BigDecimal.ZERO, "KG", "测试001");
                    case R.id.entry_frag_two_quantity:
                        key = KEY_QUANTITY;
                        break;
                    case R.id.entry_frag_two_unit_piece:
                        key = KEY_UNIT_PRICE;
                        break;
                    case R.id.entry_frag_two_number:
                        key = KEY_NUMBER;
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
            case R.id.entry_frag_two_plucode:
            case R.id.entry_frag_two_product_name:
                Intent intent1 = new Intent(getContext(), ProductsSelectActivity.class);
                //     intent1.putExtra("FromWhere", 2);
                startActivity(intent1);
                break;
            case R.id.entry_frag_two_cer_iv:
            case R.id.entry_frag_two_cer:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cerImageName = createPicName(KEY_CER);
                Uri uri = Uri.fromFile(new File(dir + cerImageName));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, CER_REQUEST);
                break;
            case R.id.entry_frag_two_cer_delete:
                deleteImage(cerImageName);
                mCerImage.setImageResource(R.drawable.default_img);
                cerImageName = "";
                mDetail.setCertificateImg(cerImageName);
                break;
            case R.id.entry_frag_two_keep:
                if (submitCheck()) {
                    if (mDetail.getCertificateImg() == null) {
                        mDetail.setCertificateImg("");
                    }
                    mDetail.setSellQuantity(new BigDecimal("0"));
                    mDetail.setRemark("");
                    sInterface.pushDetail(mDetail);
                    mLastDetail = mDetail;
                    mDetail = null;
                    clearViewContent();
                } else {
                    showMessage("将信息填写完整");
                }
                break;
        }
    }

    private boolean submitCheck() {
        boolean check = false;
        if (sInterface != null && mDetail != null && !mDetail.equals(mLastDetail)) {
            if (!TextUtils.isEmpty(mDetail.getProductName()) && !TextUtils.isEmpty(mDetail.getPluCode())) {
                if (mDetail.getUnit().equals("KG")) {
                    if (mDetail.getDecimalQuantity().compareTo(new BigDecimal(0)) > 0) {
                        check = true;
//                        if (mDetail.getNumber() == 0){
//                            mDetail.setNumber(0);
//                        }
                    } else
                        check = false;
                } else {
                    if (mDetail.getNumber() > 0) {
                        check = true;
                        if (mDetail.getDecimalQuantity() == null) {
                            mDetail.setQuantity(BigDecimal.ZERO);
                        }
                    } else
                        check = false;
                }
            } else
                check = false;
        } else
            check = false;
        return check;
    }


    private void addDetail(String key, String value) {
        if (mDetail != null) {
            switch (key) {
                case KEY_QUANTITY:
                    mDetail.setQuantity(new BigDecimal(value.lastIndexOf(".") == 0 ? "0" + value : value));
                    // addPrice(mDetail.getDecimalUnitPrice() != null);
                    break;
                case KEY_UNIT_PRICE:
                    mDetail.setUnitPrice(new BigDecimal(value));
                    //      addPrice(mDetail.getDecimalQuantity() != null);
                    break;

                case KEY_NUMBER:
                    value = value.replace(".", "");
                    mDetail.setNumber(Integer.valueOf(value));
                    break;
            }

        }
    }

    @Override
    void clearEditFocus() {
        quantityEt.clearFocus();
        numberEt.clearFocus();
        unitPriceEt.clearFocus();
    }

    @Override
    void pushParameter() {

    }

    @Override
    void clearViewContent() {
        productNameTv.setText("请选择品名");
        pluCodeTv.setText("请选择PLU");
        unitTv.setText("");
        quantityEt.setText("");
        numberEt.setText("");
        unitPriceEt.setText("");
        mCerImage.setImageResource(R.drawable.default_img);
        initData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        EventBus.getDefault().register(this);
        key = KEY_PRODUCT_NAME;
        mEditInputListener = CustomEditInputListener.getInstance();
        boolean checkParentDir = checkParentDir();
        if (!checkParentDir)
            dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        else
            dir = FileHelp.STOCK_DIR;
    }

    private void initData() {
        mDetail = new PurchaseDetail();
        mDetail.setNumber(0);
        mDetail.setQuantity(BigDecimal.ZERO);
    }

    // from ProductsSelectActivity
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveProducts(PurchaseDetail msg) {
        String proName = msg.getProductName();
        String pluCode = msg.getPluCode();
        String unitType = msg.getUnit();
        String partentName = msg.getParentsItemName();
        productNameTv.setText(proName);
        pluCodeTv.setText(pluCode);
        mDetail.setProductName(proName);
        mDetail.setPluCode(pluCode);
        mDetail.setUnit(unitType);
        mDetail.setParentsItemName(partentName);
        for (int i = 0; i < unitDataList.size(); i++) {
            if (unitType.equals(unitDataList.get(i))) {
                unitSpinner.setSelection(i, true);
                break;
            }
        }
    }

    private boolean checkParentDir() {
        File dirFile = new File(FileHelp.STOCK_DIR);
        boolean mkdirs = true;
        if (!dirFile.exists()) {
            mkdirs = dirFile.mkdirs();
        }
        return mkdirs;
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
                if (requestCode == CER_REQUEST) {
                    fis = new FileInputStream(new File(dir + cerImageName));
                    bitmap = BitmapFactory.decodeStream(fis);
                    mCerImage.setImageBitmap(bitmap);
                    mDetail.setCertificateImg(FileHelp.encodePurchaseImg(cerImageName));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        quantityEt.removeTextChangedListener(mTextWatcher);
        numberEt.removeTextChangedListener(mTextWatcher);
        unitPriceEt.removeTextChangedListener(mTextWatcher);
        EventBus.getDefault().unregister(this);
    }
}
