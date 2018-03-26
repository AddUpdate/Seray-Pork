package com.seray.http;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.seray.entity.ApiParameter;
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

    public static ApiResult api(int flag, ApiParameter parameter) {
        ApiResult api = new ApiResult();
        switch (flag) {
            case 1:
                api = OutLoulibrary(parameter);//出白条库
                break;
            case 2:
            case 3:
            case 5:
                api = OutInventory(parameter);//出库存
                break;
            case 6:
                api = OutDivision(parameter);//出分割
                break;
            case 7:
                api = outSortingArea(parameter);//出分拣
                break;
            case 8:
                //    api = setUpdateActualWeight();
                break;
        }
        return api;
    }

    /**
     * 卡登录
     */
    public static ApiResult LoginPost(String cardId) {
        Map<String, String> params = new HashMap<>();
        params.put("CardId", cardId);
     //   params.put("PassWord", password);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        String[] sourceDetail = new String[0];
        try {
            Response response = HttpUtils.syncResponsePost(API.LOGIN_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                String result = obj.getString("Result");
                String name = obj.getString("AdminName");

                if (!TextUtils.isEmpty(result)) {
                    String[] sourceStrArray = result.split(",");
                    sourceDetail = sourceStrArray;
                    LogUtil.d("jsonStr", jsonStr);
                }
                jsonStr = name;
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
     * 账号登录
     */
    public static ApiResult LoginNumberPost(String phoneNumber,String password) {
        Map<String, String> params = new HashMap<>();
        params.put("PhoneNumber", phoneNumber);
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
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                String result = obj.getString("Result");
                String name = obj.getString("AdminName");

                if (!TextUtils.isEmpty(result)) {
                    String[] sourceStrArray = result.split(",");
                    sourceDetail = sourceStrArray;
                    LogUtil.d("jsonStr", jsonStr);
                }
                jsonStr = name;
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
    public static ApiResult setUpdateActualWeight(String id,
                                                  String batchNumber,
                                                  int state, String dataHelper) {
        Map<String, String> params = new HashMap<>();
        params.put("Id", id);
        params.put("PurchaseNumber", batchNumber);
        params.put("State", String.valueOf(state));
        //  params.put("GoAlibraryName", goLibrary);
        params.put("DataHelper", dataHelper);
//        params.put("ActualWeight", String.valueOf(weight));
//        params.put("Number", String.valueOf(number));
//        params.put("GolibraryId", golibraryId);
//        params.put("GoAlibraryName",goAlibraryName);
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
    public static ApiResult OutLoulibrary(ApiParameter apiParameter) {
        //   String AlibraryName, String ComelibraryId, String GolibraryId, String weight, String name
        Map<String, String> params = new HashMap<>();
        params.put("DataHelper", apiParameter.getDataHelper());
//        params.put("ComelibraryId", ComelibraryId);
//        params.put("GolibraryId", GolibraryId);
//        params.put("Weight", weight);
//        params.put("ItemName", name);
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
     * 出分割库到其他地方(通用)
     */
    public static ApiResult OutDivision(ApiParameter parameter) {
        Map<String, String> params = new HashMap<>();
        params.put("Division", parameter.getDivision());
//        params.put("AlibraryName", source);
//        params.put("ComelibraryId", comeLibraryId);
        params.put("DataHelper", parameter.getDataHelper());
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
    public static ApiResult outSortingArea(ApiParameter apiParameter) {
        Map<String, String> params = new HashMap<>();
        params.put("DataHelper", apiParameter.getDataHelper());
//     params.put("ItemName", name);
//        params.put("Weight", weight);
//        params.put("Number", number);
//        params.put("AlibraryName", AlibraryName);
//        params.put("ComelibraryId", comelibraryId);
//        params.put("GolibraryId", golibraryId);
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.OUT_SORTING_AREA_URL,
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
    * 出鲜品库 成品1 2 号库  速冻库
    */
    public static ApiResult OutInventory(ApiParameter parameter) {
        Map<String, String> params = new HashMap<>();
        params.put("DataHelper", parameter.getDataHelper());
//      params.put("ComelibraryId", comelibraryId);
//        params.put("AlibraryName", comebraryName);
//        params.put("GolibraryId", golibraryId);
//        params.put("ItemName", name);
//        params.put("Weight", weight);
//        params.put("Number", number);

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
    public static ApiResult getOrderList(int type, String details, int pageIndex) {
        Map<String, String> params = new HashMap<>();
        params.put("Type", String.valueOf(type));
        params.put("Details", details);
        params.put("PageIndex", String.valueOf(pageIndex));
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        String[] sourceDetail = new String[1];
        boolean isSuccess = false;
        List<OrderPick> orderPickList = new ArrayList<>();
        try {
            Response response = HttpUtils.syncResponsePost(API.GET_ORDERS_URL,
                    BATCH_NUMBER_TAG, params);
            if (response.isSuccessful()) {
                code = response.code();
                jsonStr = response.body().string();
                JSONObject obj = new JSONObject(jsonStr);
                isSuccess = obj.getString("ResultCode").equals("1");
                msg = obj.getString("ResultMessage");
                if (type != 2 && type != 4) {
                    sourceDetail[0] = String.valueOf(obj.getInt("TotalPageIndex"));
                    JSONArray message = obj.getJSONArray("Result");
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
                            orderDetail.setNumber(detailObject.getInt("Number"));
                            orderDetail.setActualNumber(detailObject.getInt("ActualNumber"));
                            orderDetail.setOrderDate(detailObject.getString("OrderDate"));
                            orderDetail.setAmount(mNumUtil.getDecimalPrice(detailObject.getDouble("Amount")));
                            if (type == 3 || type == 5) {
                                orderDetail.setIsVehicle(detailObject.getString("isvehicle"));
                                orderDetail.setVehicleNumber(detailObject.getInt("VehicleNumber"));
                                orderDetail.setVehicleWeight(mNumUtil.getDecimalNetWithOutHalfUp(detailObject.getDouble("VehicleWeight")));
                            }
                            orderDetail.setWeight(mNumUtil.getDecimalNetWithOutHalfUp(detailObject.getDouble("Weight")));
                            orderDetail.setActualWeight(mNumUtil.getDecimalNetWithOutHalfUp(detailObject.getDouble("ActualWeight")));
                            orderDetail.setState(detailObject.getString("State").equals("已完成") ? 1 : 2);
                            orderDetailList.add(orderDetail);
                        }
                        orderPick.setDetailList(orderDetailList);

                        orderPickList.add(orderPick);
                    }
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
            api.sourceDetail = sourceDetail;
            api.orderPickList = orderPickList;
        }
        return api;
    }

    /*
     * 自己订单配货实际数量
     */
    public static ApiResult updatetOrderActual(String id, String state, String dataHelper) {
        Map<String, String> params = new HashMap<>();
        params.put("Id", id);
        params.put("State", state);
        params.put("DataHelper", dataHelper);
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

    /*
      * 订单上车
      */
    public static ApiResult updatetOrderVehicle(String orderNumber, String details,String name,int state) {
        Map<String, String> params = new HashMap<>();
        params.put("OrderNumber", orderNumber);
        params.put("Details", details);
        params.put("RealName",name);
        params.put("State",String.valueOf(state));
        ApiResult api = new ApiResult();
        int code = -99;
        String msg = null;
        String jsonStr = null;
        boolean isSuccess = false;
        try {
            Response response = HttpUtils.syncResponsePost(API.UPDATET_ORDER_VEHICLE,
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
