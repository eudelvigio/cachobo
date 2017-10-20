package com.rf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.Element;
import com.rf.data.entities.ElementMetaData;
import com.rf.data.enums.EnumMetadata;
import com.rf.data.repositories.ElementMetaDataRepository;

/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class ElementMetaDataService implements IElementMetaDataService {

    private ElementMetaDataRepository elementMetaDataRepository;

    @Autowired
    public void ElementMetaDataRepository(ElementMetaDataRepository elementMetaDataRepository) {
        this.elementMetaDataRepository = elementMetaDataRepository;
    }

    @Override
    public Iterable<ElementMetaData> listAllMetaDataOfElement(Element e) {
        return elementMetaDataRepository.findByElement(e);
    }

    @Override
    public ElementMetaData getMetaDataOfElementByKey(Element e, EnumMetadata key) {
        return elementMetaDataRepository.findByElementAndKey(e, key);
    }

    @Override
    public ElementMetaData getElementMetaDataById(Integer id) {
        return elementMetaDataRepository.findOne(id);
    }

    @Override
    public Iterable<ElementMetaData> listAllElementMetaData() {
        return elementMetaDataRepository.findAll();
    }

    @Override
    public ElementMetaData saveElementMetaData(ElementMetaData emd) {
        return elementMetaDataRepository.save(emd);
    }

    @Override
    public void deleteElementMetadata(Integer siteMetadataid) {
        elementMetaDataRepository.delete(siteMetadataid);
    }

}
