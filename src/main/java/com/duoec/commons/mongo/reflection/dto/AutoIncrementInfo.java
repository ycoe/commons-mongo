package com.duoec.commons.mongo.reflection.dto;

import com.duoec.commons.mongo.annotation.AutoIncrement;

/**
 * Created by ycoe on 16/5/4.
 */
public class AutoIncrementInfo {
    private AutoIncrement autoIncrement;

    private boolean isInteger = false;

    public boolean isInteger() {
        return isInteger;
    }

    public void setInteger(boolean integer) {
        isInteger = integer;
    }

    public AutoIncrement getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(AutoIncrement autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isAutoIncrement() {
        return autoIncrement != null;
    }
}
