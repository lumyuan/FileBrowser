package com.lumyuan.androidfilemanager.dao;

public class FileSettingsBean {
    private int order;
    private int inverseOrder;

    public FileSettingsBean() {
    }

    public FileSettingsBean(int order, int inverseOrder) {
        this.order = order;
        this.inverseOrder = inverseOrder;
    }

    public int getInverseOrder() {
        return inverseOrder;
    }

    public void setInverseOrder(int inverseOrder) {
        this.inverseOrder = inverseOrder;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "FileSettingsBean{" +
                "order=" + order +
                ", inverseOrder=" + inverseOrder +
                '}';
    }
}
