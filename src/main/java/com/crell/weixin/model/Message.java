package com.crell.weixin.model;

/**
 * 推送消息
 * Created by Administrator on 2016/3/24.
 */
public class Message {
    private Filter filter;

    private Text text;

    private String msgtype;

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }
}
