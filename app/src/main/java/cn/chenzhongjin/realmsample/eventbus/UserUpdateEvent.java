package cn.chenzhongjin.realmsample.eventbus;

import java.util.List;

import cn.chenzhongjin.realmsample.entity.User;

/**
 * @author chenzj
 * @Title: UserUpdateEvent
 * @Description: 类的描述 -
 * @date 2016/5/16 22:45
 * @email admin@chenzhongjin.cn
 */
public class UserUpdateEvent {

    public List<User> mUsers;

    public UserUpdateEvent(List<User> users) {
        mUsers = users;
    }
}
