package com.transmodelo.user.data.network.util;

public enum ParamRequest {
    ADMIN_ENVIRONMENT("ppxpacaa"),
    ADMIN_DISPLAY("ppxpacaw"),
    ADMIN_LANGUAGE("ppxpacal"),
    ADMIN_PRODUCTION("ppxpacap"),
    ADMIN_ESTABLISHMENT("ppxpacae"),
    CLIENT_NAME("ppxpaccn"),
    CLIENT_EMAIL("ppxpaccm"),
    CLIENT_ADDRESS("ppxpaccd"),
    CLIENT_PLAN("ppxpacca"),
    CLIENT_IDENTIFICATION("ppxpacci"),
    CLIENT_REMAIL("ppxpaccr"),
    CLIENT_PHONE("ppxpaccp");

    private final String value;

    ParamRequest(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
