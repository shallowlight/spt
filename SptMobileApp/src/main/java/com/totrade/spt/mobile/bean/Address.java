package com.totrade.spt.mobile.bean;

import com.totrade.spt.mobile.common.AppConstant;

public class Address {

    public static final String android = "android";
    public static final String ios = "ios";

    private String prod;
    private String test;
    private String prodWeb;
    private String testWeb;

    public String getProd() {
        return prod;
    }

    public void setProd(String prod) {
        this.prod = prod;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getProdWeb() {
        return prodWeb;
    }

    public void setProdWeb(String prodWeb) {
        this.prodWeb = prodWeb;
    }

    public String getTestWeb() {
        return testWeb;
    }

    public void setTestWeb(String testWeb) {
        this.testWeb = testWeb;
    }

    public Address(String prod, String test, String prodWeb, String testWeb) {
        super();
        this.prod = prod;
        this.test = test;
        this.prodWeb = prodWeb;
        this.testWeb = testWeb;
    }

    public String getIP() {
        if (AppConstant.isRelease) {
            return prod.split(" ")[0];
        } else {
            return test.split(" ")[0];
        }
    }

    public String getPort() {
        if (AppConstant.isRelease) {
            return prod.split(" ")[1];
        } else {
            return test.split(" ")[1];
        }
    }

    public String getWebIP() {
        if (AppConstant.isRelease) {
            return prodWeb.split(" ")[0];
        } else {
            return testWeb.split(" ")[0];
        }
    }

    public String getWebPort() {
        if (AppConstant.isRelease) {
            return prodWeb.split(" ")[1];
        } else {
            return testWeb.split(" ")[1];
        }
    }

    @Override
    public String toString() {
        return "Address{" +
                "prod='" + prod + '\'' +
                ", test='" + test + '\'' +
                ", prodWeb='" + prodWeb + '\'' +
                ", testWeb='" + testWeb + '\'' +
                '}';
    }
}
