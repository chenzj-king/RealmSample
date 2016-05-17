package cn.chenzhongjin.realmsample.eventbus;

import java.util.List;

import cn.chenzhongjin.realmsample.entity.User;

/**
 * @author chenzj
 * @Title: InsertEvent
 * @Description: 类的描述 -
 * @date
 * @email admin@chenzhongjin.cn
 */
public class InsertEvent {

    public List<User> mUsers;

    public InsertEvent(List<User> users) {
        mUsers = users;
    }
}

