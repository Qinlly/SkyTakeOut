package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.UserService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 营业额统计
     * @param begin 开始日期
     * @param end 结束日期
     * @return 营业额统计结果
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> dateList = getDateList(begin, end);

        String dateStr = StringUtils.join(dateList, ",");

        List<Double> turnoverList = new ArrayList<>();//存放每一天的营业额

        //查询每一天的营业额
        dateList.forEach(date -> {
            //统计状态为已完成的订单
            LocalDateTime beginTime = LocalDateTime.of(date, LocalDateTime.MIN.toLocalTime());
            LocalDateTime endTime = LocalDateTime.of(date, LocalDateTime.MAX.toLocalTime());
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            if (turnover == null) {
                turnover = 0.0;
            }
            turnoverList.add(turnover);
        });

        String turnoverStr = StringUtils.join(turnoverList, ",");

        return new TurnoverReportVO(dateStr,turnoverStr);
    }

    /**
     * 用户统计
     * @param begin 开始日期
     * @param end 结束日期
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> dateList = getDateList(begin, end);

        String dateStr = StringUtils.join(dateList, ",");

        List<Integer> newUserList = new ArrayList<>(); //存放每一天的新增用户数
        List<Integer> totalUserList = new ArrayList<>(); //存放每一天的总用户数

        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalDateTime.MIN.toLocalTime()); //一天的开始时间
            LocalDateTime endTime = LocalDateTime.of(date, LocalDateTime.MAX.toLocalTime());  //一天的结束时间

            Map map = new HashMap();
            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map); //一天的总用户数
            totalUserList.add(totalUser);

            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map); //一天的新增用户数
            newUserList.add(newUser);

        });

        String newUserStr = StringUtils.join(newUserList, ",");
        String totalUserStr = StringUtils.join(totalUserList, ",");

        return new UserReportVO(dateStr, newUserStr, totalUserStr);

    }

    /**
     * 订单统计
     * @param begin 开始日期
     * @param end 结束日期
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> dateList = getDateList(begin, end);

        String dateStr = StringUtils.join(dateList, ",");

        List<Integer> totalOrderList = new ArrayList<>(); //存放每一天的总订单数
        List<Integer> validOrderList = new ArrayList<>(); //存放每一天的有效订单数

        Integer totalOrderCount = 0; //总订单数
        Integer validOrderCount = 0; //有效订单数

        for(LocalDate date : dateList){    //查询每天的有效订单数
            LocalDateTime beginTime = LocalDateTime.of(date, LocalDateTime.MIN.toLocalTime());
            LocalDateTime endTime = LocalDateTime.of(date, LocalDateTime.MAX.toLocalTime());

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer totalOrder = orderMapper.countByMap(map);
            totalOrderCount += totalOrder;

            map.put("status", Orders.COMPLETED);
            Integer validOrder = orderMapper.countByMap(map);
            validOrderCount += validOrder;

            totalOrderList.add(totalOrder);
            validOrderList.add(validOrder);
        }
        String totalOrderStr = StringUtils.join(totalOrderList, ",");
        String validOrderStr = StringUtils.join(validOrderList, ",");
        Double validRate = 0.0;

        if (totalOrderCount != 0) {
            validRate = validOrderCount * 1.0 / totalOrderCount;
        }

        return new OrderReportVO(dateStr, totalOrderStr, validOrderStr, totalOrderCount, validOrderCount, validRate);
    }

    /**
     * 商品销量统计
     * @param begin 开始日期
     * @param end 结束日期
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalDateTime.MIN.toLocalTime());
        LocalDateTime endTime = LocalDateTime.of(end, LocalDateTime.MAX.toLocalTime());


        List<GoodsSalesDTO> salesTop10List = orderMapper.getTop10(beginTime, endTime);
        List<String> goodsNameList = salesTop10List.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<String> goodsNumberList = salesTop10List.stream().map(GoodsSalesDTO::getNumber).map(String::valueOf).collect(Collectors.toList());

        String goodsNameStr = StringUtils.join(goodsNameList, ",");
        String goodsNumberStr = StringUtils.join(goodsNumberList, ",");

        return new SalesTop10ReportVO( goodsNameStr, goodsNumberStr);
    }


    /**
     * 获取日期列表
     * @param begin 开始日期
     * @param end 结束日期
     * @return 日期列表
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        //存放begin到end的每一天
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        while (!dateList.get(dateList.size() - 1).isAfter(end)) {
            dateList.add(dateList.get(dateList.size() - 1).plusDays(1));
        }

        return dateList;
    }
}
