package com.rf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.Media;
import com.rf.data.entities.Site;
import com.rf.data.repositories.MediaRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class MediaService implements IMediaService {

    private ISiteService siteService;

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    private MediaRepository mediaRepository;

    @Autowired
    public void setMediaRepository(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Iterable<Media> listAllMedias() {
        return mediaRepository.findAll();
    }

    @Override
    public Media getMediaById(Integer id) {
        return mediaRepository.findOne(id);
    }

    @Override
    public Media saveMedia(Media media) {
        if (media.getVersion() == null) {
            media.setVersion(0);
        }

        return mediaRepository.save(media);
    }

    @Override
    public void deleteMedia(Integer id) {
        mediaRepository.delete(id);

    }

    @Override
    public Media saveOrUpdateMediaByName(Media media) {
        Media mExistente = mediaRepository.findByNameAndSite(media.getName(), media.getSite());
        if (mExistente != null) {

            mExistente.setMediaData(media.getMediaData());
            mExistente.setName(media.getName());
            mExistente.setVersion(media.getVersion());
            mExistente.setCreation(media.getCreation());
            mExistente.setDeleted(media.getDeleted());

            return mediaRepository.save(mExistente);
        } else {
            return mediaRepository.save(media);
        }
    }

    @Override
    @Deprecated
    public Media getMediaByNameAndSite(String name, Site s) {

        /* Media m = mediaRepository.findByName(name);
        
        for (Site sAsignado : m.getSitesOfMedia()) {
            if (sAsignado.getId().equals(s.getId())) {
                return m;
            }
        }
         */
        return null;
    }

    @Override
    public Iterable<Media> listAllMediasByLoggedInUsers() {
        Site s = new Site();

        Boolean bAccesoATodo = true;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                if (ga.getAuthority().contains("SITE:")) {
                    bAccesoATodo = false;
                    s = siteService.getSiteByName(ga.getAuthority().replace("SITE:", ""));
                }
            }
        } else {
            return null;
        }

        if (bAccesoATodo) {
            return mediaRepository.findAll();
        } else {
            return mediaRepository.findBySite(s);
        }

    }

    @Override
    public Iterable<Media> listAllMediasBySite(Site s) {
        return mediaRepository.findBySite(s);
    }

    @Override
    public Media getMediaByName(String name) {
        return mediaRepository.findByName(name);
    }

}
