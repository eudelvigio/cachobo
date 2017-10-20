package com.rf.data.dto.filemanager;

import java.util.List;
/**
 * DTO para el rest de filemanager
 * @author mortas
 */
public class ArrayResponseJson {

    private List<DataJson> data;

    public List<DataJson> getData() {
        return data;
    }

    public void setData(List<DataJson> data) {
        this.data = data;
    }
}
