package cn.chenzhongjin.realmsample;

import android.app.Application;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import cn.chenzhongjin.realmsample.database.RealmManager;
import cn.chenzhongjin.realmsample.entity.ExtendBean;
import cn.chenzhongjin.realmsample.entity.User;
import cn.chenzhongjin.realmsample.utils.PreferencesUtils;
import io.realm.Realm;

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

        if (PreferencesUtils.getBoolean(this, PreferencesUtils.FIRST_START, true)) {
            Logger.i("first Start");

            Realm realm = RealmManager.getRealm();

            for (int i = 0; i < 10; i++) {
                realm.beginTransaction();

                User user = realm.createObject(User.class);

                user.name = "测试数据" + (i + 1);
                user.phoneNum = "1802730070" + (i + 1);
                user.sex = i % 3 == 0 ? "男" : "女";

                ExtendBean extendBean = realm.createObject(ExtendBean.class);
                extendBean.key = "education";
                switch (i % 4) {
                    case 0:
                        extendBean.value = "高中";
                        break;
                    case 1:
                        extendBean.value = "大专";
                        break;
                    case 2:
                        extendBean.value = "本科";
                        break;
                    case 3:
                        extendBean.value = "硕士";
                        break;

                }
                user.mExtendBeanRealmList.add(extendBean);
                realm.commitTransaction();
            }
            PreferencesUtils.putBoolean(this, PreferencesUtils.FIRST_START, false);
        }
    }

    private void initLogger() {
        Logger.init("sq580").methodCount(1).methodOffset(0).logLevel(LogLevel.FULL).hideThreadInfo();
    }

    private void initDataBase() {
        RealmManager.getInstanse();
    }
}
