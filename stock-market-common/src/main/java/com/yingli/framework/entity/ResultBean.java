package com.yingli.framework.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 返回结果实体
 * @param <T>
 */
@ApiModel(value="返回参数",description="返回参数")
public class ResultBean<T> implements Serializable {
	  
	private static final long serialVersionUID = 1L;
	public static final int FAIL = 0;//失败
	public static final int SUCCESS = 1;//成功

	public static final int NO_PERMISSION = 2;//没有权限

	//成员变量
	@ApiModelProperty(value="返回消息,默认success",name="msg",example="success")
	private String msg = "success";//结果信息，默认success
	@ApiModelProperty(value="返回代码1:成功，0：失败",name="code",example="0")
	private int code = SUCCESS;//状态码，默认为SUCCESS（1）
	@ApiModelProperty(value="返回数据,json数据",name="data",example="Json")
	private T data;//需要返回的数据
	@ApiModelProperty(value="返回时间戳",name="timestamp")
	private long timestamp;//时间戳

	//构造函数
	public ResultBean() {
	    super();
		this.setTimestamp(System.currentTimeMillis());
	}
	
	public ResultBean(T data) {
	    this();
	    this.data = data;
	}
	
	public ResultBean(Throwable e) {
	    this();
	    this.msg = e.toString();
	    this.code = FAIL ;
	}

    public ResultBean(int code) {
		this();
        this.code = code;
    }

    public ResultBean(int code, T data) {
		this();
		this.code = code;
        this.data = data;
    }

    public ResultBean(String msg, T data, int code) {
		this();
    	this.msg = msg;
        this.data = data;
        this.code = code;
    }

    public ResultBean(int code, String msg) {
		this();
        this.code = code;
        this.msg = msg;
    }

	
	//getter和setter
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public T getData() {
		return data;
	}
	
	public void setData(T data) {
		this.data = data;
	}
	
	//初始化方法
	public static ResultBean ok() {
        return new ResultBean(SUCCESS);
    }

    public static <T> ResultBean ok(T data) {
        return new ResultBean(SUCCESS, data);
    }

	public static <T> ResultBean ok(T data, String msg) {
		ResultBean resultBean = new ResultBean(SUCCESS, data);
		resultBean.setMsg(msg);
		return resultBean;
	}

    public static ResultBean ok(String msg) {
        return new ResultBean(SUCCESS, msg);
    }

    public static ResultBean fail() {
        return new ResultBean(FAIL);
    }

    public static ResultBean fail(String msg) {
        return new ResultBean(FAIL, msg);
    }

    public static ResultBean fail(int code) {
        return new ResultBean(code);
    }

    public static<T> ResultBean fail(T data) {
        return new ResultBean("fail", data,FAIL);
    }

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}