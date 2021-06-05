package com.ly.redis.service.impl;

import com.ly.redis.mapper.UserMapper;
import com.ly.redis.dto.UserDTO;
import com.ly.redis.entity.UserDO;
import com.ly.redis.enums.DeleteFlagEnum;
import com.ly.redis.service.UserService;
import com.ly.redis.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author luoyong
 *  * @create 2021-05-16 10:55 下午
 *  * @last modify by [luoyong 2021-05-16 10:55 下午]
 * @Description:
 **/
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String CACHE_KEY_USER = "user:";

    @Resource
    private UserMapper userDAO;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * @param userDTO
     * @return
     * @Description: 新增用户信息
     * @author luoyong
     * @create 10:59 下午 2021/5/16
     * @last modify by [LuoYong 10:59 下午 2021/5/16 ]
     */
    @Override
    public Boolean addUser(UserDTO userDTO) {
        if (null == userDTO) {
            log.info("用户信息为null");
            return false;
        }
        UserDO userDO = BeanUtils.clone(userDTO, UserDO.class);
        //1:入库
        int i = userDAO.insertSelective(userDO);

        if (i > 0) {
            //2:需要再查询一下mysql 确保数据已经落库了
            userDO = userDAO.selectByPrimaryKey(userDO.getId());

            //3: 存入redis 完成新增功能的数据一致性
            String key = CACHE_KEY_USER + userDO.getId();
            redisTemplate.opsForValue().set(key, userDO);
        }
        return true;
    }

    /**
     * @param userId
     * @return
     * @Description: 删除用户信息
     * @author luoyong
     * @create 11:00 下午 2021/5/16
     * @last modify by [LuoYong 11:00 下午 2021/5/16 ]
     */
    @Override
    public Boolean delUser(Long userId) {
        UserDO userDO = new UserDO();
        userDO.setId(userId);
        userDO.setIsDeleted(DeleteFlagEnum.DELETED.getValue());
        int i = userDAO.updateByPrimaryKeySelective(userDO);
        if (i > 0) {
            String key = CACHE_KEY_USER + userId;
            redisTemplate.delete(key);
        }
        return true;
    }

    /**
     * @param userDTO
     * @return java.lang.Boolean
     * @Description: 更新用户信息
     * @author luoyong
     * @create 11:01 下午 2021/5/16
     * @last modify by [LuoYong 11:01 下午 2021/5/16 ]
     */
    @Override
    public Boolean updateUser(UserDTO userDTO) {
        if (null == userDTO) {
            log.info("用户信息为null");
            return false;
        }
        UserDO userDO = BeanUtils.clone(userDTO, UserDO.class);
        int i = userDAO.updateByPrimaryKeySelective(userDO);
        if (i > 0) {
            //2:需要再查询一下mysql 确保数据已经落库了
            userDO = userDAO.selectByPrimaryKey(userDO.getId());

            //3: 将捞出来的user存进redis，完成修改
            String key = CACHE_KEY_USER + userDO.getId();
            redisTemplate.opsForValue().set(key, userDO);
        }
        return null;
    }

    /**
     * @param userId
     * @return
     * @Description: 根据用户id获取用户信息
     * 业务逻辑并没有写错，对于小厂中厂(QPS《=1000)可以使用，但是大厂不行
     * @author luoyong
     * @create 11:01 下午 2021/5/16
     * @last modify by [LuoYong 11:01 下午 2021/5/16 ]
     */
    @Override
    public UserDTO getUserInfo(Long userId) {
        UserDTO userDTO = null;
        String key = CACHE_KEY_USER + userId;

        //1 先从redis里面查询，如果有直接返回结果，如果没有再去查询mysql
        UserDO userDO = (UserDO) redisTemplate.opsForValue().get(key);

        if (null == userDO) {
            //2 redis里面无，继续查询mysql
            userDO = userDAO.selectByPrimaryKey(userId);
            if (null == userDO) {
                //3.1 redis+mysql 都无数据
                return null;
            } else {
                //3.2 mysql有，需要将数据写回redis，保证下一次的缓存命中率
                redisTemplate.opsForValue().set(key, userDO);
            }
        }
        if (null != userDO) {
            userDTO = BeanUtils.clone(userDO, UserDTO.class);
        }
        return userDTO;
    }

    /**
     * @param userId
     * @return
     * @Description: 根据用户id获取用户信息 高可用QPS
     * @author luoyong
     * @create 11:01 下午 2021/5/16
     * @last modify by [LuoYong 11:01 下午 2021/5/16 ]
     */
    @Override
    public UserDTO getUserInfo2(Long userId) {
        UserDTO userDTO = null;
        String key = CACHE_KEY_USER + userId;

        //1 先从redis里面查询，如果有直接返回结果，如果没有再去查询mysql
        UserDO userDO = (UserDO) redisTemplate.opsForValue().get(key);

        if (null == userDO) {
            //2 大厂用，对于高QPS的优化，进来就先加锁，保证一个请求操作，让外面的redis等待一下，避免击穿mysql
            synchronized (UserServiceImpl.class) {
                //3 二次查redis还是null，可以去查mysql了(mysql默认有数据)
                userDO = (UserDO) redisTemplate.opsForValue().get(key);
                if (null == userDO) {
                    //4 询mysql拿数据
                    userDO = userDAO.selectByPrimaryKey(userId);
                    if (null == userDO) {
                        //4 redis+mysql 都无数据
                        return null;
                    } else {
                        //5 mysql里面有数据的，需要回写redis，完成数据一致性的同步工作
                        redisTemplate.opsForValue().set(key, userDO);
                    }
                }
            }
        }
        if (null != userDO) {
            userDTO = BeanUtils.clone(userDO, UserDTO.class);
        }
        return userDTO;
    }
}
