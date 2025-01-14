package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "数据统计相关接口")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计
     * @param begin 开始日期
     * @param end 结束日期
     * @return 营业额统计结果
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin ,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        log.info("营业额统计，参数：begin={}, end={}", begin, end);
        return Result.success(reportService.getTurnoverStatistics(begin, end));
    }

    /**
     * 用户统计
     * @param begin 开始日期
     * @param end 结束日期
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin ,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        log.info("用户统计，参数：begin={}, end={}", begin, end);
        return Result.success(reportService.getUserStatistics(begin, end));
    }


    /**
     * 订单统计
     * @param begin 开始日期
     * @param end 结束日期
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> orderStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin ,
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        log.info("订单统计，参数：begin={}, end={}", begin, end);
        return Result.success(reportService.getOrderStatistics(begin, end));
    }

    /**
     * 商品销量统计
     * @param begin 开始日期
     * @param end 结束日期
     */
    @GetMapping("/top10")
    @ApiOperation("商品销量统计")
    public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin ,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        log.info("商品销量统计，参数：begin={}, end={}", begin, end);
        return Result.success(reportService.getTop10(begin, end));
    }

    /**
     * 导出数据
     * @param response
     */
    @GetMapping("/export")
    @ApiOperation("导出数据")
    public void export(HttpServletResponse response) {
        reportService.exportBusinessData(response);
    }
}
