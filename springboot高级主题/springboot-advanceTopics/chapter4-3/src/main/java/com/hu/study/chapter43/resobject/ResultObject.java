package com.hu.study.chapter43.resobject;

/**
 * <p>Title: ResultObject</p>
 * <p>Description: result for service</p>
 * @author yxg
 * @version 1.0
 */
public class ResultObject {

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /**
     * return code
     */
    private Integer errCode;

    /**
     * return message
     */
    private String errMsg;

    /**
     * return data
     */
    private String data;

    public class CreateUserResult{
        private String saicID;

        public String getSaicID() {
            return saicID;
        }

        public void setSaicID(String saicID) {
            this.saicID = saicID;
        }
    }

    public class CheckAccountResult{
        private Boolean existed;

        public Boolean getExisted() {
            return existed;
        }

        public void setExisted(Boolean existed) {
            this.existed = existed;
        }
    }
}