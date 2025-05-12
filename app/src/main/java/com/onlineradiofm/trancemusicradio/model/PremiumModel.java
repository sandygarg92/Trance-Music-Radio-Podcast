package com.onlineradiofm.trancemusicradio.model;


import com.onlineradiofm.trancemusicradio.ypylibs.model.AbstractModel;

/**
 * @author:Radio Polska
 * @Skype:
 * @Mobile :
 * @Email:
 * @Website: http://radiopolska.com
 * Created by radiopolska on 8/20/18.
 */
public class PremiumModel extends AbstractModel {

    public static final int STATUS_BTN_BUY=1;
    public static final int STATUS_BTN_PURCHASED=2;
    public static final int STATUS_BTN_SKIP=3;

    private int resId;

    private String productId;
    private String price;
    private String duration;

    private String info1;
    private String transId;
    private String labelBtnBuy;

    private int statusBtn=STATUS_BTN_BUY;

    public PremiumModel(long id, String name, String productId, int resId) {
        super(id, name, null);
        this.productId = productId;
        this.resId = resId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }


    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getLabelBtnBuy() {
        return labelBtnBuy;
    }

    public void setLabelBtnBuy(String labelBtnBuy) {
        this.labelBtnBuy = labelBtnBuy;
    }

    public int getStatusBtn() {
        return statusBtn;
    }

    public void setStatusBtn(int statusBtn) {
        this.statusBtn = statusBtn;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

}
