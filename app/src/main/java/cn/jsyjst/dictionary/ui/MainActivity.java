package cn.jsyjst.dictionary.ui;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.jsyjst.dictionary.R;
import cn.jsyjst.dictionary.view.FindFragment;
import cn.jsyjst.dictionary.view.HistoryFragment;

/**
 * 
 */
public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager pager;
    /**
     * List对应了底部两个导航的fragment,一个是查询页面，一个是历史记录页面
     */
    private List<Fragment> fragments;
    /**
     * 导航的控件
     */
    private RadioGroup radioGroup;
    /**
     * 用来实现返回两次退出程序
     */
    private long exitTime = 0;

    private RadioButton find;
    private RadioButton history;
    /**
     * 通知栏相关
     */
    private NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = (ViewPager) findViewById(R.id.viewPager);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        find = (RadioButton) findViewById(R.id.rb_find);
        history = (RadioButton) findViewById(R.id.rb_history);
        find.setOnClickListener(this);
        history.setOnClickListener(this);


        /**
         * 底部导航的时候会发生图片的颜色变化，所以radiobutton中的照片不是一张，而是引用了自定义的选择器照片
         * 本来使用的是getResources.getDrawable,不过已经过时，所以使用ContextCompat
         */
        Drawable drawable_find = ContextCompat.getDrawable(this,R.drawable.selector_image_color);
        /**
         *  当这个图片被绘制时，给他绑定一个矩形规定这个矩形
         *  参数前两个对应图片相对于左上角的新位置，后两个为图片的长宽
         */
        drawable_find.setBounds(0, 0, 80, 80);
        /**
         *   设置图片在文字的哪个方向,分别对应左，上，右，下
         */

        find.setCompoundDrawables(null, drawable_find, null, null);

        /**
         * 处理另外一个radiobutton,
         *
         */
        Drawable drawable_history = ContextCompat.getDrawable(this,R.drawable.selector_history_color);
        drawable_history.setBounds(0, 0, 80, 80);
        history.setCompoundDrawables(null, drawable_history, null, null);
        /**
         * 更新
         */
        initView();


        /**
         * 动态请求存储权限设置
         */
        if (ContextCompat.checkSelfPermission(this, android.Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        /**
         * 通知栏实现
         */
        Intent intent = new Intent(this, TranslateActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("渊词王")
                .setContentText("点击快速查词(可在设置中关闭)")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_dictionary)
                .setContentIntent(pi)
                .build();
        manager.notify(1, notification);
    }

    private void initView() {

        /**
         * 添加fragment到ArrayList中
         */
        fragments = new ArrayList<>();
        fragments.add(new FindFragment());
        fragments.add(new HistoryFragment());

        /**
         * 适配器
         */
        pager.setAdapter(new FragmentPagerAdapter(this.getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return fragments.get(arg0);
            }
        });
        /**
         * 添加页面切换事件的监听器
         */
        pager.addOnPageChangeListener(this);
        /**
         * 默认一开始点击的是查询页面
         */
        radioGroup.check(R.id.rb_find);

    }


    /**
     * 参数state有三种取值：
     * 0：什么都没做
     * 1：开始滑动
     * 2：滑动结束
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 这个方法会在屏幕滚动过程中不断被调用。 有三个参数，第一个position，这个参数要特别注意一下。
     * 当用手指滑动时，如果手指按在页面上不动，position和当前页面index是一致的；
     * 如果手指向左拖动（相应页面向右翻动），这时候position大部分时间和当前页面是一致的，
     * 只有翻页成功的情况下最后一次调用才会变为目标页面；如果手指向右拖动（相应页面向左翻动），
     * 这时候position大部分时间和目标页面是一致的，只有翻页不成功的情况下最后一次调用才会变为原页面。
     * 当直接设置setCurrentItem翻页时，如果是相邻的情况（比如现在是第二个页面，跳到第一或者第三个页面），
     * 如果页面向右翻动，大部分时间是和当前页面是一致的，只有最后才变成目标页面；
     * 如果向左翻动，position和目标页面是一致的。这和用手指拖动页面翻动是基本一致的。如果不是相邻的情况，
     * 比如我从第一个页面跳到第三个页面，position先是0，然后逐步变成1，然后逐步变成2；
     * 我从第三个页面跳到第一个页面，position先是1，然后逐步变成0，并没有出现为2的情况。
     * positionOffset是当前页面滑动比例，如果页面向右翻动，这个值不断变大，最后在趋近1的情况后突变为0
     * 。如果页面向左翻动，这个值不断变小，
     * 最后变为0。positionOffsetPixels是当前页面滑动像素，变化情况和positionOffset一致
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 这个方法有一个参数position，代表哪个页面被选中。当用手指滑动翻页的时候，如果翻动成功了（滑动的距离够长），
     * 手指抬起来就会立即执行这个方法，position就是当前滑动到的页面。如果直接setCurrentItem翻页，
     * 那position就和setCurrentItem的参数一致，这种情况在onPageScrolled执行方法前就会立即执行。
     */
    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                radioGroup.check(R.id.rb_find);
                break;
            case 1:
                radioGroup.check(R.id.rb_history);
                break;

            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_find:
                pager.setCurrentItem(0, true);
                break;
            case R.id.rb_history:
                pager.setCurrentItem(1, true);
                break;

            default:
                break;
        }
    }

    /**
     *
     * 如果用户拒绝打开权限，就令程序退出，并取消掉通知栏
     * 如果打开权限，就执行initView的方法
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initView();
                }else{
                    manager.cancel(1);
                    finish();
                    System.exit(0);
                }
                break;
            default:
                break;
        }
    }

    /**
     * System.currentTimeMillis() 获得的是自1970-1-01 00:00:00.000 到当前时刻的时间距离,类型为long
     * 实现按两次退出
     *
     * @param keyCode
     * @param event
     * @return
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(this, "再按一次退出词王", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
