package com.ly.redis.resource;

import com.ly.redis.dto.UserDTO;
import com.ly.redis.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author luoyong
 *  * @create 2021-05-16 10:53 下午
 *  * @last modify by [luoyong 2021-05-16 10:53 下午]
 * @Description: 用户接口信息
 **/
@Api("用户信息")
@RestController
@Slf4j
public class UserResource {
    @Resource
    private UserService userService;

    @ApiOperation("数据库新增5条记录")
    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public Boolean addUser(@RequestBody UserDTO userDTO) {
        return userService.addUser(userDTO);
    }

    @ApiOperation("删除用户")
    @RequestMapping(value = "/user/delete/{id}", method = RequestMethod.POST)
    public Boolean deleteUser(@PathVariable Long id) {
        return userService.delUser(id);
    }

    @ApiOperation("修改用户信息")
    @RequestMapping(value = "/user/update", method = RequestMethod.POST)
    public Boolean updateUser(@RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    @ApiOperation("查询用户信息")
    @RequestMapping(value = "/user/find/{id}", method = RequestMethod.GET)
    public UserDTO findUserById(@PathVariable Long id) {
        return userService.getUserInfo(id);
    }

    @ApiOperation("查询用户信息")
    @RequestMapping(value = "/user/find/2/{id}", method = RequestMethod.GET)
    public UserDTO findUserById2(@PathVariable Long id) {
        return userService.getUserInfo2(id);
    }
}
