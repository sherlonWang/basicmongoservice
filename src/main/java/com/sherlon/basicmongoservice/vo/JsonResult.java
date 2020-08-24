package com.sherlon.basicmongoservice.vo;

import java.io.Serializable;

/**
 * @author :  sherlonWang
 * @description :  前后台交互返回结果类
 * @date: 2020-03-02
 */
public class JsonResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int SUCCESS = 0;
    private static final int ERROR = 1;

    private int state;
    private String msg;
    private T data;

    public JsonResult(){
        this.state = SUCCESS;
        this.msg = "ok";
    }

    public JsonResult(T data){
        this.state = SUCCESS;
        this.msg = "ok";
        this.data = data;
    }

    public JsonResult(Throwable e){
        this.state = ERROR;
        this.msg = e.getMessage();
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
