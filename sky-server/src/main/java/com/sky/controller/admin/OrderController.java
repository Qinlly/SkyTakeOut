package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "管理端订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;
    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("conditionSearch param: {}", ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 订单各状态数量统计
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("订单各状态数量统计")
    public Result<OrderStatisticsVO> statistics() {
        log.info("statistics");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 订单详情
     * @param id 订单id
     */
    @GetMapping("details/{id}")
    @ApiOperation("订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        log.info("details param: {}", id);
         OrderVO orderVO = orderService.orderDetail(id);
         return Result.success(orderVO);
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm (@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("confirm : {}", ordersConfirmDTO);
        orderService.confirm(ordersConfirmDTO);
        return  Result.success();
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception{
        log.info("rejection : {}", ordersRejectionDTO);
        orderService.rejection(ordersRejectionDTO);
        return  Result.success();
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
     @PutMapping("/cancel")
     @ApiOperation("取消订单")
    public Result cancel(@RequestBody  OrdersCancelDTO ordersCancelDTO) throws Exception{
         log.info("cancel : {}", ordersCancelDTO);
         orderService.adminCancel(ordersCancelDTO);
         return  Result.success();
     }

     /**
     * 订单派送
     * @param id 订单id
     * @return
     */
     @PutMapping("/delivery/{id}")
     @ApiOperation("订单派送")
     public Result delivery(@PathVariable("id") Long id) {
         log.info("订单开始派送 : {}", id);
         orderService.delivery(id);
         return Result.success();
     }

     /**
     * 完成订单
     * @param id 订单id
     * @return
     */
     @PutMapping("/complete/{id}")
     @ApiOperation("完成订单")
     public Result complete(@PathVariable("id") Long id) {
         log.info("订单完成 : {}", id);
         orderService.complete(id);
         return Result.success();
     }

}
