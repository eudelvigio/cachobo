package com.rf.data.dto.blackbet;

import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * Clase DTO para intercambiar los datos de las rutas de la aplicación
 * @author mortas
 */
public class Route {

    private String route;
    private String templateUrl;

    @JsonIgnore
    private String fileRoute;

    public String getFileRoute() {
        return fileRoute;
    }

    public void setFileRoute(String fileRoute) {
        this.fileRoute = fileRoute;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }
}
