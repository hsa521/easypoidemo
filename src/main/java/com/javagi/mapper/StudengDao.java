package com.javagi.mapper;

import com.javagi.model.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface StudengDao {

    @Select("select * from student")
    List<Student> getAllStudents();

}
