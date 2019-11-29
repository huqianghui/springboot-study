package com.hu.study.chapter43.resobject;

/**
 * <p>Title: ResultMsgs</p>
 * <p>Description: 返回信息枚举类</p>
 * @author yxg
 * @version 1.0
 */
public enum ResultMsgs {

  SUCCESS_CODE(0, "ok"),
  USER_TEXISTS(1, "用户已存在"),
  BUSY_CODE(-1, "系统执行忙"),
  TABLE_EXISITS(-2, "表已存在"),
  ARGUMENTS_MUSTNOTBENULL(221101, "参数不能为空"),
  DATA_NOTEXISTS(-301211, "查询数据不存在"),
  CHANELCODE_NOCASE(-2,"渠道号不匹配"),
  USER_NOT_EXISTS(2, "用户不存在"),
  ERROR_PASSWORK(3, "密码错误"),
  IS_LOCKED(4, "当前用户已被锁定，请30分钟后尝试"),
  ARGUMENTS_ERROR(-301201, "参数有误"),
  TOKEN_ERROR(-100001, "token失效");

  private int code;
  private String msg;

  ResultMsgs(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }
}
