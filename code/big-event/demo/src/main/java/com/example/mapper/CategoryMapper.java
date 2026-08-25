package com.example.mapper;

import com.example.pojo.Category;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CategoryMapper {
    // 新增
        @Insert("insert into category(category_name,category_alias,create_user,create_time,update_time,version) " +
            "values(#{categoryName},#{categoryAlias},#{createUser},#{createTime},#{updateTime},1)")
    void add(Category category);

    // 查询所有
    @Select("select * from category where create_user = #{userId}")
    List<Category> list(Integer userId);

    // 根据id查询
    @Select("select * from category where id = #{id}")
    Category findById(Integer id);

    // 更新
    @Update("update category set category_name=#{categoryName},category_alias=#{categoryAlias},update_time=#{updateTime},version=version+1 where id=#{id} and version=#{version}")
    int update(Category category);

    // 根据id删除
    @Delete("delete from category where id = #{id}")
    void delete(Integer id);
}