package com.tnd.multifuction1.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.tnd.jinbiao.model.CardCompanyModel;
import com.tnd.jinbiao.model.LineModel;
import com.tnd.jinbiao.model.PeopleModel;
import com.tnd.jinbiao.model.ResultModel;
import com.tnd.jinbiao.model.ResultPhotoImgModel;
import com.tnd.jinbiao.model.SampleModel;
import com.tnd.jinbiao.model.SampleTypeModel;
import com.tnd.jinbiao.model.SampleUnitModel;
import com.tnd.jinbiao.model.ShiJiModel;
import com.tnd.multifuction1.util.Global;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-08.
 */

public class DbHelper {

    private static ArrayList<LineModel> list;

    private DbHelper(){

    }
    private static DbUtils db = null;
    private static Context context;

    public static DbUtils GetInstance(){
        return  db;
    }

    /**
     * 初始化数据库
     * @param ctx
     * 上下文
     */
    public static void InitDb(Context ctx) {

        context = ctx;
        File file = context.getDatabasePath(Global.DATA_BASE_NAME);
        boolean isExist = file.exists();
        db = DbUtils.create(context, Global.DATA_BASE_NAME, Global.DATABASE_VERSION,
                new DbUtils.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
                        //InitTables(db);
                        if (oldVersion < newVersion) {
                            //2019.3.19
                            if (oldVersion == 2 && newVersion == 3) {
                                try {
                                    db.deleteAll(LineModel.class);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                InitTables(db);
                            }
                            String s = ResultModel.class.toString().replace("class", "").replace(".", "_").trim();
                            System.out.println(s);
                            System.out.println("数据库升级了");
                            try {
                                db.execNonQuery("alter table " + s + " add sample_number TEXT DEFAULT \"\"");
                                db.execNonQuery("alter table " + s + " add sample_unit TEXT DEFAULT \"\"");
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        db.configAllowTransaction(true);
        if(!isExist){
            InitTables(db);
        }
    }

    private static void InitTables(DbUtils dbUtil) {
        try {
            dbUtil.createTableIfNotExist(ResultModel.class);
            dbUtil.createTableIfNotExist(PeopleModel.class);
            dbUtil.createTableIfNotExist(LineModel.class);
            dbUtil.createTableIfNotExist(CardCompanyModel.class);
            dbUtil.createTableIfNotExist(ResultPhotoImgModel.class);
            dbUtil.createTableIfNotExist(SampleModel.class);
            dbUtil.createTableIfNotExist(SampleUnitModel.class);
            dbUtil.createTableIfNotExist(ShiJiModel.class);
//            System.out.println("表初始化完毕！");
            InitDbData(dbUtil);
        } catch (DbException e1) {
            e1.printStackTrace();
        }
    }

    private static void InitDbData(DbUtils dbUtil) {
        String concentrateUnit = "μg/kg";
        CardCompanyModel cardModel1 = new CardCompanyModel("浩景Q", "320","920","85","310");
        CardCompanyModel cardModel2 = new CardCompanyModel("浩景A", "320","920","100",  "345");
        CardCompanyModel cardModel3 = new CardCompanyModel("浩景R", "420","930","80", "300");

        //初始化检测项目
        list = new ArrayList<>();
//        String[] projectNames1 = new String[]{"氯霉素", "呋喃唑酮", "呋喃他酮",
//            "呋喃西林", "呋喃妥因", "孔雀石绿",
//            "氟苯尼考", "盐酸克伦特罗", "莱克多巴胺",
//            "沙丁胺醇",
//            "黄曲霉毒素B1", "呕吐毒素", "三聚氰胺"
//        };
        String[] projectNames1 = new String[]{
                "盐酸克伦特罗",
                "莱克多巴胺",
                "沙丁胺醇",
                "多菌灵",
                "灭多威",
                "克百威",
                "三聚氰胺",
                "黄曲霉M1",
                "罂粟壳",
                "玉米赤霉烯酮",


                "喹诺酮",
                "黄曲霉B1",
                "腐霉利",
                "呕吐毒素",
                "罗丹明B",
                "磺胺类",
                "豆芽无根水",
                "氯霉素",
                "孔雀石绿",
                "呋喃唑酮",
                "呋喃它酮",
                "呋喃妥因",
                "呋喃西林"

        };
        for (int i = 0; i < projectNames1.length; i++) {
            if (i < 3) {
                list.add(new LineModel(2, projectNames1[i], cardModel1.name, cardModel1.ScanStart, cardModel1.ScanEnd, cardModel1.CTPeakWidth, cardModel1.CTPeakDistance, "1", "0.1", "比值"));
            }else if (i >= 3 && i < 10){
                list.add(new LineModel(2, projectNames1[i], cardModel2.name, cardModel2.ScanStart, cardModel2.ScanEnd, cardModel2.CTPeakWidth, cardModel2.CTPeakDistance, "1", "0.1", "比值"));
            } else if (i >= 10 && i < 17) {
                list.add(new LineModel(3, projectNames1[i], cardModel2.name, cardModel2.ScanStart, cardModel2.ScanEnd, cardModel2.CTPeakWidth, cardModel2.CTPeakDistance, "1", "0.9", "比值"));
            } else {
                list.add(new LineModel(3, projectNames1[i], cardModel3.name, cardModel3.ScanStart, cardModel3.ScanEnd, cardModel3.CTPeakWidth, cardModel3.CTPeakDistance, "1", "0.9", "比值"));

            }

        }

        try {
            db.saveAll(list);
            db.save(cardModel3);
            db.save(cardModel2);
            db.save(cardModel1);
        } catch (DbException e) {
            e.printStackTrace();
        }
        try {
//        1、	检测单位：检测站
            {
                PeopleModel checkDep = new PeopleModel("检测站", 1);
                db.save(checkDep);
            }

            {
//        2、	检验员：admin
                PeopleModel checker = new PeopleModel("admin", 2);
                db.save(checker);
            }
//        3、	试剂厂商：浩景
            {
                ShiJiModel reagentCompany = new ShiJiModel();
                reagentCompany.code = "";
                reagentCompany.name = "浩景";
                db.save(reagentCompany);
            }
//        4、	样品类型：食用油、乳及乳制品、谷物
            {
                List<SampleTypeModel> sampleTypeList = new ArrayList<>();
                sampleTypeList.add(new SampleTypeModel("新鲜果蔬"));
                sampleTypeList.add(new SampleTypeModel("新鲜肉类"));
                sampleTypeList.add(new SampleTypeModel("新鲜水产"));
                sampleTypeList.add(new SampleTypeModel("冻肉类"));
                sampleTypeList.add(new SampleTypeModel("腌制肉类"));
                sampleTypeList.add(new SampleTypeModel("冰鲜水产"));
                sampleTypeList.add(new SampleTypeModel("粮油类"));
                sampleTypeList.add(new SampleTypeModel("乳及乳制品"));
                sampleTypeList.add(new SampleTypeModel("谷物"));
                db.saveAll(sampleTypeList);
            }
//        5、	样品名称：花生油、牛奶、大米、玉米、谷物饲料
            {
                List<SampleModel> sampleList = new ArrayList<>();
                sampleList.add(new SampleModel("花生油"));
                sampleList.add(new SampleModel("牛奶"));
                sampleList.add(new SampleModel("大米(粳米)"));
                sampleList.add(new SampleModel("玉米"));
                sampleList.add(new SampleModel("谷物饲料"));

                sampleList.add(new SampleModel("猪肉"));
                sampleList.add(new SampleModel("牛肉"));
                sampleList.add(new SampleModel("羊肉"));
                sampleList.add(new SampleModel("鸡肉"));
                sampleList.add(new SampleModel("鸭肉"));
                sampleList.add(new SampleModel("鹅肉"));
                sampleList.add(new SampleModel("兔肉"));
                sampleList.add(new SampleModel("鱼"));
                sampleList.add(new SampleModel("虾"));
                sampleList.add(new SampleModel("蟹"));
                sampleList.add(new SampleModel("黄瓜"));
                sampleList.add(new SampleModel("草莓"));
                sampleList.add(new SampleModel("花椒"));
                sampleList.add(new SampleModel("八角"));
                sampleList.add(new SampleModel("辣椒干"));
                sampleList.add(new SampleModel("辣椒粉"));
                sampleList.add(new SampleModel("陈皮"));
                sampleList.add(new SampleModel("养殖水"));
                db.saveAll(sampleList);
            }
//        6、	送检单位：超市、市场、其他
            {
                List<PeopleModel> checkedOrgList = new ArrayList<>();
                checkedOrgList.add(new PeopleModel("超市", 3));
                checkedOrgList.add(new PeopleModel("市场", 3));
                checkedOrgList.add(new PeopleModel("其他", 3));
                db.saveAll(checkedOrgList);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 样品号
     *
     * @param goodsName
     * @return
     */
    public static String getSampleNumber(String goodsName) {

        SQLiteDatabase database = db.getDatabase();
        if(database == null) return null;

//        Cursor cursor = database.query("com_tnd_multifuction_model_SampleName", null, "sampleName = ?", new String[]{goodsName}, null, null, "", "");
        Cursor cursor = database.query("com_tnd_multifuction1_model_SampleName", null, "sampleName = ?", new String[]{goodsName}, null, null, "", "");
        if(cursor == null) return null;

        int statusNumber = cursor.getCount();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();//移动到首位
            String sampleNumber = cursor.getString(cursor.getColumnIndex("sampleNumber"));
            cursor.close();//释放资源
            return sampleNumber;
        }

        return null;

    }


}
