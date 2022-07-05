package com.lumyuan.filebrowser.pojo;

public class EditorConfigBean {
    private float textSize;

    public EditorConfigBean() {
    }

    public EditorConfigBean(float textSize) {
        this.textSize = textSize;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    @Override
    public String toString() {
        return "EditorConfigBean{" +
                "textSize=" + textSize +
                '}';
    }
}
