package com.duoec.commons.mongo.codec;

import com.duoec.commons.mongo.reflection.dto.AutoIncrementInfo;

/**
 * Created by ycoe on 16/6/28.
 */
@FunctionalInterface
public interface AutoIncrementFun {
    /**
     * 自增ID,返回int或long型,根据字段的类型
     * @param autoIncrementInfo
     * @return
     */
    Object getNextSequence(AutoIncrementInfo autoIncrementInfo);
}
