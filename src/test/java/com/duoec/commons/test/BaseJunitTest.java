package com.duoec.commons.test;

import com.duoec.commons.mongo.reflection.ClassUtils;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * BaseJunitTest
 *
 * @author ycoe
 * @date 2016/5/5
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring-context.xml")
public class BaseJunitTest {

    /**
     * 比较
     * @param doc1
     * @param doc2
     */
    protected void assertEquals(Object doc1, Object doc2) {
        Class<?> aClass = doc1.getClass();
        if(ClassUtils.isPrimitiveOrWrapper(aClass) || aClass == String.class) {
            //基本数据类型
            Assert.assertEquals(doc1, doc2);
        }else if(doc1 instanceof Map) {
            //是Map
            for (Object key : ((Map)doc1).keySet()) {
                assertEquals(((Map) doc1).get(key), ((Map) doc2).get(key));
            }
        }else if(doc1 instanceof List) {
            //是列表
            for (int i = 0; i < ((List)doc1).size(); i++) {
                Object doc11 = ((List)doc1).get(i);
                Object doc21 = ((List)doc2).get(i);
                assertEquals(doc11, doc21);
            }
        }else{
            System.out.println(doc1);
        }

    }
}
