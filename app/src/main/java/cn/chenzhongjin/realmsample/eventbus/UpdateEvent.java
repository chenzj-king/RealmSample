package cn.chenzhongjin.realmsample.eventbus;

import java.util.List;

import cn.chenzhongjin.realmsample.entity.User;

/**
 * @author chenzj
 * @Title: UpdateEvent
 * @Description: 类的描述 -
 * @date
 * @email admin@chenzhongjin.cn
 */
public class UpdateEvent {

    public List<User> mUsers;

    public UpdateEvent(List<User> users) {
        mUsers = users;
    }
}

