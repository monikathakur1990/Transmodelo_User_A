package com.transmodelo.user.data.network.model;

import java.io.Serializable;

/**
 * Author: ftorrez
 * Created by: ModelGenerator on 5/12/21
 */
public class PagoPluxCardModel implements Serializable {

    /**
     * Datos requeridos
     */
    private final String payboxRemail;
    private final String payboxSendmail;
    private final String payboxRename;
    private final String payboxSendname;
    private final Boolean payboxProduction;
    private final String payboxLanguage;
    private final String payboxDirection;
    private final String payBoxClientPhone;
    private final String payBoxClientIdentification;
    private final String payboxIdPlan;
    private final String payboxEnvironment;
    private final String payboxDisplay;

    public PagoPluxCardModel(String payboxRemail, String payboxSendmail, String payboxRename, String payboxSendname, Boolean payboxProduction, String payboxLanguage, String payboxDirection, String payBoxClientPhone, String payBoxClientIdentification, String payboxIdPlan, String payboxEnvironment, String payboxDisplay) {
        this.payboxRemail = payboxRemail;
        this.payboxSendmail = payboxSendmail;
        this.payboxRename = payboxRename;
        this.payboxSendname = payboxSendname;
        this.payboxProduction = payboxProduction;
        this.payboxLanguage = payboxLanguage;
        this.payboxDirection = payboxDirection;
        this.payBoxClientPhone = payBoxClientPhone;
        this.payBoxClientIdentification = payBoxClientIdentification;
        this.payboxIdPlan = payboxIdPlan;
        this.payboxEnvironment = payboxEnvironment;
        this.payboxDisplay = payboxDisplay;
    }

    public String getPayboxRemail() {
        return payboxRemail;
    }

    public String getPayboxSendmail() {
        return payboxSendmail;
    }

    public String getPayboxRename() {
        return payboxRename;
    }

    public String getPayboxSendname() {
        return payboxSendname;
    }

    public Boolean getPayboxProduction() {
        return payboxProduction;
    }

    public String getPayboxLanguage() {
        return payboxLanguage;
    }

    public String getPayboxDirection() {
        return payboxDirection;
    }

    public String getPayBoxClientPhone() {
        return payBoxClientPhone;
    }

    public String getPayBoxClientIdentification() {
        return payBoxClientIdentification;
    }

    public String getPayboxIdPlan() {
        return payboxIdPlan;
    }

    public String getPayboxEnvironment() {
        return payboxEnvironment;
    }

    public String getPayboxDisplay() {
        return payboxDisplay;
    }
}