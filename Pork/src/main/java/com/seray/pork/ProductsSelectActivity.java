package com.seray.pork;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.seray.adapter.CategoryAdapter;
import com.seray.adapter.ProductsAdapter;
import com.seray.entity.Products;
import com.seray.entity.ProductsCategory;
import com.seray.entity.PurchaseDetail;
import com.seray.pork.dao.ProductsCategoryManager;
import com.seray.pork.dao.ProductsManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ProductsSelectActivity extends BaseActivity {
    private GridView categoryGridView;
    private GridView productsGridView;
    private HorizontalScrollView hs;

    private List<ProductsCategory> categoryList = new ArrayList<>();

    private List<Products> productList;
    CategoryAdapter separateAdapter;
    ProductsAdapter productsAdapter;
    String categoryName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_select);
        initView();
        initData();
        initAdapter();
        initListener();
    }

    private void initView() {
        categoryGridView = (GridView) findViewById(R.id.gv_products_select);
        categoryGridView.setItemChecked(1, true);
        categoryGridView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 单选模式
        productsGridView = (GridView) findViewById(R.id.lv_products_select);
        hs = (HorizontalScrollView) findViewById(R.id.sv_products);
    }

    private void initData() {
        ProductsCategoryManager pCaManager = ProductsCategoryManager.getInstance();
        ProductsManager productsManager = ProductsManager.getInstance();
        List<ProductsCategory> mCategoryList = pCaManager.queryAllProductsCategory();
        for (int i = 0; i < mCategoryList.size(); i++) {
            List<Products> list = productsManager.queryProductsByQueryBuilder(mCategoryList.get(i).getCategoryId());
            ProductsCategory productsCategory = new ProductsCategory(mCategoryList.get(i).getCategoryId(), mCategoryList.get(i).getCategoryName());
            productsCategory.setProductsList(list);
            categoryList.add(productsCategory);
        }
        initGridVie(categoryList.size());
    }

    private void initAdapter() {
        separateAdapter = new CategoryAdapter(this, categoryList);
        categoryGridView.setAdapter(separateAdapter);
   //     categoryGridView.setSelection(0);
        categoryGridView.setItemChecked(0, true);
        productsAdapter = new ProductsAdapter(this,productList);
        productsGridView.setAdapter(productsAdapter);
        if (categoryList.size() > 0) {
            categoryName = categoryList.get(0).getCategoryName();
            productList = categoryList.get(0).getProductsList();
            productsAdapter.setNewData(productList);
        }
    }

    private void initListener() {

        categoryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                productList = categoryList.get(position).getProductsList();
                categoryName = categoryList.get(position).getCategoryName();
                productsAdapter.setNewData(productList);
            }
        });
        productsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                String name = productList.get(position).getProductName();
                String plu = productList.get(position).getPluCode();
                String unit= productList.get(position).getUnit();
                EventBus.getDefault().post(new PurchaseDetail(name, plu, unit, categoryName)); //to entryFragmentTwo
                finish();
            }
        });
    }

    private void initGridVie(int count) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int columnWidth = dm.widthPixels;
        float density = dm.density;
        int itemWidth = (int) (150 * density);
        LayoutParams params1 = new LayoutParams(
                count * itemWidth + count - 1, LayoutParams.WRAP_CONTENT);
        categoryGridView.setLayoutParams(params1);
        categoryGridView.setColumnWidth(itemWidth);
        categoryGridView.setHorizontalSpacing(1);
        categoryGridView.setStretchMode(GridView.NO_STRETCH);
        categoryGridView.setNumColumns(count);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
