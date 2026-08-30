package com.example.mapper;

import com.example.pojo.Article;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ArticleMapper {
    // 新增
        @Insert("insert into article(title,summary,content,cover_img,state,category_id,create_user,create_time,update_time,version) " +
            "values(#{title},#{summary},#{content},#{coverImg},#{state},#{categoryId},#{createUser},#{createTime},#{updateTime},1)")
    void add(Article article);

    List<Article> list(Integer userId, Integer categoryId, String state);

    @Select("select * from article where id = #{id}")
    Article findById(Integer id);

    @Update("update article set title=#{title},summary=#{summary},content=#{content},cover_img=#{coverImg},state=#{state},category_id=#{categoryId},update_time=#{updateTime},version=version+1 where id=#{id} and version=#{version}")
    int update(Article article);

    @Delete("delete from article where id = #{id}")
    void delete(Integer id);

    List<Article> listPublished(Integer categoryId, String keyword);

    @Select("select * from article where id = #{id} and state = '已发布'")
    Article findPublishedById(Integer id);

    @Update("update article set view_count = view_count + 1 where id = #{id} and state = '已发布'")
    int incrementViewCount(Integer id);

    @Select("select count(*) from article")
    Long countAll();

    @Select("select count(*) from article where state = '草稿'")
    Long countDrafts();

    @Select("select count(*) from article where state = '已发布'")
    Long countPublished();

    @Select("select coalesce(sum(view_count), 0) from article")
    Long sumViewCount();

    List<Map<String, Object>> publishTrend();

    List<Map<String, Object>> categoryDistribution();

    List<Article> hotArticles(Integer limit);
}