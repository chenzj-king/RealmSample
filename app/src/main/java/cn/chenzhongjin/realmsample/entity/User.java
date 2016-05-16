package cn.chenzhongjin.realmsample.entity;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * @author chenzj
 * @Title: User
 * @Description: 类的描述 -
 * @date 2016/5/16 21:46
 * @email admin@chenzhongjin.cn
 */
public class User extends RealmObject {

    public int age;
    public String sex;
    public String name;
    public int phoneNum;

    public RealmList<ExtendBean> mExtendBeanRealmList;

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", mExtendBeanRealmList=" + mExtendBeanRealmList +
                '}';
    }
}
