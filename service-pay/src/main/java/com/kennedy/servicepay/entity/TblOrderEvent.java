package com.kennedy.servicepay.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * tbl_order_event
 * @author 
 */
@Data
public class TblOrderEvent implements Serializable {
    private Integer id;

    /**
     * 事件类型（支付表支付完成，订单表修改状态）
     */
    private String orderType;

    /**
     * 事件环节（new,published,processed)
     */
    private String process;

    /**
     * 事件内容，保存事件发生时需要传递的数据
     */
    private String content;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}