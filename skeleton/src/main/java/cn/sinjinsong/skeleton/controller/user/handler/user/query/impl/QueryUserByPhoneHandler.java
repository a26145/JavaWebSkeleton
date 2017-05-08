package cn.sinjinsong.skeleton.controller.user.handler.user.query.impl;

import cn.sinjinsong.skeleton.controller.user.handler.user.query.QueryUserHandler;
import cn.sinjinsong.skeleton.domain.entity.user.UserDO;
import cn.sinjinsong.skeleton.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by SinjinSong on 2017/5/6.
 */
@Component("QueryUserHandler.phone")
public class QueryUserByPhoneHandler implements QueryUserHandler {
    @Autowired
    private UserService userService;
    @Override
    public UserDO handle(String key) {
        return userService.findByEmail(key);
    }
}
