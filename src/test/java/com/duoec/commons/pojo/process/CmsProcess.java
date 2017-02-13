package com.duoec.commons.pojo.process;

import java.util.List;

/**
 * Created by ycoe on 16/6/21.
 */
public class CmsProcess {
    /**
     * ID
     */
    private String id;

    /**
     * 任务代码,同一代码的任务不可重复添加
     */
    private String code;

    /**
     * 模块名称
     */
    private String modelName;

    /**
     * 标题
     */
    private String title;

    /**
     * 总任务量
     */
    private int total;

    /**
     * 当前任务完成量
     */
    private int current;

    /**
     * 状态: -1=已取消 0=未完成 1=已完成
     */
    private int status;

    /**
     * 信息,可以存放错误日志
     */
    private String message;

    /**
     * 存放任务的请求数据
     */
    private String data;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 更新时间
     */
    private long updateTime;

    /**
     * 分页信息
     */
    private List<PageProcess> pages;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public List<PageProcess> getPages() {
        return pages;
    }

    public void setPages(List<PageProcess> pages) {
        this.pages = pages;
    }
}
