package com.rf.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.rf.data.entities.Element;
import com.rf.data.entities.Page;
import com.rf.data.entities.PageMetadata;
import com.rf.data.entities.filemanager.Folder;
import com.rf.data.repositories.FileManager.FileRepository;
import com.rf.data.repositories.FileManager.FolderRepository;
import com.rf.filemanager.FileManager;
import com.rf.services.ElementMetaDataService;
import com.rf.services.ElementService;
import com.rf.services.MediaService;
import com.rf.services.PageMetaDataService;
import com.rf.services.PageService;
import com.rf.services.SiteService;
import com.rf.services.filemanager.FileService;
import com.rf.services.filemanager.FolderService;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Este controlador contiene los métodos para la gestión de las páginas, incluyendo CRUD de las mismas, proceso de sus metadatas y eliminación de los mismos
 * @author mortas
 */
@Controller
public class PageController {

    private PageService pageService;
    private ElementService elementService;
    private PageMetaDataService pageMetadataService;

    private SiteService siteService;

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    @Autowired
    public void setPageService(PageService pageService) {
        this.pageService = pageService;
    }

    @Autowired
    public void setElementService(ElementService elementService) {
        this.elementService = elementService;
    }

    @Autowired
    public void setPageMetadataService(PageMetaDataService pageMetadataService) {
        this.pageMetadataService = pageMetadataService;
    }

    private MediaService mediaService;

    @Autowired
    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private FolderRepository folderRepository;

    @Value("${uploads_directory}")
    private String uploads_directory;

    @Value("${uploads_base_url}")
    private String uploads_base_url;

    @RequestMapping("page/new")
    public String newPage(Model model) {
        model.addAttribute("page", new Page());

        model.addAttribute("elements", elementService.listAllElements());

        model.addAttribute("existing_sites", siteService.listAllSites());

        return "pages/pageform";
    }

    @RequestMapping("page/{id}")
    public String showProduct(@PathVariable Integer id, Model model) {
        model.addAttribute("page", pageService.getPageById(id));
        return "pages/pageshow";
    }

    @RequestMapping(value = "/pages", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("pages", pageService.listAllPagesByLoggedInUser());
        return "pages/pages";
    }

    @RequestMapping("page/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Page p = pageService.getPageById(id);
        model.addAttribute("page", p);

        Iterable<Element> els = elementService.listAllElements();
        model.addAttribute("elements", els);

        model.addAttribute("existing_sites", siteService.listAllSites());

        return "pages/pageform";
    }

    @RequestMapping(value = "page", method = RequestMethod.POST)
    public String savePage(Page p, HttpServletRequest request) {

        FileManager fm = new FileManager(folderService, fileService, folderRepository, fileRepository, uploads_directory, uploads_base_url);
        Folder f = fm.createOrGetFolderIfExists("/", p.getSite().getName());
        fm.createOrGetFolderIfExists(f.getName(), "MEDIA");
        fm.createOrGetFolderIfExists(f.getName(), "DOCS");
        Folder fScripts = fm.createOrGetFolderIfExists(f.getName(), "SCRIPTS");
        Folder fViews = fm.createOrGetFolderIfExists(f.getName(), "VIEWS");
        Folder fCss = fm.createOrGetFolderIfExists(f.getName(), "CSS");

        Folder fStorage = null;

        for (PageMetadata pmd : p.getMetaDatas()) {
            MetaDataSharedController.processPageMetadata(pmd, fStorage, fCss, fScripts, fViews, fm, mediaService, uploads_directory);
            pageMetadataService.savePageMetadata(pmd);
        }
        //p.setContent(StringEscapeUtils.unescapeHtml4(p.getContent()));
        p = pageService.savePage(p);

        return "redirect:/page/edit/" + p.getId();
    }

    @RequestMapping("page/delete/{id}")
    public String delete(@PathVariable Integer id) {
        pageService.deletePage(id);
        return "redirect:/pages/pages";
    }

    @RequestMapping("page/deletemetadata/{id}")
    public String deletemetadata(@PathVariable Integer id, @RequestParam(value = "metadatasIds") Integer[] metadatasIds) {
        Page p = pageService.getPageById(id);
        List<PageMetadata> pmdABorrar = new ArrayList<>();
        for (PageMetadata pmd : p.getMetaDatas()) {
            for (Integer possibleMetadataId : metadatasIds) {
                if (pmd.getId().equals(possibleMetadataId)) {
                    pmdABorrar.add(pmd);
                }
            }
        }

        for (PageMetadata pmd : pmdABorrar) {

            p.getMetaDatas().remove(pmd);
            pmd.setPage(null);
            pageMetadataService.savePageMetadata(pmd);

        }

        pageService.savePage(p);

        return "redirect:/page/edit/" + p.getId();
    }

}
