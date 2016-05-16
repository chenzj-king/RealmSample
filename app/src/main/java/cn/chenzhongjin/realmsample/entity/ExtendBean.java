package cn.chenzhongjin.realmsample.entity;

import io.realm.RealmObject;

/**
 * @author chenzj
 * @Title: ExtendBean
 * @Description: 类的描述 -
 * @date 2016/5/16 21:46
 * @email admin@chenzhongjin.cn
 */
public class ExtendBean extends RealmObject {

    public String key;
    public String value;

    @Override
    public String toString() {
        return "ExtendBean{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
