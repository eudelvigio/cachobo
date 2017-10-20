package com.rf.services;

import com.rf.data.entities.Element;
import com.rf.data.entities.ElementMetaData;
import com.rf.data.enums.EnumMetadata;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface IElementMetaDataService {

    Iterable<ElementMetaData> listAllMetaDataOfElement(Element e);

    Iterable<ElementMetaData> listAllElementMetaData();

    ElementMetaData getElementMetaDataById(Integer id);

    ElementMetaData getMetaDataOfElementByKey(Element e, EnumMetadata key);

    ElementMetaData saveElementMetaData(ElementMetaData emd);

    void deleteElementMetadata(Integer siteMetadataid);
}
