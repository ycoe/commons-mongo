package com.duoec.commons.mongo;

import java.util.List;

/**
 * 带分页信息的
 * Created by ycoe on 16/5/19.
 */
public class Pagination<T> {
    private List<T> list;

    private long total;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
