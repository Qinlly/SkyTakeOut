package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.UserService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @Autowired
    private WorkspaceService workspaceService;

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
     * 导出数据
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //查询数据库,获取最近三十天营业数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));
        //将数据写入到excel中
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try{
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);  //基于模版创建excel

            //获取标签页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            //写入时间
            sheet.getRow(1).getCell(1).setCellValue(dateBegin.toString() + "至" + dateEnd.toString());

            //获取第四行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());  //营业额
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());  //订单完成率
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());  //新用户数

            //获取第五行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());  //有效订单数
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());  // 平均客单价


            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                //填充每一天的营业额
                BusinessDataVO data = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(data.getTurnover());
                row.getCell(3).setCellValue(data.getValidOrderCount());
                row.getCell(4).setCellValue(data.getOrderCompletionRate());
                row.getCell(5).setCellValue(data.getUnitPrice());
                row.getCell(6).setCellValue(data.getNewUsers());
            }
            //通过输出流,将excel下载到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            //关闭流
            outputStream.close();
            inputStream.close();
            excel.close();
        }catch (Exception e){
            e.printStackTrace();
        }


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
