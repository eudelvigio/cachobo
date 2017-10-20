package com.rf.services;

import com.rf.data.entities.Page;
import com.rf.data.entities.PageMetadata;
import com.rf.data.enums.EnumMetadata;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface IPageMetaDataService {

    Iterable<PageMetadata> listAllMetaDataOfPage(Page p);

    Iterable<PageMetadata> listAllPageMetaData();

    PageMetadata getPageMetaDataById(Integer id);

    PageMetadata savePageMetadata(PageMetadata pm);

    Iterable<PageMetadata> savePageMetadatas(Iterable<PageMetadata> pm);

    Iterable<PageMetadata> listPageMetaDataByKeyAndValue(EnumMetadata key, String value);

}
