package cn.chenzhongjin.realmsample;

import android.app.Application;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import cn.chenzhongjin.realmsample.database.RealmManager;

/**
 * @author chenzj
 * @Title: AppContext
 * @Description: 类的描述 -
 * @date 2016/5/16 21:51
 * @email admin@chenzhongjin.cn
 */
public class AppContext extends Application {

    public static AppContext mInstance;

    public static AppContext getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        initLogger();
        initDataBase();
    }

    private void initLogger() {
        Logger.init("sq580").methodCount(1).methodOffset(0).logLevel(LogLevel.FULL).hideThreadInfo();
    }

    private void initDataBase() {
        RealmManager.getInstanse();
    }
}
