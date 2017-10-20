package com.rf.services;

import com.rf.data.entities.Media;
import com.rf.data.entities.Site;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface IMediaService {

    Iterable<Media> listAllMedias();

    Iterable<Media> listAllMediasBySite(Site s);

    Iterable<Media> listAllMediasByLoggedInUsers();

    Media getMediaById(Integer id);

    Media saveMedia(Media media);

    Media saveOrUpdateMediaByName(Media media);

    Media getMediaByNameAndSite(String name, Site s);

    Media getMediaByName(String name);

    void deleteMedia(Integer id);
}
