package com.rf.controllers;

import com.rf.data.entities.Element;
import com.rf.data.entities.ElementMetaData;
import com.rf.data.entities.Media;
import com.rf.data.entities.PageMetadata;
import com.rf.data.entities.SiteMetadata;
import com.rf.data.entities.filemanager.Folder;
import com.rf.data.enums.EnumMetadata;
import com.rf.data.enums.EnumScope;
import com.rf.data.enums.EnumStorage;
import com.rf.data.enums.EnumWhere;
import com.rf.data.repositories.FileManager.FileRepository;
import com.rf.data.repositories.FileManager.FolderRepository;
import com.rf.filemanager.FileManager;
import com.rf.services.ElementMetaDataService;
import com.rf.services.ElementService;
import com.rf.services.MediaService;
import com.rf.services.PageMetaDataService;
import com.rf.services.SiteMetadataService;
import com.rf.services.filemanager.FileService;
import com.rf.services.filemanager.FolderService;
import java.util.Objects;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ElementController {

    private ElementService elementService;

    private ElementMetaDataService elementMetaDataService;

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
    private SiteMetadataService siteMetadataService;
    private MediaService mediaService;
    private PageMetaDataService pageMetadataService;

    @Autowired
    public void setElementService(ElementService elementService) {
        this.elementService = elementService;
    }

    @Autowired
    public void setElementMetaDataRepository(ElementMetaDataService elementMetaDataService) {
        this.elementMetaDataService = elementMetaDataService;
    }

    

    @Autowired
    public void setSiteMetadataService(SiteMetadataService siteMetadataService) {
        this.siteMetadataService = siteMetadataService;
    }

    @Autowired
    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Autowired
    public void setPageMetaDataService(PageMetaDataService pageMetadataService) {
        this.pageMetadataService = pageMetadataService;
    }
    
    
    /**
     * Este método añade al modelo todos los datos necesarios para la generación de un nuevo elemento
     * @param model Modelo que inserta Spring automáticamente
     * @return ruta a la plantilla de elementform
     */
    @RequestMapping("element/new")
    public String newElement(Model model) {
        model.addAttribute("element", new Element());
        model.addAttribute("elementMetaData", new ElementMetaData());
        model.addAttribute("enumMetadata", EnumMetadata.values());
        model.addAttribute("enumScopes", EnumScope.values());
        model.addAttribute("enumWhere", EnumWhere.values());
        model.addAttribute("enumStorage", EnumStorage.values());
        model.addAttribute("allElements", elementService.listAllElements());
        return "elements/elementform";
    }

    /**
     * Este método añade al modelo todos los datos necesarios para la edición de un elemento existente
     * @param model Modelo que inserta Spring automáticamente
     * @param id Id del elemento a editar
     * @return ruta a la plantilla de elementform
     */
    @RequestMapping("element/edit/{id}")
    public String editElement(Model model, @PathVariable Integer id) {
        Element e = elementService.getCustomElementById(id);

        model.addAttribute("element", e);
        model.addAttribute("elementMetaData", elementMetaDataService.listAllMetaDataOfElement(e));
        model.addAttribute("enumMetadata", EnumMetadata.values());
        model.addAttribute("enumScopes", EnumScope.values());
        model.addAttribute("enumWhere", EnumWhere.values());
        model.addAttribute("enumStorage", EnumStorage.values());
        model.addAttribute("allElements", elementService.listAllElements());
        return "elements/elementform";
    }

    /**
     * Este método elimina uno de los metadatos de un elemento
     * @param model Modelo que inserta Spring automáticamente
     * @param id Id del elemento del que se va a eliminar el metadato (no utilizado)
     * @param metadataid id del metadata a eliminar
     * @return vacío si OK
     */
    @RequestMapping("element/deletemetadata/{id}/{metadataid}")
    @ResponseBody
    public String delMetadata(Model model, @PathVariable Integer id, @PathVariable Integer metadataid) {

        elementMetaDataService.deleteElementMetadata(metadataid);

        return "";
    }

    /**
     * Este método elimina una de las dependencias de un elemento
     * @param model Modelo que inserta Spring automáticamente
     * @param id Id del elemento del que se va a eliminar la dependencia
     * @param dependencyId Id del elemento que es la dependencia del anterior
     * @return vacío si OK
     */
    
    @RequestMapping("element/deletedependency/{id}/{dependencyId}")
    @ResponseBody
    public String delDependency(Model model, @PathVariable Integer id, @PathVariable Integer dependencyId) {

        Element e = elementService.getCustomElementById(id);
        Element dependencyToRemove = null;
        for (Element dependency : e.getDependencies()) {
            if (dependency.getId() == dependencyId) {
                dependency.getDependents().remove(e);
                dependencyToRemove = dependency;
            }
        }
        if (dependencyToRemove != null) {
            e.getDependencies().remove(dependencyToRemove);
        }

        elementService.saveElement(e);
        elementService.saveElement(dependencyToRemove);

        return "";
    }
    
    
    /**
     * Este método guarda un elemento desde el formulario. Crea también una carpeta exclusiva para él
     * @param e Elemento a guardar
     * @return ruta a la edición del elemento recién creado
     */
    @RequestMapping(value = "element", method = RequestMethod.POST)
    public String saveElement(Element e) {

        for (ElementMetaData em : e.getMetaDatas()) {
            if (em.getKey() == EnumMetadata.HTML) {
                em.setValue(StringEscapeUtils.unescapeHtml4(em.getValue()));
            }

            em = elementMetaDataService.saveElementMetaData(em);
        }

        FileManager fm = new FileManager(folderService, fileService, folderRepository, fileRepository, uploads_directory, uploads_base_url);
        Folder f = fm.createOrGetFolderIfExists("/", "common");
        f = fm.createOrGetFolderIfExists(f.getName(), "ELEMENTS");
        f = fm.createOrGetFolderIfExists(f.getName(), "ELEMENT_" + e.getName().replaceAll(" ", "_"));
        e.setFileManagerRoute(f.getName());

        e = elementService.saveElement(e);

        return "redirect:/element/edit/" + e.getId();
    }

    /**
     * Este método devuelve la lista de elementos existentes
     * @param model modelo que inserta spring
     * @return ruta a la plantilla de mostrado de lista de elementos
     */
    @RequestMapping(value = "/elements", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("elements", elementService.listAllElements());
        return "elements/elements";
    }

    /**
     * Este método devuelve los datos correspondientes a un elemento
     * @param model Modelo que inserta Spring
     * @param id Id del elemento a mostrar
     * @return ruta a la plantilla de mostrado
     */
    @RequestMapping("element/{id}")
    public String list(Model model, @PathVariable Integer id) {
        model.addAttribute("element", elementService.getCustomElementById(id));
        return "elements/elementshow";
    }

    /**
     * Este método obtendrá el estado actual de un elemento, y buscará entre los sitemetadatas y los pagemetadats
     * todas las apariciones de este elemento, para a continuación actualizar todos los posibles campos. De esta manera,
     * se actualizan las referencias a los ficheros del metadata, permitiendo actualizarlos.
     * @param id Id del elemento que se actualizará
     * @return ruta a la plantilla de edición del elemento
     */
    @RequestMapping("element/updateme/{id}")
    public String updateElement(@PathVariable Integer id) {

        FileManager fm = new FileManager(folderService, fileService, folderRepository, fileRepository, uploads_directory, uploads_base_url);

        Folder fStorage = null;
        Element e = elementService.getCustomElementById(id);
        Iterable<SiteMetadata> smds = siteMetadataService.listAllSiteMetadata();
        Iterable<PageMetadata> pmds = pageMetadataService.listAllPageMetaData();
        Iterable<Media> medias = mediaService.listAllMedias();

        for (SiteMetadata smd : smds) {
            for (ElementMetaData emd : e.getMetaDatas()) {
                if (smd.getElementMetadata() != null && Objects.equals(smd.getElementMetadata().getId(), emd.getId())) {
                    Folder f = fm.createOrGetFolderIfExists("/", smd.getSite().getName());
                    fm.createOrGetFolderIfExists(f.getName(), "MEDIA");
                    fm.createOrGetFolderIfExists(f.getName(), "DOCS");
                    Folder fScripts = fm.createOrGetFolderIfExists(f.getName(), "SCRIPTS");
                    Folder fViews = fm.createOrGetFolderIfExists(f.getName(), "VIEWS");
                    Folder fCss = fm.createOrGetFolderIfExists(f.getName(), "CSS");

                    //Entonces es el mismo metadata, asín que intentamos actualizarlo
                    MetaDataSharedController.processSiteMetadata(smd, fStorage, fCss, fScripts, fViews, fm, mediaService, uploads_directory, true);
                }
            }
        }
        siteMetadataService.saveSiteMetadatas(smds);
        for (PageMetadata pmd : pmds) {
            for (ElementMetaData emd : e.getMetaDatas()) {
                if (emd != null
                        && pmd != null
                        && pmd.getElementMetadata() != null
                        && pmd.getElementMetadata().getId() != null
                        && Objects.equals(pmd.getElementMetadata().getId(), emd.getId())
                        && pmd.getPage() != null && pmd.getPage().getSite() != null) {
                    Folder f = fm.createOrGetFolderIfExists("/", pmd.getPage().getSite().getName());
                    fm.createOrGetFolderIfExists(f.getName(), "MEDIA");
                    fm.createOrGetFolderIfExists(f.getName(), "DOCS");
                    Folder fScripts = fm.createOrGetFolderIfExists(f.getName(), "SCRIPTS");
                    Folder fViews = fm.createOrGetFolderIfExists(f.getName(), "VIEWS");
                    Folder fCss = fm.createOrGetFolderIfExists(f.getName(), "CSS");

                    //Entonces es el mismo metadata, asín que intentamos actualizarlo
                    MetaDataSharedController.processPageMetadata(pmd, fStorage, fCss, fScripts, fViews, fm, mediaService, uploads_directory, true);
                }
            }
        }
        pageMetadataService.savePageMetadatas(pmds);

        return "redirect:/element/edit/" + e.getId();
    }

}
