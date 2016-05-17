package cn.chenzhongjin.realmsample.ui.base;


import android.view.View;

import cn.chenzhongjin.realmsample.AppContext;
import cn.chenzhongjin.realmsample.database.RealmManager;
import io.realm.Realm;

/**
 * @author chenzj
 * @Title: BaseFragment
 * @Description: 类的描述 -
 * @date 2016/3/26 11:08
 * @email admin@chenzhongjin.cn
 */
public abstract class BaseFragment extends BaseLazyFragment {

    protected Realm mRealm;

    protected AppContext getAppContext() {
        return AppContext.getInstance();
    }

    @Override
    protected void initSpecialView(View view) {
        super.initSpecialView(view);
        mRealm = RealmManager.getRealm();
    }
}

