package cn.chenzhongjin.realmsample.ui.base;

import cn.chenzhongjin.realmsample.AppContext;
import cn.chenzhongjin.realmsample.database.RealmManager;
import io.realm.Realm;

/**
 * @author: chenzj
 * @Title: BaseActivity
 * @Description:
 * @date: 2016/3/24 22:51
 * @email: admin@chenzhongjin.cn
 */
public abstract class BaseActivity extends BaseCompatActivity {

    protected Realm mRealm;

    protected AppContext getAppContext() {
        return AppContext.getInstance();
    }

    @Override
    protected void initSpecialView() {
        super.initSpecialView();
        mRealm = RealmManager.getRealm();
    }
}
