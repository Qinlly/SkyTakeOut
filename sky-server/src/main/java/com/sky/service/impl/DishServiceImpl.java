package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 保存菜品+口味信息
     * @param dishDTO
     */

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

         dishMapper.insert(dish);
         // 获取插入的菜品id
         Long dishId = dish.getId();


         // 保存口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!= null && flavors.size() > 0){
            for(DishFlavor flavor : flavors){
                flavor.setDishId(dishId);
            }
            //口味表中插入数据
            dishFlavorMapper.insertBatch(flavors);

        }

    }

    /**
     * 分页查询菜品列表
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        PageResult pageResult = new PageResult();
        pageResult.setRecords(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断是否启售
        for(Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){//如果启用，则不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断是否在菜品套餐中
        List<Long> setmealIds = setmealDishMapper.getSetmealIdByDishId(ids);
        if(setmealIds!= null && setmealIds.size() > 0){
            //如果在套餐中，则不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }


/*
        for(Long id : ids){
            //删除菜品
            dishMapper.deleteById(id);
            //删除菜品口味信息
            dishFlavorMapper.deleteByDishId(id);
        }

*/
        //批量删除
        dishMapper.deleteBatchByIds(ids);
        dishFlavorMapper.deleteBatchByDishIds(ids);


    }
}
