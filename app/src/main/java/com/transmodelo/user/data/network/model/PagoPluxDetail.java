package com.transmodelo.user.data.network.model;

import java.io.Serializable;

/**
 * Author: ehuiza
 * Created by: PagoPluxDetail on 08/06/2021
 */
public class PagoPluxDetail implements Serializable {
    private final int amount;
    private final String cardInfo;
    private final String cardIssuer;
    private final String cardType;
    private final String clientID;
    private final String clientName;
    private final String fecha;
    private final String idTransaccion;
    private final String state;
    private final String tipoPago;
    private final String token;

    public PagoPluxDetail(int amount, String cardInfo, String cardIssuer, String cardType, String clientID, String clientName, String fecha, String idTransaccion, String state, String tipoPago, String token) {
        this.amount = amount;
        this.cardInfo = cardInfo;
        this.cardIssuer = cardIssuer;
        this.cardType = cardType;
        this.clientID = clientID;
        this.clientName = clientName;
        this.fecha = fecha;
        this.idTransaccion = idTransaccion;
        this.state = state;
        this.tipoPago = tipoPago;
        this.token = token;
    }

    public final int getAmount() {
        return amount;
    }

    public final String getCardInfo() {
        return cardInfo;
    }

    public final String getCardIssuer() {
        return cardIssuer;
    }

    public final String getCardType() {
        return cardType;
    }

    public final String getClientID() {
        return clientID;
    }

    public final String getClientName() {
        return clientName;
    }

    public final String getFecha() {
        return fecha;
    }

    public final String getIdTransaccion() {
        return idTransaccion;
    }

    public final String getState() {
        return state;
    }

    public final String getTipoPago() {
        return tipoPago;
    }

    public final String getToken() {
        return token;
    }
}