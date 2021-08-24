package org.jzz.mybatistest.mapper;

import org.apache.ibatis.annotations.Select;
import org.jzz.mybatistest.entity.MyBean;

public interface TestMapper {

    @Select("select * from Kustom.dbo.test where id = #{id}")
    MyBean selectTest(int id);
}
