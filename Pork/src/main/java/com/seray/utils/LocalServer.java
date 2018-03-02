package com.seray.utils;

import android.text.TextUtils;

import com.seray.entity.Library;
import com.seray.entity.MonitorLibraryMessage;
import com.seray.entity.MonitorProdctsMessage;
import com.seray.entity.MonitorSupplierMsg;
import com.seray.entity.Products;
import com.seray.entity.ProductsCategory;
import com.seray.entity.Supplier;
import com.seray.pork.dao.LibraryManager;
import com.seray.pork.dao.ProductsCategoryManager;
import com.seray.pork.dao.ProductsManager;
import com.seray.pork.dao.SupplierManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by pc on 2017/11/3.
 * 向外提供接口
 */

public class LocalServer extends NanoHTTPD {
    LibraryManager libraryManager = LibraryManager.getInstance();
    SupplierManager supplierManager = SupplierManager.getInstance();
    ProductsCategoryManager productsCategoryManager = ProductsCategoryManager.getInstance();
    ProductsManager productsManager = ProductsManager.getInstance();

    private Map<String, String> fields;

    public LocalServer() {
        super(8082);
    }

    public Response serve(IHTTPSession session) {
        try {
            if (Method.POST.equals(session.getMethod())) {
                String uri = session.getUri();
                fields = session.getParms();
                session.parseBody(fields);
                String result = "";
                switch (uri) {
                    case "/SendGoodsName":
                        receiveGoodsName();
                        break;
                    case "/SendSupplier":
                        receiveSendSupplier();
                        break;
                    case "/SendLibname":
                        receiveSendLibname();
                        break;
                    default:
                        return new Response("0,没有匹配的方法;");
                }
                return new Response("1,OK;" + result);
            } else {
                return new Response("0,no_post;");
            }
        } catch (Exception e) {
            return new Response("0," + "客户端错误：" + e.getMessage() + ";");
        }
    }

    private void receiveSendSupplier() throws JSONException {
        String supplierList = fields.get("supplierList");
        LogUtil.e("localServer", supplierList);
        if (!TextUtils.isEmpty(supplierList)) {
            List<Supplier> list = getSupplierHttp(supplierList);
            supplierManager.deleteAll();
            supplierManager.insertMultSupplier(list);
            EventBus.getDefault().post(new MonitorSupplierMsg(list)); //to supplierSelectActivity
        }
    }

    private void receiveSendLibname() {
        String alibraryList = fields.get("alibraryList");
        LogUtil.e("alibraryList", alibraryList.trim());
        Library library = new Library("", alibraryList.trim(),0, "");
        libraryManager.deleteAll();
        libraryManager.insertLibrary(library);
        EventBus.getDefault().post(new MonitorLibraryMessage(library));//to separate,sort,temporary,excessStock,FinishProduct,FrozenLibrary
    }
//    private void receiveSendLibname() throws JSONException {
//        String alibraryList = fields.get("alibraryList");
//        LogUtil.e("alibraryList",alibraryList);
//        if (!TextUtils.isEmpty(alibraryList)) {
//            List<Library> list = getLibraryHttp(alibraryList);
//            libraryManager.deleteAll();
//            libraryManager.insertMultLibrary(list);
//            EventBus.getDefault().post(new MonitorLibraryMessage(list));//to separate,sort,temporary
//        }
    //  }

    private void receiveGoodsName() throws JSONException {
        String itemList = fields.get("itemList");
        LogUtil.e("localServer", itemList);
        if (!TextUtils.isEmpty(itemList)) {
            List<ProductsCategory> list = getProductHttp(itemList);
            productsCategoryManager.deleteAll();
            productsManager.deleteAll();
            productsCategoryManager.insertMultProductsCategory(list);
            for (int i = 0; i < list.size(); i++) {
                List<Products> productsList = list.get(i).getProductsList();
                productsManager.insertMultProducts(productsList);
            }
            EventBus.getDefault().post(new MonitorProdctsMessage(list));// to separateActivity
        }
    }

    private List<ProductsCategory> getProductHttp(String v) throws JSONException {
        JSONArray jsonArray = new JSONArray(v);
        List<ProductsCategory> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject result = jsonArray.getJSONObject(i);
            String categoryId = result.getString("id");
            String categoryName = result.getString("itemname");
            JSONArray child = result.getJSONArray("child");
            List<Products> productsList = new ArrayList<>();
            for (int j = 0; j < child.length(); j++) {
                JSONObject jsonObject = child.getJSONObject(j);
                String productId = jsonObject.getString("ProductId");
                String productName = jsonObject.getString("ItemName");
                String pluCode = jsonObject.getString("PLU");
                String createdAt = jsonObject.getString("CreatedAt");
                int statusCode = jsonObject.getInt("State");
                String remark = jsonObject.getString("Remarks");
                String parentId = jsonObject.getString("ParentId");
                float unitPrice = (float) jsonObject.getDouble("UnitPrice");
                int num = jsonObject.getInt("MeasurementMethod");
                String unit="KG";
                switch (num) {
                    case 1:
                        unit = "KG";
                        break;
                    case 2:
                        unit = "袋";
                        break;
                    case 3:
                        unit = "箱";
                        break;
                }
                Products products = new Products(productId, productName, pluCode, createdAt, statusCode,
                        remark, parentId, unitPrice, unit);
                productsList.add(products);
            }
            ProductsCategory productsCy = new ProductsCategory(categoryId, categoryName);
            productsCy.setProductsList(productsList);
            list.add(productsCy);
        }
        LogUtil.e("list", list.toString());
        return list;
    }

    private List<Supplier> getSupplierHttp(String v) throws JSONException {
        List<Supplier> supplierList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(v);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String id = jsonObject.getString("id");
            String suppliername = jsonObject.getString("suppliername");
            String supplieraddress = jsonObject.getString("supplieraddress");
            String supplierphone = jsonObject.getString("supplierphone");
            Supplier supplier = new Supplier(id, suppliername, supplieraddress, supplierphone);
            supplierList.add(supplier);
        }
        return supplierList;
    }

//    private List<Library> getLibraryHttp(String v) throws JSONException {
//        List<Library> libraryList = new ArrayList<>();
//        JSONArray jsonArray = new JSONArray(v);
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            String id = jsonObject.getString("id");
//            String alibraryname = jsonObject.getString("alibraryname");
//            String state = jsonObject.getString("state");
//            Library Library = new Library(id, alibraryname, state);
//            libraryList.add(Library);
//        }
//        return libraryList;
//    }
}
