package com.ly.redis.service;

import com.ly.redis.dto.UserDTO;

/**
 * @author luoyong
 *  * @create 2021-05-16 10:55 下午
 *  * @last modify by [luoyong 2021-05-16 10:55 下午]
 * @Description:
 **/
public interface UserService {

    /**
     * @param userDTO
     * @return
     * @Description: 新增用户信息
     * @author luoyong
     * @create 10:59 下午 2021/5/16
     * @last modify by [LuoYong 10:59 下午 2021/5/16 ]
     */
    Boolean addUser(UserDTO userDTO);

    /**
     * @param userId
     * @return
     * @Description: 删除用户信息
     * @author luoyong
     * @create 11:00 下午 2021/5/16
     * @last modify by [LuoYong 11:00 下午 2021/5/16 ]
     */
    Boolean delUser(Long userId);

    /**
     * @param userDTO
     * @return java.lang.Boolean
     * @Description: 更新用户信息
     * @author luoyong
     * @create 11:01 下午 2021/5/16
     * @last modify by [LuoYong 11:01 下午 2021/5/16 ]
     */
    Boolean updateUser(UserDTO userDTO);

    /**
     * @param userId
     * @return
     * @Description: 根据用户id获取用户信息
     * @author luoyong
     * @create 11:01 下午 2021/5/16
     * @last modify by [LuoYong 11:01 下午 2021/5/16 ]
     */
    UserDTO getUserInfo(Long userId);

    /**
     * @param userId
     * @return
     * @Description: 根据用户id获取用户信息
     * @author luoyong
     * @create 11:01 下午 2021/5/16
     * @last modify by [LuoYong 11:01 下午 2021/5/16 ]
     */
    UserDTO getUserInfo2(Long userId);
}

