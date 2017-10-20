package com.rf.data.enums;

/**
 * No ha sido usado nunca, deprecado
 * @author mortas
 * @deprecated
 */
@Deprecated
public enum EnumScope {
    PAGE(new Integer(1), "Página"),
    SITE(new Integer(2), "Sitio");

    private final Integer value;
    private final String description;

    EnumScope(Integer val, String description) {
        this.value = val;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getValue() {
        return this.value;
    }

}
