package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 营业额统计
     * @param begin 开始日期
     * @param end 结束日期
     * @return 营业额统计结果
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin 开始日期
     * @param end 结束日期
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     * @param begin 开始日期
     * @param end 结束日期
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 商品销量统计
     * @param begin 开始日期
     * @param end 结束日期
     */
    SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end);
}
