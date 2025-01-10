package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron="0 * * * * ?") // 每分钟执行一次
    public void processTimeoutOrder() {
        log.info("处理超时订单: {}", LocalDateTime.now());

        //查询所有超时订单 待付款且超过15分钟的订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT,LocalDateTime.now().minusMinutes(15));
        if (ordersList!= null && ordersList.size() > 0) {
            for (Orders order : ordersList) {
                // 超时订单处理
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时未支付，系统自动取消订单");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);

            }
        }
    }

    /**
     * 处理派送中订单
     */
    @Scheduled(cron="0 0 1 * * ?")//每天1点执行一次
    public void processDeliveryOrder() {
        log.info("处理派送中订单: {}", LocalDateTime.now());

        //查询所有派送中订单 待收货的订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().minusMinutes(60));
        if (ordersList!= null && ordersList.size() > 0) {
            for (Orders order : ordersList) {
                // 超时订单处理
                order.setStatus(Orders.COMPLETED);
                order.setCancelReason("订单超时未收货，系统自动完成订单");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);

            }
        }

    }
}
