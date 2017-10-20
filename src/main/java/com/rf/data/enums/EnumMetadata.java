package com.rf.data.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Los distintos tipos de metadatos que se representan
 * @author mortas
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EnumMetadata {

    JS(1, "Script JS", ".js"),
    CSS(2, "Link CSS", ".css"),
    HTML(3, "HTML", ".html"),
    SERVICE(4, "Servicio", "");//deprecated

    private final String description;
    private final Integer value;
    private final String extension;
    //private final Boolean canBeMultiple;

    EnumMetadata(Integer val, String desc, String ext) {
        this.value = val;
        this.description = desc;
        this.extension = ext;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getDescription() {
        return description;
    }

    public String getExtension() {
        return extension;
    }

    /*public Boolean getCanBeMultiple() {
		return canBeMultiple;
	}*/
}
