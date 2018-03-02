package com.seray.pork;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.seray.entity.OrderSortCodeMessage;
import com.seray.frag.CompleteCarFrag;
import com.seray.frag.OrderCustomerFrag;
import com.seray.frag.OrderFinishFrag;
import com.seray.frag.OrderProductsFrag;
import com.seray.frag.ProductsSortFrag;

import org.greenrobot.eventbus.EventBus;

public class OrderActivity extends BaseActivity {
    RadioGroup radioGroup;
    RadioButton rbCustomer, rbProducts, rbFinish, rbProductsSort, rbCompleteCar, lastSelected;
    private static final String TAG_ORDER_CUSTOMER = "customer";
    private static final String TAG_ORDER_PRODUCTS = "products";
    private static final String TAG_ORDER_FINISH = "finish";
    private static final String TAG_PRODUCTS_SORT = "productsSort";
    private static final String TAG_COMPLETE_CAR = "completeCar";
    private Fragment customerFrag, productsFrag, finishFrag, productsSortFrag, completeCarFrag, currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        init();
    }

    private void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radio_gr);
        rbCustomer = (RadioButton) findViewById(R.id.order_wait_customer_rb);
        rbProducts = (RadioButton) findViewById(R.id.order_wait_products_rb);
        rbFinish = (RadioButton) findViewById(R.id.order_finish_rb);
        rbProductsSort = (RadioButton) findViewById(R.id.order_products_sort_rb);
        rbCompleteCar = (RadioButton) findViewById(R.id.order_complete_car_rb);
        lastSelected = rbCustomer;
    }

    public void init() {
        customerFrag = new OrderCustomerFrag();
        productsFrag = new OrderProductsFrag();
        productsSortFrag = new ProductsSortFrag();
        finishFrag = new OrderFinishFrag();
        completeCarFrag = new CompleteCarFrag();
        currentFragment = customerFrag;
        initFrag();
        rbCustomer.setOnClickListener(this);
        rbProducts.setOnClickListener(this);
        rbFinish.setOnClickListener(this);
        rbProductsSort.setOnClickListener(this);
        rbCompleteCar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.order_wait_customer_rb:
                if (rbCustomer.isChecked()) {
                    setFragment(customerFrag, TAG_ORDER_CUSTOMER);
                }
                changedSelectedView(rbCustomer);
                break;
            case R.id.order_wait_products_rb:
                setFragment(productsFrag, TAG_ORDER_PRODUCTS);
                changedSelectedView(rbProducts);
                break;
            case R.id.order_products_sort_rb:
                setFragment(productsSortFrag, TAG_PRODUCTS_SORT);
                changedSelectedView(rbProductsSort);
                break;
            case R.id.order_finish_rb:
                setFragment(finishFrag, TAG_ORDER_FINISH);
                changedSelectedView(rbFinish);
                break;
            case R.id.order_complete_car_rb:
                setFragment(completeCarFrag, TAG_COMPLETE_CAR);
                changedSelectedView(rbCompleteCar);
                break;
        }
    }

    private void changedSelectedView(View v) {
        if (lastSelected != null) {
            lastSelected.setBackground(getResources().getDrawable(R.drawable.shapeone));
        }
        lastSelected = (RadioButton) v;
        lastSelected.setBackground(getResources().getDrawable(R.drawable.radio_selected));
    }

    public void initFrag() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.mian_fram, customerFrag, TAG_ORDER_CUSTOMER).commitAllowingStateLoss();
    }

    public void setFragment(Fragment fragment, String tag) {
        if (fragment == currentFragment) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded()) {
            fragmentTransaction.hide(currentFragment).add(R.id.mian_fram, fragment, tag).commitAllowingStateLoss();
        } else {
            fragmentTransaction.hide(currentFragment).show(fragment).commitAllowingStateLoss();
        }
        currentFragment = fragment;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int code = event.getKeyCode();
        if (code == KeyEvent.KEYCODE_BACK || code == KeyEvent.KEYCODE_F3) {
            return super.dispatchKeyEvent(event);
        }
        return true;
    }
}