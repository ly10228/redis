package com.ly.redis.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by chenshiyang on 2017.
 */
public class UserDTO implements Serializable {


    private Long id;

    private String name;

    private String accountName;

    private String email;

    private String phoneNumber;

    private String password;

    private String status;

    private String updatePassword;

    private String directSupervisor;

    private String position;

    private String wechatPlatformId;

    private Long tenantId;

    private Date gmtCreated;


    @ApiModelProperty("主键id")
    public Long getId() {
        return this.id;
    }

    public UserDTO setId(Long value) {
        this.id = value;
        return this;
    }

    @ApiModelProperty("用户名称")
    public String getName() {
        return this.name;
    }

    public UserDTO setName(String value) {
        this.name = value;
        return this;
    }

    @ApiModelProperty("账户名")
    public String getAccountName() {
        return this.accountName;
    }

    public UserDTO setAccountName(String value) {
        this.accountName = value;
        return this;
    }

    @ApiModelProperty("邮箱")
    public String getEmail() {
        return this.email;
    }

    public UserDTO setEmail(String value) {
        this.email = value;
        return this;
    }

    @ApiModelProperty("手机")
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public UserDTO setPhoneNumber(String value) {
        this.phoneNumber = value;
        return this;
    }

    @ApiModelProperty("密码")
    public String getPassword() {
        return this.password;
    }

    public UserDTO setPassword(String value) {
        this.password = value;
        return this;
    }

    @ApiModelProperty("状态")
    public String getStatus() {
        return this.status;
    }

    public UserDTO setStatus(String value) {
        this.status = value;
        return this;
    }

    @ApiModelProperty("是否修改过密码")
    public String getUpdatePassword() {
        return this.updatePassword;
    }

    public UserDTO setUpdatePassword(String value) {
        this.updatePassword = value;
        return this;
    }

    @ApiModelProperty("直属上级")
    public String getDirectSupervisor() {
        return this.directSupervisor;
    }

    public UserDTO setDirectSupervisor(String value) {
        this.directSupervisor = value;
        return this;
    }

    @ApiModelProperty("岗位")
    public String getPosition() {
        return this.position;
    }

    public UserDTO setPosition(String value) {
        this.position = value;
        return this;
    }

    @ApiModelProperty("微信平台id")
    public String getWechatPlatformId() {
        return this.wechatPlatformId;
    }

    public UserDTO setWechatPlatformId(String value) {
        this.wechatPlatformId = value;
        return this;
    }

    @ApiModelProperty("租户id")
    public Long getTenantId() {
        return this.tenantId;
    }

    public UserDTO setTenantId(Long value) {
        this.tenantId = value;
        return this;
    }

    @ApiModelProperty("创建时间")
    public Date getGmtCreated() {
        return this.gmtCreated;
    }

    public UserDTO setGmtCreated(Date value) {
        this.gmtCreated = value;
        return this;
    }


}
