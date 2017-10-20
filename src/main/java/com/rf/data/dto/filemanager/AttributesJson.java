package com.rf.data.dto.filemanager;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/**
 * DTO para el rest de filemanager
 * @author mortas
 */

public class AttributesJson {

    private String name;

    private String extension;

    private String path;

    //tiene que llamarse protected
    private Integer protectedField;

    private String created;

    private String modified;

    private Long timestamp;

    private Integer width;

    private Integer height;

    private String bytes;

    private String[] capabilities;

    private Object[] config;

    private String content;

    private Integer bbddID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @JsonSerialize(as = String.class)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getProtectedField() {
        return protectedField;
    }

    public void setProtectedField(Integer protectedField) {
        this.protectedField = protectedField;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }

    public String[] getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String[] capabilities) {
        this.capabilities = capabilities;
    }

    public Object[] getConfig() {
        return config;
    }

    public void setConfig(Object[] config) {
        this.config = config;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getBbddID() {
        return bbddID;
    }

    public void setBbddID(Integer bbddID) {
        this.bbddID = bbddID;
    }

}
