package com.rf.services.published_services;

import com.rf.data.entities.published_entities.PublishedMedia;
import com.rf.data.entities.published_entities.PublishedSite;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface IPublishedMediaService {

    Iterable<PublishedMedia> listAllMedias();

    Iterable<PublishedMedia> listAllMediasByLoggedInUsers();

    PublishedMedia getMediaById(Integer id);

    PublishedMedia getMediaByParentMediaId(Integer id);

    PublishedMedia saveMedia(PublishedMedia media);

    PublishedMedia saveOrUpdateMediaByName(PublishedMedia media);

    PublishedMedia getMediaByNameAndSite(String name, PublishedSite s);

    void deleteMedia(Integer id);

    Iterable<PublishedMedia> listAllMediasBySite(PublishedSite s);
}
