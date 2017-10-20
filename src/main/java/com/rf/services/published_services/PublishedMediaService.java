package com.rf.services.published_services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.published_entities.PublishedMedia;
import com.rf.data.entities.published_entities.PublishedSite;
import com.rf.data.repositories.published_repositories.PublishedMediaRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class PublishedMediaService implements IPublishedMediaService {

    private IPublishedSiteService siteService;

    @Autowired
    public void setSiteService(PublishedSiteService siteService) {
        this.siteService = siteService;
    }

    private PublishedMediaRepository mediaRepository;

    @Autowired
    public void setMediaRepository(PublishedMediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Iterable<PublishedMedia> listAllMedias() {
        return mediaRepository.findAll();
    }

    @Override
    public PublishedMedia getMediaById(Integer id) {
        return mediaRepository.findOne(id);
    }

    @Override
    public PublishedMedia saveMedia(PublishedMedia media) {
        if (media.getVersion() == null) {
            media.setVersion(0);
        }

        return mediaRepository.save(media);
    }

    @Override
    public void deleteMedia(Integer id) {
        mediaRepository.delete(id);

    }

    public void deleteMediaList(Iterable<PublishedMedia> medias) {
        mediaRepository.delete(medias);

    }

    @Override
    public PublishedMedia saveOrUpdateMediaByName(PublishedMedia media) {
        PublishedMedia mExistente = mediaRepository.findByNameAndSite(media.getName(), media.getSite());
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
    public PublishedMedia getMediaByNameAndSite(String name, PublishedSite s) {
        return mediaRepository.findByNameAndSite(name, s);
    }

    @Override
    public Iterable<PublishedMedia> listAllMediasByLoggedInUsers() {
        PublishedSite s = new PublishedSite();

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
    public Iterable<PublishedMedia> listAllMediasBySite(PublishedSite s) {
        return mediaRepository.findBySite(s);
    }

    @Override
    public PublishedMedia getMediaByParentMediaId(Integer id) {
        return mediaRepository.getByParentMediaId(id);
    }

}
