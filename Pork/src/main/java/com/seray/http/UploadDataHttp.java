package com.seray.http;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.seray.entity.ApiResult;
import com.seray.entity.OrderDetail;
import com.seray.entity.OrderPick;
import com.seray.entity.PurchaseDetail;
import com.seray.entity.PurchaseSearch;
import com.seray.entity.PurchaseSubtotal;
import com.seray.pork.dao.ConfigManager;
import com.seray.utils.HttpUtils;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UploadDataHttp {

    /*
     *   返回值  1成功  2失败
     */
    private static final String BATCH_NUMBER_TAG = "UploadBatchNumberPostTag";

    /**
     * 登录
     */

    public static ApiResult LoginPost(String phone, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("PhoneNumber", phone);
        params.put("PassWord", password);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        String[] sourceDetail = new String[0];
        try {
            Response response = HttpUtils.syncResponsePost(API.LOGIN_URL,
                    BATCH_NUMBER_TAG, params);
            LogUtil.d("loginApi",API.LOGIN_URL);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                String result = obj.getString("Result");
                if (!TextUtils.isEmpty(result)) {
                    String[] sourceStrArray = result.split(",");
                    sourceDetail = sourceStrArray;
                    LogUtil.d("jsonStr", jsonStr);
                }
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
            api.sourceDetail = sourceDetail;
        }
        return api;
    }

    /**
     * 提交采购单
     */

    public static ApiResult uploadDataPost(String purchase) {
        Map<String, String> params = new HashMap<>();
        params.put("Purchase", purchase);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        String[] sourceDetail = new String[0];
        try {
            Response response = HttpUtils.syncResponsePost(API.SET_UPLOAD_BATCH_NUMBER_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");

                String result = obj.getString("Result");
                if (!TextUtils.isEmpty(result)) {
                    String[] sourceStrArray = result.split(",");
                    for (int i = 0; i < sourceStrArray.length; i++) {
                        LogUtil.d("sourceStrArray", sourceStrArray[i]);
                    }
                    sourceDetail = sourceStrArray;

                    LogUtil.d("jsonStr", jsonStr);
                }
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
            api.sourceDetail = sourceDetail;
        }
        return api;
    }

    /*
        返回未入库采购单
    */
    public static ApiResult getBatchNoListPost() {
        Map<String, String> params = new HashMap<>();
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        List<PurchaseSubtotal> subtotalList = new ArrayList<>();
        try {
            Response response = HttpUtils.syncResponsePost(API.GET_BATCH_NO_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                JSONArray message = obj.getJSONArray("Result");
                for (int i = 0; i < message.length(); i++) {
                    PurchaseSubtotal subtotal = new PurchaseSubtotal();
                    JSONObject jsonObject = message.getJSONObject(i);
                    subtotal.setBatchNumber(jsonObject.getString("PurchaseNumber"));
                    subtotal.setSupplier(jsonObject.getString("SupplierName"));
                    subtotal.setRemark(jsonObject.getString("State"));
                    subtotalList.add(subtotal);
                }
                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败" + API.GET_BATCH_NO_URL);
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败" + API.GET_BATCH_NO_URL);
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
            api.SubtotalList = subtotalList;
        }
        return api;
    }

    /*
        返回地磅吊秤采购单内明细
     */
    public static ApiResult getPurchaseDetail(String number, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("PurchaseNumber", number);
        params.put("ItemName", name);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        List<PurchaseDetail> detailList = new ArrayList<>();
        try {
            Response response = HttpUtils.syncResponsePost(API.GET_PURCHASE_DETAIL_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                JSONArray message = obj.getJSONArray("Result");
                for (int i = 0; i < message.length(); i++) {
                    PurchaseDetail detail = new PurchaseDetail();
                    JSONObject jsonObject = message.getJSONObject(i);
                    detail.setProductId(jsonObject.getString("PurchaseDetailId"));
                    detail.setProductName(jsonObject.getString("ItemName"));
                    detail.setQuantity(new BigDecimal(jsonObject.getString("Weight")));
                    detail.setNumber(Integer.valueOf(jsonObject.getString("Number")));
                    detail.setActualNumber(Integer.valueOf(jsonObject.getString("ActualNumber")));
                    detail.setUnit(jsonObject.getString("WeightCompany"));
                    detail.setState(jsonObject.getInt("State"));
                    detail.setActualWeight(new BigDecimal(jsonObject.getString("ActualWeight")));
                    detailList.add(detail);
                }
                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
            api.DetailList = detailList;
        }
        return api;
    }

    /*
       实际重量上传
     */
    public static ApiResult setUpdateActualWeight(String id, float weight,
                                                  int number, String batchNumber,
                                                  int state) {
        Map<String, String> params = new HashMap<>();
        params.put("Id", id);
        params.put("ActualWeight", String.valueOf(weight));
        params.put("Number", String.valueOf(number));
        params.put("PurchaseNumber", batchNumber);
        params.put("State", String.valueOf(state));
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.SET_UPDATE_ACTUAL_WEIGHT_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                LogUtil.d("jsonStrWeight", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }

    /*
     * 进白条库
     */
    public static ApiResult getSaveLoulibrary(String Loulibrary, String ComelibraryId, String GolibraryId) {
        Map<String, String> params = new HashMap<>();
        params.put("Loulibrary", Loulibrary);
        params.put("ComelibraryId", ComelibraryId);
        params.put("GolibraryId", GolibraryId);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.SET_SAVE_LOULIBRARY_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");

                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }

    /*
     * 出白条库 到分割
     */
    public static ApiResult getOutLoulibrary(String AlibraryName, String ComelibraryId, String GolibraryId, String weight, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("AlibraryName", AlibraryName);
        params.put("ComelibraryId", ComelibraryId);
        params.put("GolibraryId", GolibraryId);
        params.put("Weight", weight);
        params.put("ItemName", name);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.SET_OUT_LOULIBRARY_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");

                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }

    /*
         * 出白条库 到鲜品
         */
    public static ApiResult getTakeLoulibrary(String ComelibraryId, String weight, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("ComelibraryId", ComelibraryId);
        params.put("Weight", weight);
        params.put("ItemName", name);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.SET_TAKE_LOULIBRARY_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }

    /*
    * 进分割库
    */
    public static ApiResult getSaveDivision(String Division, String ComelibraryId, String GolibraryId) {
        Map<String, String> params = new HashMap<>();
        params.put("Division", Division);
        params.put("ComelibraryId", ComelibraryId);
        params.put("GolibraryId", GolibraryId);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.SET_SAVE_DIVISION_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }

    /*
     * 出分割库到分拣
     */
    public static ApiResult getOutDivision(String Division, String source, String comeLibraryId, String goLibraryId) {
        Map<String, String> params = new HashMap<>();
        params.put("Division", Division);
        params.put("AlibraryName", source);
        params.put("ComelibraryId", comeLibraryId);
        params.put("GolibraryId", goLibraryId);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.SET_OUT_DIVISION_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");

                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }

    /*
     * 出分割库到鲜品
     */
    public static ApiResult getTakeDivision(String comeLibraryId, String name, String weight) {
        Map<String, String> params = new HashMap<>();
        params.put("ComelibraryId", comeLibraryId);
        params.put("ItemName", name);
        params.put("Weight", weight);

        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.SET_TAKE_DIVISION_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");

                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }

    /*
     *   入分拣
     */
    public static ApiResult getSaveSortingArea(String sortingArea, String comelibraryId, String golibraryId) {

        Map<String, String> params = new HashMap<>();
        params.put("SortingArea", sortingArea);
        params.put("ComelibraryId", comelibraryId);
        params.put("GolibraryId", golibraryId);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.SET_SAVE_SORTING_AREA_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");

                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }

    /*
     * 出分拣
     */
    public static ApiResult getTakeSortingArea(String name, String weight, String number, String AlibraryName,
                                               String comelibraryId, String golibraryId) {
        Map<String, String> params = new HashMap<>();
        params.put("ItemName", name);
        params.put("Weight", weight);
        params.put("Number", number);
        params.put("AlibraryName", AlibraryName);
        params.put("ComelibraryId", comelibraryId);
        params.put("GolibraryId", golibraryId);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.SET_TAKE_SORTING_AREA_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                jsonStr = obj.getString("Result");
                LogUtil.d("jsonStrSort", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }

    /*
     *入速冻库 成品1 2 号库
     */
    public static ApiResult getSaveFreeze(String barCode, String comelibraryId, String alibraryName, String golibraryId, String goAlibraryName, String picture) {
        Map<String, String> params = new HashMap<>();
        params.put("BarCode", barCode);
        params.put("GolibraryId", golibraryId);
        params.put("ComelibraryId", comelibraryId);
        params.put("GoAlibraryName", goAlibraryName);
        params.put("AlibraryName", alibraryName);
        params.put("Picture", picture);
        LogUtil.d("picture-----", picture);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.SET_SAVE_INVENTORY_BAR_CODE_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }

    /*
       *获取二维码内信息
       */
    public static ApiResult getBarCodeContent(String barCode) {
        Map<String, String> params = new HashMap<>();
        params.put("BarCode", barCode);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.GET_BAR_CODE_CONTENT_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");

                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }

    /*
      * 入鲜品库
      */
    public static ApiResult getSaveInventory(String inventory, String comelibraryId, String golibraryId, String goAlibraryName) {
        Map<String, String> params = new HashMap<>();
        params.put("Inventory", inventory);
        params.put("ComelibraryId", comelibraryId);
        params.put("GolibraryId", golibraryId);
        params.put("GoAlibraryName", goAlibraryName);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.SET_SAVE_INVENTORY_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");

                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }

    /*
    * 出鲜品库
    */
    public static ApiResult getOutInventory(String comelibraryId, String comebraryName, String golibraryId, String name, String weight,String number) {
        Map<String, String> params = new HashMap<>();
        params.put("ComelibraryId", comelibraryId);
        params.put("AlibraryName", comebraryName);
        params.put("GolibraryId", golibraryId);
        params.put("ItemName", name);
        params.put("Weight", weight);
        params.put("Number", number);

        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.SET_OUT_INVENTORY_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }

    /*
      * 采购单查询
      */
    public static ApiResult getbatchNumberSeach(String supplierName, String itemName, String startDate, String endDate) {
        Map<String, String> params = new HashMap<>();
        params.put("SupplierName", supplierName);
        params.put("ItemName", itemName);
        params.put("StartDate", startDate);
        params.put("EndDate", endDate);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        List<PurchaseSearch> searchList = new ArrayList<>();
        try {
            Response response = HttpUtils.syncResponsePost(API.GET_BATCH_NUMBER_SEACH_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                JSONArray message = obj.getJSONArray("Result");
                for (int i = 0; i < message.length(); i++) {
                    JSONObject object = message.getJSONObject(i);

                    String purchaseDate = object.getString("PurchaseDate");
                    String purchaseNumber = object.getString("PurchaseNumber");
                    String name = object.getString("SupplierName");
                    String productName = object.getString("ItemName");
                    String weight;
                    String actualWeight;
                    if (object.getString("WeightCompany").equals("KG")) {
                        weight = object.getString("Weight") + object.getString("WeightCompany");
                        actualWeight = object.getString("ActualWeight") + object.getString("WeightCompany");
                    } else {
                        weight = object.getString("Number") + object.getString("WeightCompany");
                        actualWeight = object.getString("ActualNumber") + object.getString("WeightCompany");
                    }
                    String state = object.getString("State").equals("1") ? "是" : "否";
                    PurchaseSearch search = new PurchaseSearch(purchaseDate, name, purchaseNumber, productName, weight, actualWeight, state);
                    searchList.add(search);
                }
                LogUtil.d("jsonStrSeach", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
            api.SearchList = searchList;
        }
        return api;
    }

    /*
          获取订单列表
       */
    public static ApiResult getOrderList() {
        Map<String, String> params = new HashMap<>();
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        SparseArray<List<OrderPick>> mSparseArray = new SparseArray<>();
        List<OrderPick> orderPickList = new ArrayList<>();
        List<OrderPick> orderPickList2 = new ArrayList<>();
        List<OrderPick> orderPickList3 = new ArrayList<>();
        try {
            Response response = HttpUtils.syncResponsePost(API.GET_ORDERS_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                JSONArray message = obj.getJSONArray("Result");
                //    List<OrderPick> orderPickList = new ArrayList<>();
                NumFormatUtil mNumUtil;
                mNumUtil = NumFormatUtil.getInstance();
                for (int i = 0; i < message.length(); i++) {
                    OrderPick orderPick = new OrderPick();
                    JSONObject jsonObject = message.getJSONObject(i);
                    orderPick.setState(jsonObject.getString("State"));
                    orderPick.setName(jsonObject.getString("CustomerName"));
                    orderPick.setOrderId(jsonObject.getString("OrderId"));
                    orderPick.setOrderDate(jsonObject.getString("OrderDate"));
                    orderPick.setTel(jsonObject.getString("UserTelephone"));
                    orderPick.setOrderNumber(jsonObject.getString("OrderNumber"));
                    orderPick.setTotalNumber(jsonObject.getInt("TotalNumber"));
                    orderPick.setPaymentMethod(jsonObject.getString("PaymentMethod"));
                    orderPick.setAmount(mNumUtil.getDecimalPrice(jsonObject.getDouble("TotalAmount")));
                    orderPick.setRemark(jsonObject.getString("Remarks"));

                    JSONArray orderdetail = jsonObject.getJSONArray("orderdetail");

                    List<OrderDetail> orderDetailList = new ArrayList<>();
                    for (int j = 0; j < orderdetail.length(); j++) {
                        OrderDetail orderDetail = new OrderDetail();
                        JSONObject detailObject = orderdetail.getJSONObject(j);
                        orderDetail.setOrderNumber(detailObject.getString("OrderNumber"));
                        orderDetail.setOrderDetailId(detailObject.getString("OrderDetailId"));
                        orderDetail.setProductName(detailObject.getString("CommodityName"));
                        orderDetail.setAlibraryName(detailObject.getString("AlibraryName"));
                        orderDetail.setNumber(detailObject.getInt("Number"));
                        orderDetail.setActualNumber(detailObject.getInt("ActualNumber"));
                        orderDetail.setOrderDate(detailObject.getString("OrderDate"));
                        orderDetail.setAmount(mNumUtil.getDecimalPrice(detailObject.getDouble("Amount")));
                        orderDetail.setBarCode(detailObject.getString("BarCode"));
                        orderDetail.setWeight(mNumUtil.getDecimalNetWithOutHalfUp(detailObject.getDouble("Weight")));
                        orderDetail.setActualWeight(mNumUtil.getDecimalNetWithOutHalfUp(detailObject.getDouble("ActualWeight")));
                        orderDetail.setState(detailObject.getString("State").equals("已完成") ? 1 : 2);
                        orderDetailList.add(orderDetail);
                    }
                    orderPick.setDetailList(orderDetailList);
                    if (orderPick.getState().equals("配送中") || orderPick.getState().equals("已完成")) {
                        orderPickList3.add(orderPick);
                    } else {
                        orderPickList.add(orderPick);
                    }
                }
                OrderPick orderPick2 = new OrderPick();
                orderPickList2.add(orderPick2);
                mSparseArray.put(1, orderPickList);
                mSparseArray.put(2, orderPickList2);
                mSparseArray.put(3, orderPickList3);
                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
            api.SparseArray = mSparseArray;
        }
        return api;
    }

    /*
   * 自己订单配货实际数量
   */
    public static ApiResult updatetOrderActual(String id, String actualWeight, String actualNumber, String state) {
        Map<String, String> params = new HashMap<>();
        params.put("Id", id);
        params.put("ActualWeight", actualWeight);
        params.put("ActualNumber", actualNumber);
        params.put("State", state);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.UPDATET_ORDER_ACTUAL_WEIGHT,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                LogUtil.d("jsonStr", jsonStr);
            } else {
                code = response.code();
                msg = "访问服务器失败";
                LogUtil.d("message", "访问服务器失败");
            }
        } catch (JSONException e) {
            msg = "解析错误";
            LogUtil.d("message", "解析错误");
        } catch (IOException e) {
            msg = "访问服务器失败";
            LogUtil.d("message", "访问服务器失败");
        } finally {
            api.Result = isSuccess;
            api.ResultMessage = msg;
            api.ResultJsonStr = jsonStr;
            api.ResultCode = code;
        }
        return api;
    }


}
