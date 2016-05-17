package cn.chenzhongjin.realmsample.database;

import cn.chenzhongjin.realmsample.entity.ExtendBean;
import cn.chenzhongjin.realmsample.entity.User;
import io.realm.annotations.RealmModule;

/**
 * @author chenzj
 * @Title: CustomModule
 * @Description: 类的描述 -
 * @date
 * @email admin@chenzhongjin.cn
 */
@RealmModule(classes = {ExtendBean.class, User.class})
public class CustomModule {
}