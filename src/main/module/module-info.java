module net.dongliu.requests {
    requires java.logging;
    requires net.dongliu.commons;
    requires static gson;
    requires static fastjson;
    requires static com.fasterxml.jackson.annotation;
    requires static com.fasterxml.jackson.core;
    requires static com.fasterxml.jackson.databind;

    exports net.dongliu.requests;
    exports net.dongliu.requests.body;
    exports net.dongliu.requests.exception;
    exports net.dongliu.requests.json;
}