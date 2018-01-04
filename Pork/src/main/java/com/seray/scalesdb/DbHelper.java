package com.seray.scalesdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "scales.db";
    private static final int DB_VERSION = 1;
    private static DbHelper helper = null;

    private DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        getWritableDatabase().enableWriteAheadLogging();
        getReadableDatabase().enableWriteAheadLogging();
    }

    public static DbHelper getInstance(Context context) {
        if (helper == null) {
            synchronized (DbHelper.class) {
                if (helper == null) {
                    helper = new DbHelper(context);
                }
            }
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String product = "create table Product(ProductId INTEGER primary key not null," +
                "CategoryId INTEGER not null," +
                "ProductName TEXT(20) not null," +
                "Alias TEXT(100)," +
                "PinYin TEXT(30)," +
                "PluCode TEXT(10)," +
                "UnitPrice DECIMAL(10,2) default 0 not null," +
                "MemberPrice DECIMAL(10,2) default 0 not null," +
                "IsDiscountRate INTEGER NOT NULL default(0)," +
                "IsWeight INTEGER default(1) not null," +
                "OrderNum INTEGER default(1) not null," +
                "Remark TEXT(50)," +
                "Place TEXT(30)," +
                "PurPlace TEXT(30) NOT NULL DEFAULT('')," +
                "StatusCode BOOLEAN," +
                "CreatedAt DATETIME default (datetime(current_timestamp,'localtime')) not null," +
                "LastModifiedAt DATETIME default (datetime(current_timestamp, 'localtime')) not null)";
        String detail = "create table Detail(" +
                "Id INTEGER primary key autoincrement not null," +
                "S_Id INTEGER not null," +
                "OrderNum INTEGER not null," +
                "ProductName TEXT(12) not null," +
                "PluCode TEXT(10) not null," +
                "DealModel INTEGER not null," +
                "Price DECIMAL(10,2) not null," +
                "MemberPrice DECIMAL(10,2) not null," +
                "Number DECIMAL(10,3) not null," +
                "Amount DECIMAL(10,2) not null," +
                "Tare DECIMAL(10,3) not null," +
                "TraceCode TEXT(50) not null," +
                "DealTime DATETIME not null," +
                "ImgSrc TEXT(32) not null," +
                "Remark TEXT(32) not null)";
        String subtotal = "create table Subtotal(" +
                "Id INTEGER primary key autoincrement not null," +
                "SerialNumber TEXT(32) not null DEFAULT('')," +
                "PaymentType INTEGER not null," +
                "Amount DECIMAL(10,2) not null," +
                "PaymentAmount DECIMAL(10,2) not null," +
                "CardNum TEXT(32) not null," +
                "Credential TEXT not null DEFAULT('')," +
                "UserId TEXT(10) not null DEFAULT('')," +
                "DealTime DATETIME not null," +
                "IsDeleted INTEGER NOT NULL," +
                "Remark TEXT(32) not null DEFAULT('')," +
                "CustomerInfo TEXT(16) not null DEFAULT('')," +
                "CustomerTel TEXT(16) NOT NULL DEFAULT(''))";
        String log = "create table Log(" +
                "Id INTEGER primary key autoincrement not null," +
                "Type INTEGER not null," +
                "Details TEXT(255) not null," +
                "CreateAt DATETIME not null," +
                "Remark TEXT(32) not null)";
        String config = "create table Config(" +
                "Id INTEGER primary key autoincrement not null," +
                "Key TEXT(32) not null," +
                "Value TEXT(100) not null)";
        String purchase = "create table Purchase(" +
                "Id Integer primary key autoincrement not null," +
                "ProductName TEXT(12) not null," +
                "PluCode TEXT(10)," +
                "Place TEXT(30), " +
                "PurPlace TEXT(30) NOT NULL DEFAULT('')," +
                "UnitPrice DECIMAL(6,2) not null," +
                "ShopPrice Decimal(6,2) NOT NULL," +
                "Quantity DECIMAL(6,3) not null," +
                "Amount DECIMAL(6,2) not null," +
                "Unit TEXT(5) not null," +
                "BoothId TEXT(36) not null," +
                "StockDate DATETIME not null," +
                "OperationTime DATETIME not null," +
                "IsDeleted INTEGER not null," +
                "IsQualified INTEGER not null," +
                "IsHavePic INTEGER not null," +
                "PurOrderImg TEXT(50) not null," +
                "Certificate TEXT(50) not null," +
                "Remark TEXT(32) not null)";
        String month = "create table Months(MonthId VARCHAR(2) not null,MonthChineseName VARCHAR" +
                "(3) default 1 not null);";
        String customer = "create table Customer(" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "IDCode INTEGER NOT NULL DEFAULT(0)," +
                "Name TEXT(10) NOT NULL DEFAULT('')," +
                "Tel TEXT(15) NOT NULL DEFAULT('')," +
                "PlateNumber TEXT(8) NOT NULL DEFAULT('')," +
                "IDCard TEXT(18) NOT NULL DEFAULT('')," +
                "Market TEXT(32) NOT NULL DEFAULT('')," +
                "UnpaidOrder INTEGER NOT NULL DEFAULT(0)," +
                "PaidOrder INTEGER NOT NULL DEFAULT(0)," +
                "TotalOrder INTEGER NOT NULL DEFAULT(0)," +
                "Remark TEXT(32) NOT NULL DEFAULT(''));";
        String inputPlace = "create table ProductSource(" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "IDCode INTEGER NOT NULL DEFAULT(0)," +
                "Source TEXT(255) NOT NULL DEFAULT('')," +
                "Remark TEXT(32) NOT NULL DEFAULT(''))";
        String eleSubtotal = "create table EleSubtotal(" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "BoothId TEXT(36) NOT NULL DEFAULT('')," +
                "OrderNum TEXT(32) UNIQUE NOT NULL DEFAULT('')," +
                "OrderDate DATETIME NOT NULL," +
                "Address TEXT(255) NOT NULL DEFAULT('')," +
                "Person TEXT(16) NOT NULL DEFAULT('')," +
                "Phone TEXT(16) NOT NULL DEFAULT('')," +
                "StatusCode INTEGER NOT NULL DEFAULT(0)," +
                "StatusCodeName TEXT(32) NOT NULL DEFAULT('')," +
                "PayStatusCode INTEGER NOT NULL DEFAULT(0)," +
                "PayStatusCodeName TEXT(32) NOT NULL DEFAULT('')," +
                "IsSettlement INTEGER NOT NULL DEFAULT(1)," +
                "Score INTEGER NOT NULL DEFAULT(0)," +
                "Discount DECIMAL(6,2) NOT NULL," +
                "TicketMoney DECIMAL(6,2) NOT NULL," +
                "FactPay DECIMAL(10,2) NOT NULL," +
                "Amount DECIMAL(10,2) NOT NULL," +
                "SendType INTEGER NOT NULL DEFAULT(0)," +
                "SendTypeName TEXT(32) NOT NULL DEFAULT('')," +
                "SendTime DATETIME NOT NULL," +
                "SendDate DATETIME NOT NULL," +
                "Message TEXT(255) NOT NULL DEFAULT('')," +
                "Remark TEXT(32) NOT NULL DEFAULT('')" +
                ")";
        String eleDetail = "create table EleDetail(" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "OrderNum TEXT(32) NOT NULL DEFAULT('')," +
                "ProductName TEXT(16) NOT NULL DEFAULT('')," +
                "ProductId TEXT(36) NOT NULL DEFAULT('')," +
                "CategoryId TEXT(36) NOT NULL DEFAULT('')," +
                "Count DECIMAL(6,2) NOT NULL," +
                "FactCount DECIMAL(6,2) NOT NULL," +
                "Discount DOUBLE NOT NULL," +
                "Price DECIMAL(6,2) NOT NULL," +
                "DiscountPrice DECIMAL(6,2) NOT NULL," +
                "UnitType INTEGER NOT NULL DEFAULT(0)," +
                "Unit TEXT(6) NOT NULL DEFAULT('斤')," +
                "Amount DECIMAL(10,2) NOT NULL" +
                ")";
        db.execSQL("CREATE TABLE PurchaseSubtotal(" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "Supplier TEXT(255) NOT NULL DEFAULT('')," +
                "BatchNumber TEXT(255) NOT NULL DEFAULT('')," +
                "Tel TEXT(16) NOT NULL DEFAULT('')," +
                "StockDate DATETIME NOT NULL," +
                "PurOrderImg TEXT(128) NOT NULL DEFAULT('')," +
                "DetailsNumber INTEGER," +
                "Remark TEXT(32) NOT NULL DEFAULT(''))");
        db.execSQL("CREATE TABLE PurchaseDetail(" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "S_Id INTEGER," +
                "OrderNum INTEGER," +
                "ProductName TEXT(32) NOT NULL DEFAULT('')," +
                "PluCode TEXT(32) NOT NULL DEFAULT('')," +
                "Quantity DECIMAL(6,3)," +
                "UnitPrice DECIMAL(4,2)," +
                "Amount DECIMAL(10,2)," +
                "SellQuantity DECIMAL(6,3)," +
                "CertificateImg TEXT(128) NOT NULL DEFAULT('')," +
                "Unit INTEGER DEFAULT(0)," +
                "Remark TEXT(32) NOT NULL DEFAULT(''))");
        db.execSQL(product);
        db.execSQL(detail);
        db.execSQL(subtotal);
        db.execSQL(log);
        db.execSQL(config);
        db.execSQL(purchase);
        db.execSQL(month);
        db.execSQL(customer);
        db.execSQL(inputPlace);
        db.execSQL(eleSubtotal);
        db.execSQL(eleDetail);
        createMonthData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                String config = "create table Config(" +
                        "Id INTEGER primary key autoincrement not null," +
                        "Key TEXT(32) not null," +
                        "Value TEXT(100) not null)";
                db.execSQL(config);
            case 2:
                db.execSQL("drop table Log");
                db.execSQL(
                        "create table Log(" +
                                "Id INTEGER primary key autoincrement not null," +
                                "Type INTEGER not null," +
                                "Details TEXT(255) not null," +
                                "CreateAt DATETIME not null," +
                                "Remark TEXT(32) not null)");
            case 3:
                db.execSQL("create table Purchase(" +
                        "Id Integer primary key autoincrement not null," +
                        "ProductName TEXT not null," +
                        "PluCode TEXT(10)," +
                        "Place TEXT(30)," +
                        "UnitPrice DECIMAL(6,2) not null," +
                        "Quantity DECIMAL (6,3) not null," +
                        "Amount DECIMAL(6,2) not null," +
                        "Unit TEXT(5) not null," +
                        "BoothId TEXT(36) not null," +
                        "StockDate DATETIME not null," +
                        "OperationTime DATETIME not null," +
                        "IsDeleted INTEGER not null," +
                        "IsQualified INTEGER not null," +
                        "IsHavePic INTEGER not null," +
                        "PurOrderImg text(50) not null," +
                        "Certificate text(50) not null," +
                        "Remark TEXT(32) not null)");
                db.execSQL("create table Months(MonthId VARCHAR(2) not null,MonthChineseName " +
                        "VARCHAR(3) default 1 not null)");
                createMonthData(db);

                db.execSQL("Alter table Detail Add Column DealTime DATETIME not null default('');");
                db.execSQL("Update Detail Set DealTime = datetime('now','localtime');");

                db.execSQL("Alter table Subtotal Add Column IsDeleted INTEGER NOT NULL DEFAULT(0)" +
                        ";");
                db.execSQL("Update Subtotal Set IsDeleted = 0");

                db.execSQL("Alter table Detail Add Column MemberPrice DECIMAL(10,2) not null " +
                        "default(0);");
                db.execSQL("Update Detail Set MemberPrice = Price");

                db.execSQL("Alter table Product Add Column MemberPrice Decimal(6,2) default(0);");

                db.execSQL("Update Product Set MemberPrice=UnitPrice;");
            case 4:
                db.execSQL("Alter table Product Add Column IsDiscountRate INTEGER NOT NULL " +
                        "default(0);");
            case 5:
                db.execSQL("Alter table Purchase Add Column ShopPrice Decimal(6,2) NOT NULL " +
                        "DEFAULT(0);");
            case 6:
                db.execSQL("Alter table Product Add Column PurPlace TEXT(30) NOT NULL DEFAULT('')");
                db.execSQL("Alter table Purchase Add Column PurPlace TEXT(30) NOT NULL DEFAULT('')");
            case 7:
                db.execSQL("Alter table Subtotal Add Column CustomerTel TEXT(16) NOT NULL DEFAULT('')");
                db.execSQL("create table Customer(" +
                        "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "IDCode INTEGER NOT NULL DEFAULT(0)," +
                        "Name TEXT(10) NOT NULL DEFAULT('')," +
                        "Tel TEXT(15) NOT NULL DEFAULT('')," +
                        "PlateNumber TEXT(8) NOT NULL DEFAULT('')," +
                        "IDCard TEXT(18) NOT NULL DEFAULT('')," +
                        "Market TEXT(32) NOT NULL DEFAULT('')," +
                        "UnpaidOrder INTEGER NOT NULL DEFAULT(0)," +
                        "PaidOrder INTEGER NOT NULL DEFAULT(0)," +
                        "TotalOrder INTEGER NOT NULL DEFAULT(0)," +
                        "Remark TEXT(32) NOT NULL DEFAULT(''));");
            case 8:
                db.execSQL("create table ProductSource(" +
                        "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "IDCode INTEGER NOT NULL DEFAULT(0)," +
                        "Source TEXT(255) NOT NULL DEFAULT('')," +
                        "Remark TEXT(32) NOT NULL DEFAULT(''))");
            case 9:
                db.execSQL("create table EleSubtotal(" +
                        "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "BoothId TEXT(32) NOT NULL DEFAULT('')," +
                        "OrderNum TEXT(32) UNIQUE NOT NULL DEFAULT('')," +
                        "OrderDate DATETIME NOT NULL," +
                        "Address TEXT(255) NOT NULL DEFAULT('')," +
                        "Person TEXT(16) NOT NULL DEFAULT('')," +
                        "Phone TEXT(16) NOT NULL DEFAULT('')," +
                        "StatusCode INTEGER NOT NULL DEFAULT(0)," +
                        "StatusCodeName TEXT(32) NOT NULL DEFAULT('')," +
                        "PayStatusCode INTEGER NOT NULL DEFAULT(0)," +
                        "PayStatusCodeName TEXT(32) NOT NULL DEFAULT('')," +
                        "IsSettlement INTEGER NOT NULL DEFAULT(1)," +
                        "Score INTEGER NOT NULL DEFAULT(0)," +
                        "Discount DECIMAL(6,2) NOT NULL," +
                        "TicketMoney DECIMAL(6,2) NOT NULL," +
                        "FactPay DECIMAL(10,2) NOT NULL," +
                        "Amount DECIMAL(10,2) NOT NULL," +
                        "SendType INTEGER NOT NULL DEFAULT(0)," +
                        "SendTypeName TEXT(32) NOT NULL DEFAULT('')," +
                        "SendTime DATETIME NOT NULL," +
                        "SendDate DATETIME NOT NULL," +
                        "Message TEXT(255) NOT NULL DEFAULT('')," +
                        "Remark TEXT(32) NOT NULL DEFAULT(''))");
                db.execSQL("create table EleDetail(" +
                        "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "OrderNum TEXT(32) NOT NULL DEFAULT('')," +
                        "ProductName TEXT(16) NOT NULL DEFAULT('')," +
                        "ProductId TEXT(36) NOT NULL DEFAULT('')," +
                        "CategoryId TEXT(36) NOT NULL DEFAULT('')," +
                        "Count DECIMAL(6,2) NOT NULL," +
                        "FactCount DECIMAL(6,2) NOT NULL," +
                        "Discount DOUBLE NOT NULL," +
                        "Price DECIMAL(6,2) NOT NULL," +
                        "DiscountPrice DECIMAL(6,2) NOT NULL," +
                        "UnitType INTEGER NOT NULL DEFAULT(0)," +
                        "Unit TEXT(6) NOT NULL DEFAULT('斤')," +
                        "Amount DECIMAL(10,2) NOT NULL)");
                break;
            case 10:
                db.execSQL("CREATE TABLE PurchaseSubtotal(" +
                        "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "Supplier TEXT(255) NOT NULL DEFAULT('')," +
                        "BatchNumber TEXT(255) NOT NULL DEFAULT('')," +
                        "Tel TEXT(16) NOT NULL DEFAULT('')," +
                        "StockDate DATETIME NOT NULL," +
                        "PurOrderImg TEXT(128) NOT NULL DEFAULT('')," +
                        "DetailsNumber INTEGER," +
                        "Remark TEXT(32) NOT NULL DEFAULT(''))");
                db.execSQL("CREATE TABLE PurchaseDetail(" +
                        "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "S_Id INTEGER," +
                        "OrderNum INTEGER," +
                        "ProductName TEXT(32) NOT NULL DEFAULT('')," +
                        "PluCode TEXT(32) NOT NULL DEFAULT('')," +
                        "Quantity DECIMAL(6,3)," +
                        "UnitPrice DECIMAL(4,2)," +
                        "Amount DECIMAL(10,2)," +
                        "SellQuantity DECIMAL(6,3)," +
                        "CertificateImg TEXT(128) NOT NULL DEFAULT('')," +
                        "Unit INTEGER DEFAULT(0)," +
                        "Remark TEXT(32) NOT NULL DEFAULT(''))");
                break;
        }
    }

    private void createMonthData(SQLiteDatabase db) {
        db.execSQL("insert into Months(MonthId,MonthChineseName)values('01','一月')");
        db.execSQL("insert into Months(MonthId,MonthChineseName)values('02','二月')");
        db.execSQL("insert into Months(MonthId,MonthChineseName)values('03','三月')");
        db.execSQL("insert into Months(MonthId,MonthChineseName)values('04','四月')");
        db.execSQL("insert into Months(MonthId,MonthChineseName)values('05','五月')");
        db.execSQL("insert into Months(MonthId,MonthChineseName)values('06','六月')");
        db.execSQL("insert into Months(MonthId,MonthChineseName)values('07','七月')");
        db.execSQL("insert into Months(MonthId,MonthChineseName)values('08','八月')");
        db.execSQL("insert into Months(MonthId,MonthChineseName)values('09','九月')");
        db.execSQL("insert into Months(MonthId,MonthChineseName)values('10','十月')");
        db.execSQL("insert into Months(MonthId,MonthChineseName)values('11','十一月')");
        db.execSQL("insert into Months(MonthId,MonthChineseName)values('12','十二月')");
    }
}
