package com.duoec.commons.test.reflenction;

import com.duoec.commons.pojo.house.House;
import com.duoec.commons.mongo.reflection.ReflectionUtils;
import com.duoec.commons.mongo.reflection.dto.ClassMate;
import org.junit.Test;

/**
 * Created by ycoe on 17/1/6.
 */
public class ReflectionUtilsTest {
    @Test
    public void analyseTest(){
        Class clazz = House.class;

        ReflectionUtils.analyse(clazz);

        ClassMate classMate = ReflectionUtils.getClassMate(clazz);
        System.out.println(classMate);
    }
}
