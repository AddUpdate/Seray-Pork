package com.seray.stock;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seray.entity.PurchaseDetail;
import com.seray.entity.PurchaseOrder;
import com.seray.entity.PurchaseSubtotal;
import com.seray.entity.SaoQRCodeMsg;
import com.seray.pork.BaseActivity;
import com.seray.pork.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class StockActivity extends BaseActivity implements BaseTwoFragment.ParameterInterface {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<BaseTwoFragment> mFragmentList;

    private MyPageChangeListener mPageChangeListener = null;

    private boolean isHaveStock = false;

    private PurchaseSubtotal subtotal;

    private List<PurchaseDetail> detailList;

    private String[] titles = {"供应商信息", "采购商品信息", "采购录入确认"};
    private int[] imageResId = {R.drawable.tab_start_sel, R.drawable.tab_mid_unsel, R.drawable.tab_end_unsel};

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        initView();
        initData();
        initListener();
        TabViewAdapter adapter = new TabViewAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(adapter.getTabView(i));
            }
        }
        TabLayout.Tab tabAt = mTabLayout.getTabAt(0);
        if (tabAt != null)
            tabAt.select();
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mMisc.beep();
                View customView = tab.getCustomView();
                int position = tab.getPosition();
                if (customView != null) {
                    ImageView tabIv = (ImageView) customView.findViewById(R.id.stock_tab_iv);
                    switch (position) {
                        case 0:
                            tabIv.setImageResource(R.drawable.tab_start_sel);
                            break;
                        case 1:
                            tabIv.setImageResource(R.drawable.tab_mid_sel);
                            break;
                        case 2:
                            tabIv.setImageResource(R.drawable.tab_end_sel);
                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                int position = tab.getPosition();
                if (customView != null) {
                    ImageView tabIv = (ImageView) customView.findViewById(R.id.stock_tab_iv);
                    switch (position) {
                        case 0:
                            tabIv.setImageResource(R.drawable.tab_start_unsel);
                            break;
                        case 1:
                            tabIv.setImageResource(R.drawable.tab_mid_unsel);
                            break;
                        case 2:
                            tabIv.setImageResource(R.drawable.tab_end_unsel);
                            break;
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.stock_tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.stock_viewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    private void initData() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new EntryFragmentOne());
        mFragmentList.add(new EntryFragmentTwo());
        mFragmentList.add(new EntryFragmentThree());
    }

    private void initListener() {
        mPageChangeListener = new MyPageChangeListener();
        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.isHaveStock)
            EventBus.getDefault().post(new SaoQRCodeMsg(true, "进货更新"));
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(mPageChangeListener);
            mViewPager = null;
            mTabLayout = null;
        }
        imageResId = null;
        titles = null;
    }

    @Override
    public void pushSubtotal(@NonNull PurchaseSubtotal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public void pushDetail(@NonNull PurchaseDetail detail) {
        if (this.detailList == null)
            this.detailList = new ArrayList<>();
        this.detailList.add(detail);
    }

    @Override
    public void restartStock(int beginIndex) {
        for (int i = beginIndex; i < mFragmentList.size(); i++) {
            BaseTwoFragment fragment = mFragmentList.get(i);
            if (fragment != null)
                fragment.clearViewContent();
        }
        if (beginIndex == 0 ){
            if (detailList != null ) {
                subtotal = null;
                detailList.clear();
            }
        }
        mViewPager.setCurrentItem(beginIndex);
    }


    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            BaseTwoFragment fragment = mFragmentList.get(position);
            getSupportFragmentManager().beginTransaction().show(fragment).commit();
            if (position == mFragmentList.size() - 1) {
                for (int i = 0; i < mFragmentList.size(); i++) {
                    mFragmentList.get(i).pushParameter();
                }
                if (subtotal != null) {
                    if (detailList != null && !detailList.isEmpty()) {
                        PurchaseOrder order = new PurchaseOrder(subtotal, detailList);
                        EventBus.getDefault().post(order);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == 2) { // TODO: 2017/11/9    state == 2
                BaseTwoFragment fragment = mFragmentList.get(1);
                fragment.clearEditFocus();
            }
            if (state == 0){
                mFragmentList.get(0).clearEditFocus();
            }
        }
    }

    private class TabViewAdapter extends FragmentPagerAdapter {

        FragmentManager mFragmentManager;

        TabViewAdapter(FragmentManager fm) {
            super(fm);
            this.mFragmentManager = fm;
        }

        View getTabView(int position) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout
                    .stock_tab_layout, null);
            TextView tv = (TextView) view.findViewById(R.id.stock_tab_tv);
            tv.setText(titles[position]);
            ImageView img = (ImageView) view.findViewById(R.id.stock_tab_iv);
            img.setImageResource(imageResId[position]);
            return view;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            BaseTwoFragment fragment = mFragmentList.get(position);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.hide(fragment);
            fragmentTransaction.commit();
        }

        @Override
        public int getCount() {
            return mFragmentList == null ? 0 : mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
