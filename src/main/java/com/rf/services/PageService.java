package com.rf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.Page;
import com.rf.data.entities.Site;
import com.rf.data.repositories.PageRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class PageService implements IPageService {

    private PageRepository pageRepository;

    @Autowired
    public void setProductRepository(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    private ISiteService siteService;

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    @Override
    public Iterable<Page> listAllPages() {
        return pageRepository.findAll();
    }

    @Override
    public Iterable<Page> listAllPagesByLoggedInUser() {
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
            return pageRepository.findAll();
        } else {
            return pageRepository.findBySite(s);
        }

    }

    @Override
    public Page getPageById(Integer id) {
        return pageRepository.findOne(id);
    }

    @Override
    public Page savePage(Page page) {
        if (page.getVersion() == null) {
            page.setVersion(0);
        }

        return pageRepository.save(page);
    }

    @Override
    public void deletePage(Integer id) {
        pageRepository.delete(id);

    }

}
