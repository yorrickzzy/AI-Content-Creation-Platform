package com.example.mapper;

import com.example.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{username}")
    User findByUserName(@Param("username") String username);

    @Insert("insert into user(username, password, create_time, update_time) " +
            "values(#{username}, #{password}, now(), now())")
    void add(@Param("username") String username, @Param("password") String password);

    @Update("update user set nickname=#{nickname},email=#{email},update_time=#{updateTime},version=version+1 where id=#{id} and version=#{version}")
    int update(User user);

    @Update("update user set user_pic=#{avatarUrl},update_time=now() where id=#{id}")
    int updateAvatar(String avatarUrl, Integer id);

    @Update("update user set password=#{md5String},update_time=now() where id=#{id}")
    int updatePwd(String md5String, Integer id);
}
