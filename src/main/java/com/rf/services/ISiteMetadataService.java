package com.rf.services;

import com.rf.data.entities.Site;
import com.rf.data.entities.SiteMetadata;
import com.rf.data.enums.EnumMetadata;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface ISiteMetadataService {

    Iterable<SiteMetadata> listAllMetadataOfSite(Site s);

    Iterable<SiteMetadata> listAllSiteMetadata();

    SiteMetadata getSiteMetadataById(Integer id);

    SiteMetadata saveSiteMetadata(SiteMetadata pm);

    Iterable<SiteMetadata> saveSiteMetadatas(Iterable<SiteMetadata> apm);

    Iterable<SiteMetadata> listSiteMetaDataByKeyAndValue(EnumMetadata key, String value);
}
