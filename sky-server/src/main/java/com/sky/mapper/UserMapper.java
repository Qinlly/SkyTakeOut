package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openId
     * @return
     */
    @Select("SELECT * FROM user WHERE openid = #{openId}")
    User getByOpenid(String openId);

    /**
     * 插入用户
     * @param user
     */
    void insert(User user);

    /**
     * 根据id查询用户
     * @param userId
     * @return
     */
    @Select("SELECT * FROM user WHERE id = #{userId}")
    User getById(Long userId);

    Integer countByMap(Map map);
}
