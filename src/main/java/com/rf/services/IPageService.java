package com.rf.services;

import com.rf.data.entities.Page;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface IPageService {

    Iterable<Page> listAllPages();

    Page getPageById(Integer id);

    Page savePage(Page page);

    void deletePage(Integer id);

    Iterable<Page> listAllPagesByLoggedInUser();
}
