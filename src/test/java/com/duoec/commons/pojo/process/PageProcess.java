package com.duoec.commons.pojo.process;

/**
 * Created by ycoe on 16/7/1.
 */
public class PageProcess {
    /**
     * 页数
     */
    private int page;

    /**
     * 状态: 0未完成 1已完成
     */
    private int status;

    public PageProcess(){}

    public PageProcess(int pageNo) {
        this.page = pageNo;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
