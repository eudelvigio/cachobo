package com.rf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.Page;
import com.rf.data.entities.PageMetadata;
import com.rf.data.enums.EnumMetadata;
import com.rf.data.repositories.PageMetadataRepository;

/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class PageMetaDataService implements IPageMetaDataService {

    private PageMetadataRepository pageMetadataRepository;

    @Autowired
    public void ElementMetaDataRepository(PageMetadataRepository pageMetadataRepository) {
        this.pageMetadataRepository = pageMetadataRepository;
    }

    @Override
    public Iterable<PageMetadata> listAllMetaDataOfPage(Page p) {
        return pageMetadataRepository.findByPage(p);
    }

    @Override
    public Iterable<PageMetadata> listAllPageMetaData() {
        return pageMetadataRepository.findAll();
    }

    @Override
    public PageMetadata getPageMetaDataById(Integer id) {
        return pageMetadataRepository.findOne(id);
    }

    @Override
    public PageMetadata savePageMetadata(PageMetadata pm) {
        return pageMetadataRepository.save(pm);
    }

    @Override
    public Iterable<PageMetadata> savePageMetadatas(Iterable<PageMetadata> pms) {
        return pageMetadataRepository.save(pms);
    }

    @Override
    public Iterable<PageMetadata> listPageMetaDataByKeyAndValue(EnumMetadata key, String value) {
        return pageMetadataRepository.findByKeyAndValue(key, value);
    }

}
