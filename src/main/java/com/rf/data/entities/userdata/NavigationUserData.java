package com.rf.data.entities.userdata;

/**
 * Esta entidad sirve para almacenar los datos de usuarios, tales como su ip, useragent
 * Pero no se avanzó mucho no
 * @author mortas
 */
public class NavigationUserData {

    private Integer id;

    private String ip;
    private String xForwardedIp;
    private String userAgent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getxForwardedIp() {
        return xForwardedIp;
    }

    public void setxForwardedIp(String xForwardedIp) {
        this.xForwardedIp = xForwardedIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
