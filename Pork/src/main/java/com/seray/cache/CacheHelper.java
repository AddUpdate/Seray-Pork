package com.seray.cache;

public class CacheHelper {

    /**
     * 四个客户的销售明细缓存
//     */
//    public static List<Detail> Detail_1 = new ArrayList<>();
//    public static List<Detail> Detail_2 = new ArrayList<>();
//    public static List<Detail> Detail_3 = new ArrayList<>();
//    public static List<Detail> Detail_4 = new ArrayList<>();
//
//    /**
//     * 上笔交易明细
//     */
//    public static Detail latestDetail = null;
//
//    /**
//     * 上笔交易明细是否已经插入数据库
//     */
//    public static boolean isSaved = false;
//
//    /**
//     * 设备信息
//     */
//    public static String DeviceCode;
//    public static String ServerBaseUrl;
//    public static boolean isOpenRecord;
//    public static boolean isOpenCamera;
//    public static boolean isNoPluNoSum;
//    public static boolean isChangePrice;
//    public static boolean isHoldPlu;
//    public static boolean isPrintPrice;
//    public static boolean isOpenBackDisplay;
//    public static boolean isHoldPrice;
//    public static boolean isOpenJin;
//    public static boolean isOpenPayByQR;
//    public static boolean isOnlinePayByQR;
//    public static boolean isOpenYunPay;
//    public static boolean isOpenInputTel;
//    public static boolean isOpenPrintQR;
//    public static boolean isOpenBattery;
//
//    /**
//     * 打印信息
//     */
//    public static String UserId;
//    public static String BillHeader;//票据抬头
//    public static String MarketLicenseNo;
//    public static String MarketTel;
//    public static String MarketAddress;
//    public static String AD;
//    public static String BoothId;//商户ID
//    public static String BoothName;//商户名
//    public static String MarketName;//市场补全名称
//    public static String SellerTel;//卖方电话
//    public static String SellerName;//卖方姓名
//    public static String SuperviseUnit;//监管单位
//    public static String SuperviseTel;//监督电话
//    public static String TraceAddress;
//    public static int DATE_ID;
//
//    /**
//     * 二维码支付信息
//     */
//    public static String QRMerchantNumber_YUN;//二维码支付商户号 云支付
//    public static String QRMerchantNumber_SOS;//二维码支付商户号 嗖嗖支付
//    public static String QRMerchantPrivateKey_YUN;//二维码支付秘钥 云天翼付秘钥
//    public static String QRMerchantPrivateKey_SOS;//二维码支付秘钥 嗖嗖支付秘钥
//
//    /**
//     * 菜篮宝/会员卡支付地址
//     */
//    public static String MemberPostUrl;
//
//    /**
//     * 二维码云天翼付被扫接口地址
//     */
//    public static String QRCodeSweptUrl_YUN;
//
//    /**
//     * 二维码云天翼付机构编号
//     */
//    public static String QRCodeStoreCode_YUN;
//
//    /**
//     * 二维码云天翼付终端号
//     */
//    public static String QRCodeTerminalCode_YUN;
//
//    /**
//     * 二维码云天翼付设备序列号
//     */
//    public static String QRCodeDeviceCode;
//
//    /**
//     * 二维码查询支付结果接口地址
//     */
//    public static String QRCodePostUrl_YUN;
//    public static String QRCodePostUrl_SOS;
//
//    /**
//     * 二维码支付生成图接口地址
//     */
//    public static String QRCodeImgUrl_YUN;
//    public static String QRCodeImgUrl_SOS;
//
//    /**
//     * POS机设备信息
//     */
//    public static DeviceInfo PosDevice;
//
//    /**
//     * 缓存交易信息
//     */
//    public static List<OrderInfo> orderCache = new ArrayList<>();
//    /**
//     * 缓存客户
//     */
//    public static CustomerInfo[] customerCache = {null, null, null, null};
//    /**
//     * 累计客户按钮初始化文字
//     */
//    public static int[] basicBtnText = {R.string.fn_1, R.string.fn_2, R.string.fn_3, R.string.fn_4};
//
//    private static ConfigManager conManager = ConfigManager.getInstance();
//
//    public static void cleanLocalCache() {
//        if (Detail_1 != null) {
//            Detail_1.clear();
//            Detail_1 = null;
//        }
//        if (Detail_2 != null) {
//            Detail_2.clear();
//            Detail_2 = null;
//        }
//        if (Detail_3 != null) {
//            Detail_3.clear();
//            Detail_3 = null;
//        }
//        if (Detail_4 != null) {
//            Detail_4.clear();
//            Detail_4 = null;
//        }
//        if (orderCache != null) {
//            orderCache.clear();
//            orderCache = null;
//        }
//        latestDetail = null;
//        PosDevice = null;
//        customerCache = null;
//        basicBtnText = null;
//    }
//
//    /**
//     * 缓存订单详情
//     */
//    public static LocalFileTag addOrderToCache(OrderInfo info) {
//        orderCache.clear();// 清空缓存数据
//        orderCache.add(info);// 插入
//        return FileHelp.writeToReprint(info);// 写入备份文件
//    }
//
//    /**
//     * 从缓存中读取订单
//     */
//    public static OrderInfo getOrderInfoFromCache() {
//        if (!orderCache.isEmpty()) {
//            return orderCache.get(0);
//        }
//        return null;
//    }
//
//    /**
//     * 重置当前缓存交易订单
//     */
//    public static LocalFileTag resetOrderInfoCache() {
//        LocalFileTag tag = FileHelp.readReprintContent();
//        Object obj = tag.getObj();
//        OrderInfo info;
//        if (tag.isSuccess() && obj != null) {
//            if (obj instanceof OrderInfo) {
//                info = (OrderInfo) obj;
//                orderCache.clear();
//                orderCache.add(info);
//            }
//        }
//        return tag;
//    }
//
//    /**
//     * 部署设备和打印信息 二维码支付信息
//     */
//    public static void prepareCacheData() {
//        if (TextUtils.isEmpty(DeviceCode))
//            getDeviceCode();
//        Map<String, String> map = conManager.query();
//        String data_id = map.get("Date_Id");
//        DATE_ID = isAccord(data_id) ? 0 : Integer.parseInt(data_id);
//        preparePrintData(map);
//        prepareConfig(map);
//        prepareQRCodePayData(map);
//        // 称器的唯一码
//        prepareActivateInfo(map);
//    }
//
//    private static String getDeviceCode() {
//        LocalFileTag tag = FileHelp.readDeviceCode();
//        Object obj = tag.getObj();
//        if (tag.isSuccess() && obj != null) {
//            DeviceCode = (String) tag.getObj();
//        } else {
//            DeviceCode = "";
//        }
//        return DeviceCode;
//    }
//
//    /**
//     * 是否满足插入或更新数据库条件
//     */
//    private static boolean isAccord(String value) {
//        return TextUtils.isEmpty(value);
//    }
//
//    /**
//     * 部署打印信息
//     */
//    private static void preparePrintData(Map<String, String> map) {
//        String billHeader = map.get("BillHeader");
//        String boothName = map.get("BoothName");
//        String boothId = map.get("BoothId");
//        String userId = map.get("UserId");
//        String marketName = map.get("MarketName");
//        String licenseNo = map.get("MarketLicenseNo");
//        String tel = map.get("Tel");
//        String address = map.get("Address");
//        String sellerTel = map.get("SellerTel");
//        String sellerName = map.get("SellerName");
//        String superviseUnit = map.get("SuperviseUnit");
//        String superviseTel = map.get("SuperviseTel");
//        String ad = map.get("AD");
//        String traceAddress = map.get("TraceAddress");
//
//        BoothId = isAccord(boothId) ? "" : boothId;// 摊位号
//
//        BoothName = isAccord(boothName) ? "" : boothName;// 摊位名
//
//        SellerTel = isAccord(sellerTel) ? "" : sellerTel;// 卖方电话
//
//        SellerName = isAccord(sellerName) ? "" : sellerName;// 卖方姓名
//
//        UserId = isAccord(userId) ? "" : userId;// 操作员 收银员
//
//        BillHeader = isAccord(billHeader) ? "苏州市食用农产品统一销售凭证" : billHeader;// 票据抬头
//
//        MarketName = isAccord(marketName) ? "苏州市深锐电子科技有限公司" : marketName;// 市场名称
//
//        MarketLicenseNo = isAccord(licenseNo) ? "91320509301934608A" : licenseNo;// 市场营业执照编码
//
//        MarketTel = isAccord(tel) ? "0512-69168123" : tel;// 市场电话
//
//        MarketAddress = isAccord(address) ? "姑苏区虎池路85号旺思楼" : address;// 市场地址
//
//        AD = isAccord(ad) ? "科技服务民生" : ad;// 广告信息
//
//        SuperviseUnit = isAccord(superviseUnit) ? "苏州市食品药品管理局" : superviseUnit;// 监管单位
//
//        SuperviseTel = isAccord(superviseTel) ? "12331" : superviseTel;// 监督电话
//
//        TraceAddress = isAccord(traceAddress) ? "http://sy.gsjmpt.com/?sn=WJCNZK-[SN]" : traceAddress;// 二维码追溯地址
//    }
//
//    /**
//     * 部署设置信息
//     */
//    private static void prepareConfig(Map<String, String> map) {
//
//        String record = map.get("isOpenRecord");
//        String camera = map.get("isOpenCamera");
//        String noPluNoSum = map.get("isNoPluNoSum");
//        String changePrice = map.get("isChangePrice");
//        String holdPlu = map.get("isHoldPlu");
//        String printPrice = map.get("isPrintPrice");
//        String openDisplay = map.get("isOpenBackDisplay");
//        String url = map.get("WebAddress");
//        String holdPrice = map.get("isHoldPrice");
//        String openJin = map.get("isOpenJin");
//        String isOpenQRPay = map.get("isOpenPayByQR");
//        String isQROnline = map.get("isOnlinePayByQR");
//        String isYunPay = map.get("isOpenYunPay");
//        String memberPostUrl = map.get("MemberPostUrl");
//        String isNeedInputTel = map.get("isOpenInputTel");
//        String openPrintQR = map.get("isOpenPrintQR");
//        String openBattery = map.get("isOpenBattery");
//
//        isOpenRecord = record == null || record.equals("1");// 是否开启强制记录// 默认开启
//
//        isOpenCamera = camera == null || camera.equals("1"); // 是否开启照相功能//默认开启
//
//        isNoPluNoSum = noPluNoSum != null && noPluNoSum.equals("1"); // 是否开启无PLU不显示总价//默认关闭
//
//        isHoldPlu = holdPlu == null || holdPlu.equals("1");// 是否开启交易完成保留PLU//默认开启
//
//        isHoldPrice = holdPrice == null || holdPrice.equals("1");// 是否开启交易完成保留单价/默认开启
//
//        isPrintPrice = printPrice == null || printPrice.equals("1");// 是否开启打印单价/默认开启
//
//        isOpenBackDisplay = openDisplay == null || openDisplay.equals("1");// 是否开启后显示/默认开启
//
//        ServerBaseUrl = isAccord(url) ? "" : url;// 更新应用程序服务器地址
//
//        MemberPostUrl = isAccord(memberPostUrl) ? "" : memberPostUrl;//菜篮宝/会员卡支付地址
//
//        isOpenJin = openJin != null && openJin.equals("1");// 是否开启了称重单位(斤)//默认关闭
//
//        isOpenPayByQR = isOpenQRPay == null || isOpenQRPay.equals("1");//是否开启二维码支付//默认开启
//
//        isOnlinePayByQR = isQROnline != null && isQROnline.equals("1");//二维码支付是否是在线模式//默认离线模式
//
//        isOpenYunPay = isYunPay == null || isYunPay.equals("1");//是否启用云天翼付 默认开启
//
//        isChangePrice = changePrice == null || changePrice.equals("1");//是否允许修改单价  默认允许
//
//        isOpenInputTel = isNeedInputTel != null && isNeedInputTel.equals("1");//打印是否需要输入客户电话//默认关闭
//
//        isOpenPrintQR = openPrintQR == null || openPrintQR.equals("1");// 是否开启打印二维码//默认开启
//
//        isOpenBattery = openBattery == null || openBattery.equals("1");// 是否开启打印二维码//默认开启
//
//    }
//
//    /**
//     * 部署二维码支付信息
//     */
//    private static void prepareQRCodePayData(Map<String, String> map) {
//        //云天翼付
//        String merchantNumber_1 = map.get("QRMerchantNumber_YUN");
//        String yunPostUrl = map.get("QRCodePostUrl_YUN");
//        String yunImgUrl = map.get("QRCodeImgUrl_YUN");
//        String privateKey_yun = map.get("QRMerchantPrivateKey_YUN");
//        String sweptUrl_yun = map.get("QRCodeSweptUrl_YUN");
//        String storeCode_yun = map.get("QRCodeStoreCode_YUN");
//        String terminalCodeYun = map.get("QRCodeTerminalCode_YUN");
//        String qrCodeDeviceCode = map.get("QRCodeDeviceCode_YUN");
//
//        //嗖嗖收银
//        String merchantNumber_2 = map.get("QRMerchantNumber_SOS");
//        String privateKey = map.get("QRMerchantPrivateKey_SOS");
//        String sosPostUrl = map.get("QRCodePostUrl_SOS");
//        String sosImgUrl = map.get("QRCodeImgUrl_SOS");
//
//        QRMerchantNumber_YUN = isAccord(merchantNumber_1) ? "" : merchantNumber_1;//二维码商户号（云天翼付）
//
//        QRMerchantPrivateKey_YUN = isAccord(privateKey_yun) ? "" : privateKey_yun;//二维码商户秘钥（云天翼付）
//
//        QRMerchantNumber_SOS = isAccord(merchantNumber_2) ? "" : merchantNumber_2;  //二维码商户号（嗖嗖收银）
//
//        QRMerchantPrivateKey_SOS = isAccord(privateKey) ? "" : privateKey;//二维码商户秘钥（嗖嗖收银）
//
//        QRCodePostUrl_YUN = isAccord(yunPostUrl) ? "" : yunPostUrl;//二维码云天翼付查询接口
//
//        QRCodeStoreCode_YUN = isAccord(storeCode_yun) ? "" : storeCode_yun;//二维码云天翼付机构号
//
//        QRCodeTerminalCode_YUN = isAccord(terminalCodeYun) ? "" : terminalCodeYun;//二维码云天翼付终端号
//
//        QRCodeDeviceCode = isAccord(qrCodeDeviceCode) ? "" : qrCodeDeviceCode;//二维码云天翼付设备序列号
//
//        QRCodeImgUrl_YUN = isAccord(yunImgUrl) ? "" : yunImgUrl;//云天翼付二维码生成地址
//
//        QRCodeSweptUrl_YUN = isAccord(sweptUrl_yun) ? "" : sweptUrl_yun;//云天翼付被扫接口地址
//
//        QRCodePostUrl_SOS = isAccord(sosPostUrl) ? "" : sosPostUrl;//嗖嗖二维码查询接口
//
//        QRCodeImgUrl_SOS = isAccord(sosImgUrl) ? "" : sosImgUrl;//嗖嗖二维码图片地址
//
//    }
//
//    /**
//     * 激活验证
//     */
//    private static void prepareActivateInfo(Map<String, String> map) {
//        String code = map.get("ActivationCode");
//        String day = map.get("NetworkConnectedDate");
//        if (isAccord(code)) {
//            code = UUID.randomUUID().toString();
//            conManager.insert("ActivationCode", code);
//        }
//        if (isAccord(day)) {
//            conManager.insert("NetworkConnectedDate", NumFormatUtil.getReportDate());
//        }
//    }
//
//    /**
//     * 保存订单流水号后三位
//     */
//    public static void saveDataIdToDb() {
//        conManager.insert("Date_Id", String.format(Locale.CHINA, "%03d", DATE_ID));
//    }
//
//    /**
//     * 重新部署打印信息
//     */
//    public static void rePrePrintData() {
//        getDeviceCode();
//        Map<String, String> map = conManager.query();
//        preparePrintData(map);
//    }
//
//    /**
//     * 重新部署设置信息
//     */
//    public static void rePreConfigData() {
//        Map<String, String> map = conManager.query();
//        prepareConfig(map);
//    }
//
//    /**
//     * 重新部署二维码支付信息
//     */
//    public static void rePreQRCodeData() {
//        Map<String, String> map = conManager.query();
//        prepareQRCodePayData(map);
//    }
//
//    /**
//     * 将下发的打印信息插入或更新至本地数据库
//     */
//    public static void addDataToDb(Map<String, String> fields) {
//
//        Object[] keySet = fields.keySet().toArray();
//
//        int size = keySet.length;
//
//        for (int i = size - 1; i >= 0; i--) {
//
//            if (isAccord(fields.get(keySet[i]))) {
//
//                fields.remove(keySet[i]);
//
//            }
//        }
//        conManager.insert(fields);
//    }
}
