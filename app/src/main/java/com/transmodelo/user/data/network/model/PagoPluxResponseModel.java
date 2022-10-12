package com.transmodelo.user.data.network.model;

import java.io.Serializable;

/**
 * Author: ehuiza
 * Created by: PagoPluxResponseModel on 08/06/2021
 */
public class PagoPluxResponseModel implements Serializable {
    private final int code;
    private final String description;
    private final PagoPluxDetail detail;
    private final String status;

    public PagoPluxResponseModel(int code, String description, PagoPluxDetail detail, String status) {
        this.code = code;
        this.description = description;
        this.detail = detail;
        this.status = status;
    }

    public final int getCode() {
        return code;
    }

    public final String getDescription() {
        return description;
    }

    public final PagoPluxDetail getDetail() {
        return detail;
    }

    public final String getStatus() {
        return status;
    }
}