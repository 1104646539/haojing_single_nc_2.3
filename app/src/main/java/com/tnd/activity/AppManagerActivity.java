package com.tnd.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.stericson.RootTools.RootTools;
import com.tnd.bean.AppInfoBean;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.util.DensityUtil;
import com.tnd.util.APPUtils;

import java.util.ArrayList;
import java.util.List;


public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvInnerSpace;
    private TextView tvSdSpace;

    private ListView listView;
    protected Context ctx;

    private TextView tvTitle;
    protected PopupWindow popWindow;

    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        ctx = this;

        tvInnerSpace = (TextView) findViewById(R.id.tv_space_inner);
        tvSdSpace = (TextView) findViewById(R.id.tv_space_sd);

        listView = (ListView) findViewById(R.id.listView);

        tvTitle = (TextView) findViewById(R.id.tv_sub_title);

        long sdUsableSpace = Environment.getExternalStorageDirectory().getUsableSpace();// 获得SD卡可用空间

        String sdSpaceStr = Formatter.formatFileSize(this, sdUsableSpace);
        tvSdSpace.setText("SD卡空间:"+sdSpaceStr);


        long innerUsableSpace = Environment.getDataDirectory().getUsableSpace();
        String innerSpaceStr = Formatter.formatFileSize(this, innerUsableSpace);
        tvInnerSpace.setText("内部空间:"+innerSpaceStr);

        proDlg = new ProgressDialog(this);
        proDlg.setMessage("玩命加载中，请稍候...");

        fillData();
        regListener();

        // 注册 ，删除应用时的广播
        deleteApkReceiver = new DeleteApkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED); // 删除应用时，广播的action
        // 如果要接收删除应用的广播 ，filter中必须添加此句
        filter.addDataScheme("package");
        registerReceiver(deleteApkReceiver, filter);

    }

    /**
     * 选择的条目的下标
     */
    private int selectPosition = -1;


    private void regListener() {
        // 设置条目点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            /**
             * @param view 当前点击的view,也是，getView 方法的返回值
             * @param position 当前点击的条目的下标
             */
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                dismissPopWindow();// 先偿试销毁之前的弹出窗体

                selectPosition = position;

                View contentView = getLayoutInflater().inflate(R.layout.popup_item, null);

                LinearLayout llUninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                LinearLayout llStart = (LinearLayout) contentView.findViewById(R.id.ll_start);
                LinearLayout llShared = (LinearLayout) contentView.findViewById(R.id.ll_share);
                LinearLayout llSetting = (LinearLayout) contentView.findViewById(R.id.ll_setting);
                llUninstall.setOnClickListener(AppManagerActivity.this);
                llStart.setOnClickListener(AppManagerActivity.this);
                llShared.setOnClickListener(AppManagerActivity.this);
                llSetting.setOnClickListener(AppManagerActivity.this);


                popWindow=new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, -2);
                //  如果要执行动画，popWindow 必须得有背景
                popWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 添加一个完全透明的背景

                int top; // 当前点击的view 的屏幕上的Y坐标

                int[] location = new int[2];
                view.getLocationInWindow(location);// 获得view 在当前窗体的坐标，参数是长度为2的整数数组
                // 当getLocationInWindow 方法执行完以后location[0] 就是x坐标 ,location[1] 就是view  的Y坐标
                top = location[1];

                int left  = DensityUtil.dip2px(ctx, 80); // 获得80个dp对应的像素
                popWindow.showAtLocation(listView, Gravity.LEFT+Gravity.TOP, left, top);

                // 添加动画 效果

                AlphaAnimation aa = new AlphaAnimation(0, 1); // 从完全透明，到完全不透明
                aa.setDuration(500);

                // 以(0,0.5f)为中心，从50% 放大至100%
                ScaleAnimation sa = new ScaleAnimation(0.5f, 1, 0.5f, 1,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(500);

                AnimationSet as = new AnimationSet(false);
                as.addAnimation(aa);
                as.addAnimation(sa);

                contentView.startAnimation(as);// 只有view 才有执行动画的功能,


            }
        });

        // 为listView 设置滑动的监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            // 滑动状态发生改变时，调用此方法
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            // 滑动listView时，不断调用此方法
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                dismissPopWindow();// 先偿试销毁之前的弹出窗体

                if(userAppList==null){// 证明此时，还没有获得数据
                    return;
                }

                // 如果屏幕可见的第一个条目是系统应用，那么，改变标题为系统应用
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                if(firstVisiblePosition >=userAppList.size()){
                    tvTitle.setText("系统应用");
                }else{
                    tvTitle.setText("用户应用");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dismissPopWindow();

        // 取消注册 广播
        unregisterReceiver(deleteApkReceiver);
    }

    private void dismissPopWindow() {
        if(popWindow!=null && popWindow.isShowing()){
            popWindow.dismiss();
            popWindow = null;
        }
    }



    /**
     * 所有的应用信息
     */
    private List<AppInfoBean> allAppInfo;
    /**
     * 用户应用信息
     */
    private List<AppInfoBean> userAppList;
    /**
     * 系统应用信息
     */
    private List<AppInfoBean> sysAppList;

    private ProgressDialog proDlg;

    private void fillData() {
        proDlg.show();

        // 开子线程获得数据
        new Thread(){

            public void run() {
                allAppInfo = APPUtils.getAllAppInfo(ctx);

                // 对总的集合进行分组
                userAppList = new ArrayList<AppInfoBean>();
                sysAppList = new ArrayList<AppInfoBean>();
                for(AppInfoBean app : allAppInfo){
                    if(app.isSys){
                        sysAppList.add(app);
                    }else{
                        userAppList.add(app);
                    }
                }

                handler.sendEmptyMessage(FLUSH_UI);

            };
        }.start();
    }

    /*
     * 最终效果的实现步骤：
     *  1- 展示所有的应用列表，不考滤分组，标题
     *  2- 想办法，分组
     *  3- 想办法，加标题 \ 固定在顶底的标题
     */

    private final int FLUSH_UI = 100;

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FLUSH_UI:
                    // 隐藏进度框
                    proDlg.dismiss();
                    // 显示listView
                    adapter = new MyAdapter();
                    listView.setAdapter(adapter);

                    break;

            }
        };
    };

    private MyAdapter adapter;


    /*
     * listView 定理一：
     * 在 getView 方法 中，我们一般都会复用convertView ，如果复用了convertView 那么，代码中有if判断 必须写 else
     *
     *
     */


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userAppList.size()+sysAppList.size();
        }

        @Override
        /**
         * 该方法的返回值，就是 listView.getItemAtPosition(position); 方法的返回值
         */
        public Object getItem(int position) {
            // 根据 position 获得对应的数据Bean
            AppInfoBean appInfo = null;// 获得指定位置的数据

            if (position < userAppList.size()) {
                // 从用户应用列表中取
                appInfo = userAppList.get(position);
            } else {
                // 从系统应用列是中取，
                appInfo = sysAppList.get(position - userAppList.size());
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        /**
         * 获得listView 中指定下标的 条目
         */
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            ViewHolder vh;

            if(convertView == null){
                view = getLayoutInflater().inflate(R.layout.list_item_app_manager,null);
                vh = new ViewHolder();

                // 找到子view
                ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon_list_item);
                TextView tvName = (TextView) view.findViewById(R.id.tv_name_list_item);
                TextView tvSize = (TextView) view.findViewById(R.id.tv_size_list_item);
                TextView tvLocation = (TextView) view.findViewById(R.id.tv_location_list_item);
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_sub_title_list_item);
//				tvTitle.setVisibility(View.GONE);

                // 将子view 打包
                vh.ivIcon = ivIcon;
                vh.tvName = tvName;
                vh.tvSize = tvSize;
                vh.tvLocation = tvLocation;
                vh.tvTitle = tvTitle;

                // 将包裹背在view的身上
                view.setTag(vh);

            }else{
                view = convertView;
                vh = (ViewHolder) view.getTag();
            }

            // 为子view赋值

            // 根据 position 获得对应的数据Bean
            AppInfoBean appInfo  = (AppInfoBean) getItem(position);

            vh.ivIcon.setBackgroundDrawable(appInfo.appIcon);

            vh.tvName.setText(appInfo.appName);

            String sizeStr = Formatter.formatFileSize(ctx, appInfo.appSize);
            vh.tvSize.setText("大小："+sizeStr);

            if(appInfo.isInSd){
                vh.tvLocation.setText("在SD卡中");
            }else{
                vh.tvLocation.setText("在内部存储中");
            }

            // 处理小标题,第一个用户条目，和第一个系统条目，显示标题
            if(position == 0){// 第一个用户条目
                vh.tvTitle.setVisibility(View.VISIBLE);
                vh.tvTitle.setText("用户应用");
            }else if(position == userAppList.size()){// 第一个系统条目
                vh.tvTitle.setVisibility(View.VISIBLE);
                vh.tvTitle.setText("系统应用");
            }else{
                // 其他位置的情况，统统隐藏小标题
                vh.tvTitle.setVisibility(View.GONE);
            }

            return view;
        }

    }

    private class ViewHolder{

        public TextView tvTitle;
        public TextView tvLocation;
        public TextView tvSize;
        public TextView tvName;
        public ImageView ivIcon;

    }

    @Override
    public void onClick(View v) {

        // 根据selectPosition 获得应用bean 的包名
        AppInfoBean appInfo = (AppInfoBean) listView.getItemAtPosition(selectPosition);
        switch (v.getId()) {
            case R.id.ll_uninstall:// 删除

                if(appInfo.isSys){
                    // 系统应用
                    try {
                        // 一： 判断手机是否ROOT
                        if(RootTools.isRootAvailable()){
                            // 二：判断当前应用是否可以使用root权限
                            if(RootTools.isAccessGiven()){
                                // 三：执行linux 指令
                                // 将/system改为可读可写的
                                RootTools.sendShell("mount -o remount rw /system", 2000);
                                // 获得选择的条目对应的 apk 文件路径
                                String apkPath = getPackageManager().getApplicationInfo(appInfo.packageName, 0).sourceDir;
                                RootTools.sendShell("rm -r "+apkPath, 2000);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else{
                    // 用户应用
//			act=android.intent.action.DELETE
//			cat=[android.intent.category.DEFAULT]
//			dat=package:cn.itcast.viewpagerindicatortest

                    Intent intent4 = new Intent("android.intent.action.DELETE");
                    intent4.addCategory("android.intent.category.DEFAULT");
                    intent4.setData(Uri.parse("package:"+appInfo.packageName));
                    startActivity(intent4);
                }
                break;
            case R.id.ll_setting://
                // 根据log日志所得：
//			act=android.settings.APPLICATION_DETAILS_SETTINGS
//			cat=[android.intent.category.DEFAULT]
//			dat=package:zz.itcast.widgettestz6

                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:"+appInfo.packageName));
                startActivity(intent);

                break;
            case R.id.ll_start:// 启动指定的应用

                // 获得启动应用的intent
                Intent intent2 = getPackageManager().getLaunchIntentForPackage(appInfo.packageName);
                if(intent2 !=null){
                    startActivity(intent2);
                }

                break;
            case R.id.ll_share://
//			act=android.intent.action.SEND
//			cat=[android.intent.category.DEFAULT]
//			typ=text/plain

                Intent intent3 = new Intent("android.intent.action.SEND");
                intent3.addCategory("android.intent.category.DEFAULT");
                intent3.setType("text/plain");
                intent3.putExtra(Intent.EXTRA_TEXT, "给你介绍一个好玩的应用："+appInfo.appName);

                startActivity(intent3);

                break;

        }
    }

    private DeleteApkReceiver deleteApkReceiver;

    /**
     * 删除应用时的广播
     * @author Administrator
     *
     */
    private class DeleteApkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("DeleteApkReceiver=====");

            Uri data = intent.getData();
            String packageName = data.toString().substring(8); // 去掉前面的package: 8个字符
            System.out.println("packageName:"+packageName);

            // 将删除的APK的应用信息，从集合中去掉，刷新页面

            // 增强for循环不允许对集合一边遍历一边修改
            // 普通 for 不建议对集合一边遍历一边修改
            // 遍历的时候，不修改，修改的时候，不遍历
//			for(AppInfoBean appInfo : userAppList){
//				if(appInfo.packageName.equals(packageName)){
//					userAppList.remove(appInfo);
//				}
//			}

            // 声明一个临时变量,记录将要被删除的appInfoBean
            AppInfoBean willDeleteBean = null;

            for(AppInfoBean appInfo : userAppList){
                if(appInfo.packageName.equals(packageName)){
                    willDeleteBean = appInfo;
                    break;
                }
            }
            userAppList.remove(willDeleteBean);

            // 如果是系统应用，就从系统应用的集合中删除
            willDeleteBean = null;
            for(AppInfoBean appInfo : sysAppList){
                if(appInfo.packageName.equals(packageName)){
                    willDeleteBean = appInfo;
                    break;
                }
            }
            sysAppList.remove(willDeleteBean);

            // 刷新页面
            adapter.notifyDataSetChanged();


        }

    }



}
