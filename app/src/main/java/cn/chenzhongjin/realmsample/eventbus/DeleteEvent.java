package cn.chenzhongjin.realmsample.eventbus;

import java.util.List;

import cn.chenzhongjin.realmsample.entity.User;

/**
 * @author chenzj
 * @Title: DeleteEvent
 * @Description: 类的描述 -
 * @date
 * @email admin@chenzhongjin.cn
 */
public class DeleteEvent {

    public List<User> mUsers;

    public DeleteEvent(List<User> users) {
        mUsers = users;
    }
}

