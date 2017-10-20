package com.rf.data.dto.filemanager;

/**
 * DTO para el rest de filemanager
 * @author mortas
 */
public class ResponseJson {

    private DataJson data;

    public DataJson getData() {
        return data;
    }

    public void setData(DataJson data) {
        this.data = data;
    }
}
