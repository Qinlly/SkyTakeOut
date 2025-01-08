package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单查询
     * @param page, pageSize, status
     * @return
     */
    PageResult page(int page, int pageSize, Integer status);

    /**
     * 订单详情查询
     * @param id 订单id
     * @return
     */
    OrderVO orderDetail(Long id);

    /**
     * 取消订单
     * @param id 订单id
     * @return
     */
    void userCancel(Long id) throws Exception;

    /**
     * 再来一单
     * @param id 订单id
     * @return
     */
    void repetition(Long id);

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 订单各状态数量统计
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO
     * @return
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    void adminCancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    /**
     * 订单派送
     * @param id 订单id
     * @return
     */
    void delivery(Long id);

    /**
     * 完成订单
     * @param id 订单id
     * @return
     */
    void complete(Long id);
}
