package cn.chenzhongjin.realmsample.eventbus;

import java.util.List;

import cn.chenzhongjin.realmsample.entity.User;

/**
 * @author chenzj
 * @Title: SelectEvent
 * @Description: 类的描述 -
 * @date
 * @email admin@chenzhongjin.cn
 */
public class SelectEvent {

    public List<User> mUsers;

    public SelectEvent(List<User> users) {
        mUsers = users;
    }
}

