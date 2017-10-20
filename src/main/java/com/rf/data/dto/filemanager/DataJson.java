package com.rf.data.dto.filemanager;

/**
 * DTO para el rest de filemanager
 * @author mortas
 */
public class DataJson {

    private String id;

    private String type;

    private AttributesJson attributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AttributesJson getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributesJson attributes) {
        this.attributes = attributes;
    }

}
