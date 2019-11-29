package com.hu.study.chapter43.resobject;


/**
 * @author likai
 * @Date 2018/11/21
 */
public class ResultModel<T> {

  private String errMsg;
  private Integer errCode;
  private T data;

  public String getErrMsg() {
    return errMsg;
  }

  public void setErrMsg(String errMsg) {
    this.errMsg = errMsg;
  }

  public Integer getErrCode() {
    return errCode;
  }

  public void setErrCode(Integer errCode) {
    this.errCode = errCode;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public static ResultModel getInstance(){
    return new ResultModel();
  }
}
