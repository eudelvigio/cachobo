package com.rf.data.entities.userdata;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Esta entidad mapearía al usuario, de manera que se pudiese trackear su uso a partir de distintos dispositivos y eso
 * @author mortas
 */
@Entity
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Date fc;
    private String firstAccessSessionToken;
    private String navigationSessionToken;
    private String currentUrl;
    private String ip;
    private String xForwardedIp;
    private String userAgent;

    @OneToOne(cascade = CascadeType.ALL)
    private MouseUserData mousePositions;

    @OneToOne(cascade = CascadeType.ALL)
    private MouseUserData clickOn;

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public Date getFc() {
        return fc;
    }

    public void setFc(Date fc) {
        this.fc = fc;
    }

    public String getFirstAccessSessionToken() {
        return firstAccessSessionToken;
    }

    public void setFirstAccessSessionToken(String firstAccessSessionToken) {
        this.firstAccessSessionToken = firstAccessSessionToken;
    }

    public String getNavigationSessionToken() {
        return navigationSessionToken;
    }

    public void setNavigationSessionToken(String navigationSessionToken) {
        this.navigationSessionToken = navigationSessionToken;
    }

    public MouseUserData getMousePositions() {
        return mousePositions;
    }

    public void setMousePositions(MouseUserData mousePositions) {
        this.mousePositions = mousePositions;
    }

    public MouseUserData getClickOn() {
        return clickOn;
    }

    public void setClickOn(MouseUserData clickOn) {
        this.clickOn = clickOn;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getxForwardedIp() {
        return xForwardedIp;
    }

    public void setxForwardedIp(String xForwardedIp) {
        this.xForwardedIp = xForwardedIp;
    }

}
