package com.ly.redis.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author luoyong
 *  * @create 2021-05-29 11:59 上午
 *  * @last modify by [luoyong 2021-05-29 11:59 上午]
 * @Description: 产品信息
 **/
@Data
@AllArgsConstructor
public class Product {

    @ApiModelProperty("产品id")
    private Long id;

    @ApiModelProperty("产品名称")
    private String name;

    @ApiModelProperty("产品价格")
    private Integer price;

    @ApiModelProperty("产品详情")
    private String detail;
}
