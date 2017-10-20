package com.rf.controllers;

import com.rf.data.dto.blackbet.Route;
import com.rf.data.entities.Element;
import com.rf.data.entities.ElementMetaData;
import com.rf.data.entities.Media;
import com.rf.data.entities.Page;
import com.rf.data.entities.PageMetadata;
import com.rf.data.entities.Site;
import com.rf.data.entities.SiteMetadata;
import com.rf.data.entities.SiteSensitiveDatas;
import com.rf.data.entities.auth.Role;
import com.rf.data.entities.auth.User;
import com.rf.data.entities.filemanager.Folder;
import com.rf.data.entities.live_config.LiveConfig;
import com.rf.data.entities.published_entities.PublishedMedia;
import com.rf.data.entities.published_entities.PublishedPage;
import com.rf.data.entities.published_entities.PublishedPageMetadata;
import com.rf.data.entities.published_entities.PublishedSite;
import com.rf.data.entities.published_entities.PublishedSiteMetadata;
import com.rf.data.enums.EnumMetadata;
import com.rf.data.enums.EnumScope;
import com.rf.data.enums.EnumWhere;
import com.rf.data.repositories.FileManager.FileRepository;
import com.rf.data.repositories.FileManager.FolderRepository;
import com.rf.filemanager.FileManager;
import com.rf.services.ElementMetaDataService;
import com.rf.services.ElementService;
import com.rf.services.ISiteService;
import com.rf.services.MediaService;
import com.rf.services.SiteMetadataService;
import com.rf.services.SiteSensitiveDatasService;
import com.rf.services.SiteService;
import com.rf.services.auth.RoleServiceImpl;
import com.rf.services.auth.UserServiceImpl;
import com.rf.services.filemanager.FileService;
import com.rf.services.filemanager.FolderService;
import com.rf.services.live_config.LiveConfigService;
import com.rf.services.published_services.CachedMediaService;
import com.rf.services.published_services.IPublishedSiteService;
import com.rf.services.published_services.PublishedMediaService;
import com.rf.services.published_services.PublishedSiteService;
import com.rf.shared.SharedFileUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
//import org.tmatesoft.svn.core.wc.ISVNOptions;
//import org.tmatesoft.svn.core.wc.SVNClientManager;

@Controller
public class SiteController {

    private ISiteService siteService;


    private CachedMediaService cachedMediaService;


    private ElementService elementService;


    private SiteMetadataService siteMetadataService;


    private SiteSensitiveDatasService siteSensitiveDatasService;


    private IPublishedSiteService publishedSiteService;

    private PublishedMediaService publishedMediaService;

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
    public String uploads_base_url;

    @Value("${bo_base_url}")
    private String bo_base_url;

    private EntityManager entityManager;


    private LiveConfigService liveConfigService;


    private MediaService mediaService;


    private UserServiceImpl userService;
    private RoleServiceImpl roleService;
    @Autowired
    private TemplateEngine htmlTemplateEngine;
    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
    @Autowired
    public void setCachedMediaService(CachedMediaService cachedMediaService) {
        this.cachedMediaService = cachedMediaService;
    }
    @Autowired
    public void setElementService(ElementService elementService) {
        this.elementService = elementService;
    }
    @Autowired
    public void setSiteMetadataService(SiteMetadataService siteMetadataService) {
        this.siteMetadataService = siteMetadataService;
    }
    @Autowired
    public void setSiteSensitiveDatasService(SiteSensitiveDatasService siteSensitiveDatasService) {
        this.siteSensitiveDatasService = siteSensitiveDatasService;
    }
    @Autowired
    public void setPublishedSiteService(PublishedSiteService publishedSiteService) {
        this.publishedSiteService = publishedSiteService;
    }
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    @Autowired
    public void setLiveConfigService(LiveConfigService liveConfigService) {
        this.liveConfigService = liveConfigService;
    }
    @Autowired
    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleServiceImpl roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setPublishedMediaService(PublishedMediaService publishedMediaService) {
        this.publishedMediaService = publishedMediaService;
    }
    /**
     * Este método privado se encarga de crear un nuevo usuario administrador para el sitio, al que le asigna 
     * el rol SITE_ADMIN, y crea otro rol que también asigna de nombre SITE:<siteName>
     * @param siteName nombre del sitio que se está creando que llama a este método
     */
    private void createAdminUserForSite(String siteName) {
        User user1 = new User();
        user1.setUsername(siteName + "_admin");
        user1.setPassword("abbyl40L");
        userService.saveOrUpdate(user1);

        Role siteRole = new Role();
        siteRole.setRole("SITE:" + siteName);
        roleService.saveOrUpdate(siteRole);

        List<Role> roles = (List<Role>) roleService.listAll();
        List<User> users = (List<User>) userService.listAll();

        roles.forEach(role -> {
            if (role.getRole().equalsIgnoreCase("SITE:" + siteName) || role.getRole().equalsIgnoreCase("SITE_ADMIN")) {
                users.forEach(user -> {
                    if (user.getUsername().equals(siteName + "_admin")) {
                        user.addRole(role);
                        userService.saveOrUpdate(user);
                    }
                });
            }
        });
    }

    /**
     * Método que se encarga de añadir a modelo los datos necesarios para ir a la plantilla de creación de sitio
     * @param model Modelo que inserta spring
     * @return ruta a la template de formulario
     */
    @RequestMapping("site/new")
    public String newSite(Model model) {
        model.addAttribute("site", new Site());
        model.addAttribute("elements", elementService.listAllElements());

        model.addAttribute("enumMetadata", EnumMetadata.values());
        model.addAttribute("enumScopes", EnumScope.values());
        return "sites/siteform";
    }

    /**
     * Método que devuelve la página de visión del sitio, la cual incluye la gestión de los liveconfigs (cortinillas)
     * @param id id del sitio
     * @param model Modelo que inserta spring
     * @return ruta a la plantilla de mostrado del sitio
     */
    @RequestMapping("site/{id}")
    public String showSite(@PathVariable Integer id, Model model) {

        Site site = siteService.getSiteById(id);

        model.addAttribute("site", site);

        Iterable<LiveConfig> liveConfigs = liveConfigService.getLiveConfigsBySite(site);
        for (LiveConfig lc : liveConfigs) {
            lc.setSite(null);
        }
        model.addAttribute("liveConfigs", liveConfigs);

        return "sites/siteshow";
    }

    /**
     * Método para guardar los liveconfigs (cortinillas) de un sitio
     * @param liveConfigs valores de liveconfigs
     * @return nada si OK
     */
    @RequestMapping(value = "site/configs", method = RequestMethod.POST)
    @ResponseBody
    public String liveConfigSiteSave(@RequestBody final LiveConfig[] liveConfigs) {
        for (LiveConfig lc : liveConfigs) {
            if (lc.getSite_id() != null) {
                lc.setSite(siteService.getSiteById(lc.getSite_id()));
            }

            LiveConfig lcAux = liveConfigService.getLiveConfigBySiteAndKey(lc.getSite(), lc.getKey());

            if (lcAux != null) {
                lcAux.setValue(lc.getValue());
                liveConfigService.saveLiveConfig(lcAux);
            } else {
                liveConfigService.saveLiveConfig(lc);
            }
        }
        return "";
    }

    /**
     * Método que devuelve la lista de sitios en la aplicación
     * @param model Modelo que inserta spring
     * @return 
     */
    @RequestMapping(value = "/sites", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("sites", siteService.listAllSites());
        return "sites/sites";
    }

    /**
     * Método Método para obtener los datos de un sitio para editar
     * @param id ID del sitio a editar
     * @param model Modelo que inserta Spring
     * @param revNumber número de revisión a devolver
     * @return ruta a la plantilla del formulario, con el modelo ya rellenado
     */
    @RequestMapping("site/edit/{id}")
    public String edit(@PathVariable Integer id, Model model, @RequestParam(value = "revNumber", required = false) Integer revNumber) {
        Comparator<SiteMetadata> mapComparator = Comparator.nullsLast((SiteMetadata o1, SiteMetadata o2) -> {
            if (o1.getOrder() == null || o2.getOrder() == null) {
                return 0;
            }
            return o1.getOrder().compareTo(o2.getOrder());
        });
        Site s = siteService.getSiteById(id);

        //Y ahora las revisiones            
        AuditReader reader = AuditReaderFactory.get(entityManager);

        List<Number> revisions = reader.getRevisions(Site.class, id);
        if (revNumber != null) {
            reader = AuditReaderFactory.get(entityManager);

            s = reader.find(Site.class, id, revNumber);

            model.addAttribute("rev", "[" + revNumber + "]");
        }

        model.addAttribute("revisions", revisions);

        s.getSiteMetadatas().sort(mapComparator);
        model.addAttribute("site", s);
        Iterable<Element> listaelementos = elementService.listAllElements();

        model.addAttribute("elements", listaelementos);

        model.addAttribute("enumMetadata", EnumMetadata.values());
        model.addAttribute("enumScopes", EnumScope.values());

        PublishedSite publishedSite = publishedSiteService.getPublishedSiteByparent_site_id(id);
        model.addAttribute("publishedSite", publishedSite);

        return "sites/siteform";
    }

    
    /**
     * Este método, llamado por POST, se encarga de persistir en BBDD los datos de un sitio enviados desde el formulario
     * Se encarga de crear los directorios necesarios, así como de crear un usuario administrador básico
     * @param s Sitio que viene pòr la request
     * @param request Resto de valores de la request
     * @return redirect a la plantilla de formulario
     */
    @RequestMapping(value = "site", method = RequestMethod.POST)
    public String saveSite(Site s, HttpServletRequest request) {

        FileManager fm = new FileManager(folderService, fileService, folderRepository, fileRepository, uploads_directory, uploads_base_url);
        Folder f = fm.createOrGetFolderIfExists("/", s.getName());
        fm.createOrGetFolderIfExists(f.getName(), "MEDIA");
        fm.createOrGetFolderIfExists(f.getName(), "DOCS");
        Folder fScripts = fm.createOrGetFolderIfExists(f.getName(), "SCRIPTS");
        Folder fViews = fm.createOrGetFolderIfExists(f.getName(), "VIEWS");
        Folder fCss = fm.createOrGetFolderIfExists(f.getName(), "CSS");

        Folder fStorage = null;

        if (s.getId() == null) {
            //SÃ³lamente si estoy creando el sitio por primera vez
            createAdminUserForSite(s.getName());
        }

        for (SiteMetadata smd : s.getSiteMetadatas()) {
            MetaDataSharedController.processSiteMetadata(smd, fStorage, fCss, fScripts, fViews, fm, mediaService, uploads_directory);

        }

        s = siteService.saveSite(s);

        return "redirect:/site/edit/" + s.getId();
    }

    /**
     * Método que elimina el sitio de id {id}
     * @param id id del sitio a eliminar
     * @return redirect a la lista de sitios
     */
    @RequestMapping("site/delete/{id}")
    public String delete(@PathVariable Integer id) {
        siteService.deleteSite(id);
        return "redirect:/sites/sites";
    }

    /**
     * Este método elimina uno o varios de los sitemetadatas del sitio por su id
     * @param id id del sitio de dónde se eliminaran los metadatas
     * @param metadatasIds id's de los metadatas a eliminar
     * @return redirtect a la página de formulario de edición del sitio
     */
    @RequestMapping("site/deletemetadata/{id}")
    public String deletemetadata(@PathVariable Integer id, @RequestParam(value = "metadatasIds") Integer[] metadatasIds) {
        Site s = siteService.getSiteById(id);
        List<SiteMetadata> smdABorrar = new ArrayList<>();
        for (SiteMetadata smd : s.getSiteMetadatas()) {
            for (Integer possibleMetadataId : metadatasIds) {
                if (smd.getId().equals(possibleMetadataId)) {
                    smdABorrar.add(smd);
                }
            }
        }

        for (SiteMetadata smd : smdABorrar) {

            s.getSiteMetadatas().remove(smd);

            smd.setSite(null);
            siteMetadataService.saveSiteMetadata(smd);

        }

        siteService.saveSite(s);

        return "redirect:/site/edit/" + s.getId();
    }


    /**
     * Este método obtiene el html del sitio, y lo escribe en un fichero "index.html", que se lanzará como descarga al ir a esta ruta
     * @param id id del sitio cuyo index.html queremos obtener
     * @param revision Revisión específica que colocar en el sitio
     * @param request HttpServletRequest para acceder directamente a los valores de la request
     * @param response HttpServletResponse para acceder directamente a los valores de la response
     */
    //esta devuelve solo el index.html de una revision dada
    @RequestMapping("site/gethtml/{id}")
    public void getHtml(@PathVariable Integer id, @RequestParam(value = "rev", required = false) Integer revision, HttpServletRequest request, HttpServletResponse response) {
        Site s = siteService.getSiteById(id);
        String html = getHtmlFromSite(request, response, s, revision);
        try {
            InputStream is = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));

            response.setHeader("Content-Disposition", "attachment; filename=index.html");
            IOUtils.copy(is, response.getOutputStream());
            //return "blackbet";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Este método obtiene el index.html, y lo devuelve directamente como texto, de manera que se puede ver en vivo el sitio sin necesidad de descargar el index.html
     * @param id id del sitio cuyo index.html queremos obtener
     * @param revNumber Revisión específica que colocar en el sitio
     * @param request HttpServletRequest para acceder directamente a los valores de la request
     * @param response HttpServletResponse para acceder directamente a los valores de la response
     * @return Se devuelve como cuerpo de la respuesta el texto dentro de index.html
     */
    @RequestMapping(value = "site/showhtml/{id}", produces = MediaType.TEXT_HTML)
    @ResponseBody
    public String showHtml(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "revNumber", required = false) Integer revNumber) {
        Site s = siteService.getSiteById(id);

        String html = getHtmlFromSite(request, response, s, revNumber);

        return html;
    }

    /**
     * Este método obtiene una lista de strings, con las url's de acceso de todos los ficheros de elementos insertados tanto en el sitio como
     * en las páginas que cuelgan del sitio
     * @param s Sitio del que buscar la lista de ficheros
     * @param key Key a buscar (ficheros CSS, HTML, JS)
     * @param where Where a buscar (VIEW, HEADER, SCRIPT...)
     * @return Lista con las url's de todos los ficheros de key KEY y where WHERE que cuelgan del sitio
     */
    private List<String> getMetadatasOfSiteBy(Site s, EnumMetadata key, EnumWhere where) {
        List<String> lista = new ArrayList<>();

        List<SiteMetadata> metadatas = s.getSiteMetadatas();

        if (metadatas != null && metadatas.size() > 0) {
            for (SiteMetadata metadata : metadatas) {
                ElementMetaData emd = metadata.getElementMetadata();
                if (emd != null) {

                    if (emd.getKey().equals(key) && emd.getWhere().equals(where)) {
                        lista.add(metadata.getValue());
                    }

                    if (emd.getElement().getDependencies() != null) {
                        lista.addAll(0, getDataOfDependencies(emd.getElement().getDependencies(), key, where));
                    }
                } else {
                    if (metadata.getWhere() != null && metadata.getWhere().equals(where)) {
                        if (key.equals(EnumMetadata.HTML) && metadata.getKey().equals(key)) {
                            lista.add(StringEscapeUtils.unescapeHtml4(metadata.getValue()));
                        } else {
                            //lista.add(metadata.getValue());
                            //Esto aqui estaba mal, hacÃ­a que se aÃ±adiese css en js
                        }
                    } else {
                        if (key.equals(EnumMetadata.CSS) && metadata.getKey().equals(key)) {
                            //Si no hay where ni tampoco elementmetadata, deberÃ­a ser el css
                            lista.add(metadata.getValue());
                        }

                    }
                }
            }
        }

        for (Page p : s.getPages()) {
            for (PageMetadata pm : p.getMetaDatas()) {
                ElementMetaData emd = pm.getElementMetadata();
                if (emd != null) {
                    if (emd.getKey().equals(key) && emd.getWhere().equals(where)) {
                        lista.add(pm.getValue());
                    }

                    if (emd.getElement().getDependencies() != null) {
                        lista.addAll(0, getDataOfDependencies(emd.getElement().getDependencies(), key, where));
                    }
                }
            }
        }

        //Esto se hace para eliminar duplicados, dado que un linkedhashset no puede tener duplicados
        return new ArrayList(new LinkedHashSet(lista));
    }

    /**
     * Este método obtiene una lista de strings, con las url's de acceso de todos los ficheros de elementos insertados tanto en el sitio publicado (PublishedSite) 
     * como en las páginas (publishedPages) que cuelgan del sitio
     * @param s Sitio (PublishedSite) del que buscar la lista de ficheros
     * @param key Key a buscar (ficheros CSS, HTML, JS)
     * @param where Where a buscar (VIEW, HEADER, SCRIPT...)
     * @return Lista con las url's de todos los ficheros de key KEY y where WHERE que cuelgan del PublishedSite
     */
    private List<String> getMetadatasOfPublishedSiteBy(PublishedSite s, EnumMetadata key, EnumWhere where) {
        List<String> lista = new ArrayList<>();

        List<PublishedSiteMetadata> metadatas = s.getSiteMetadatas();

        //List<SiteMetadata> metadatas = s.getSiteMetadatas();
        if (metadatas != null && metadatas.size() > 0) {
            for (PublishedSiteMetadata metadata : metadatas) {
                ElementMetaData emd = metadata.getElementMetadata();
                if (emd != null) {

                    if (emd.getKey().equals(key) && emd.getWhere().equals(where)) {
                        lista.add(metadata.getValue());
                    }

                    if (emd.getElement().getDependencies() != null) {
                        lista.addAll(0, getDataOfDependencies(emd.getElement().getDependencies(), key, where));
                    }
                } else {
                    if (metadata.getWhere() != null && metadata.getWhere().equals(where)) {
                        if (key.equals(EnumMetadata.HTML) && metadata.getKey().equals(key)) {
                            lista.add(StringEscapeUtils.unescapeHtml4(metadata.getValue()));
                        } else {
                            //lista.add(metadata.getValue());
                            //Esto aqui estaba mal, hacÃ­a que se aÃ±adiese css en js
                        }
                    } else {
                        if (key.equals(EnumMetadata.CSS) && metadata.getKey().equals(key)) {
                            //Si no hay where ni tampoco elementmetadata, deberÃ­a ser el css
                            lista.add(metadata.getValue());
                        }

                    }
                }
            }
        }

        for (PublishedPage p : s.getPages()) {
            for (PublishedPageMetadata pm : p.getMetaDatas()) {
                ElementMetaData emd = pm.getElementMetadata();
                if (emd != null) {
                    if (emd.getKey().equals(key) && emd.getWhere().equals(where)) {
                        lista.add(pm.getValue());
                    }

                    if (emd.getElement().getDependencies() != null) {
                        lista.addAll(0, getDataOfDependencies(emd.getElement().getDependencies(), key, where));
                    }
                }
            }
        }

        //Esto se hace para eliminar duplicados, dado que un linkedhashset no puede tener duplicados
        return new ArrayList(new LinkedHashSet(lista));
    }

    /**
     * Este método se encarga de recorrer las posibles dependencias existentes en un elemento recursivamente, de manera 
     * que se puedan obtener todas las url's de todos los ficheros también de las dependencias de los elementos
     * @param dependencies lista de elementos en la que buscar los ficheros
     * @param key Key a buscar (ficheros CSS, HTML, JS)
     * @param where Where a buscar (VIEW, HEADER, SCRIPT...)
     * @return Lista de todos los ficheros de key KEY y where WHERE encontrados entre las dependencias
     */
    private List<String> getDataOfDependencies(List<Element> dependencies, EnumMetadata key, EnumWhere where) {
        List<String> lista = new ArrayList<>();
        for (Element e : dependencies) {
            for (ElementMetaData emd : e.getMetaDatas()) {
                if (emd != null && emd.getKey() != null && emd.getKey().equals(key) && emd.getWhere() != null && emd.getWhere().equals(where)) {
                    if (emd != null && emd.getStorage() != null) {
                        switch (emd.getStorage()) {
                            case FILEMANAGER_CREATE:
                            case FILEMANAGER_NOT_CREATE:
                                lista.add(emd.getScript().getUrlPath());
                                break;
                            case EXTERNAL:
                            case INLINE:
                                lista.add(emd.getValue());

                        }
                    }
                }
            }
            if (e.getDependencies() != null && e.getDependencies().size() > 0) {
                lista.addAll(0, getDataOfDependencies(e.getDependencies(), key, where));
            }

        }

        return lista;
    }

    /**
     * Este método obtiene una lista de strings, con las rutas en el sistema de ficheros de todos los ficheros de elementos insertados 
     * tanto en el sitio publicado (PublishedSite) como en las páginas (publishedPages) que cuelgan del sitio
     * @param s Sitio (PublishedSite) del que buscar la lista de ficheros
     * @param key Key a buscar (ficheros CSS, HTML, JS)
     * @param where Where a buscar (VIEW, HEADER, SCRIPT...)
     * @return Lista con las rutas en el sistema de ficheros de todos los ficheros de key KEY y where WHERE que cuelgan del PublishedSite
     */
    private List<String> getFilesMetadatasOfPublishedSiteBy(PublishedSite s, EnumMetadata key, EnumWhere where) {
        List<String> lista = new ArrayList<>();

        List<PublishedSiteMetadata> metadatas = s.getSiteMetadatas();

        //List<SiteMetadata> metadatas = s.getSiteMetadatas();
        if (metadatas != null && metadatas.size() > 0) {
            for (PublishedSiteMetadata metadata : metadatas) {
                ElementMetaData emd = metadata.getElementMetadata();
                if (emd != null) {

                    if (emd.getKey().equals(key) && emd.getWhere().equals(where) && metadata.getScript() != null) {
                        lista.add(metadata.getScript().getFilePath());
                    }

                    if (emd.getElement().getDependencies() != null) {
                        lista.addAll(0, getFilesOfDependencies(emd.getElement().getDependencies(), key, where));
                    }
                } else {
                    if (metadata.getWhere() != null && metadata.getWhere().equals(where) && metadata.getScript() != null) {
                        if (key.equals(EnumMetadata.HTML) && metadata.getKey().equals(key)) {
                            lista.add(StringEscapeUtils.unescapeHtml4(metadata.getScript().getFilePath()));
                        } else {
                            //lista.add(metadata.getValue());
                            //Esto aqui estaba mal, hacÃ­a que se aÃ±adiese css en js
                        }
                    } else {
                        if (key.equals(EnumMetadata.CSS) && metadata.getKey().equals(key) && metadata.getScript() != null) {
                            //Si no hay where ni tampoco elementmetadata, deberÃ­a ser el css
                            lista.add(metadata.getScript().getFilePath());
                        }

                    }
                }
            }
        }

        for (PublishedPage p : s.getPages()) {
            for (PublishedPageMetadata pm : p.getMetaDatas()) {
                ElementMetaData emd = pm.getElementMetadata();
                if (emd != null) {
                    if (emd.getKey().equals(key) && emd.getWhere().equals(where) && pm.getScript() != null) {
                        lista.add(pm.getScript().getFilePath());
                    }

                    if (emd.getElement().getDependencies() != null) {
                        lista.addAll(0, getFilesOfDependencies(emd.getElement().getDependencies(), key, where));
                    }
                }
            }
        }

        //Esto se hace para eliminar duplicados, dado que un linkedhashset no puede tener duplicados
        return new ArrayList(new LinkedHashSet(lista));
    }

    /**
     * Este método se encarga de recorrer las posibles dependencias existentes en un elemento recursivamente, de manera 
     * que se puedan obtener todas las rutas de acceso en el sistema de ficheros de todos los ficheros también de las dependencias de los elementos
     * @param dependencies lista de elementos en la que buscar los ficheros
     * @param key Key a buscar (ficheros CSS, HTML, JS)
     * @param where Where a buscar (VIEW, HEADER, SCRIPT...)
     * @return Lista de las rutas en sistema de ficheros de todos los ficheros de key KEY y where WHERE encontrados entre las dependencias
     */
    private List<String> getFilesOfDependencies(List<Element> dependencies, EnumMetadata key, EnumWhere where) {
        List<String> lista = new ArrayList<>();
        for (Element e : dependencies) {
            for (ElementMetaData emd : e.getMetaDatas()) {
                if (emd != null && emd.getKey() != null && emd.getKey().equals(key) && emd.getWhere() != null && emd.getWhere().equals(where)) {
                    if (emd != null && emd.getStorage() != null) {
                        //Aqui hay algo raruno

                        switch (emd.getStorage()) {
                            case FILEMANAGER_CREATE:
                            case FILEMANAGER_NOT_CREATE:
                                lista.add(emd.getScript().getFilePath());
                                break;
                            case EXTERNAL:
                            case INLINE:
                            //lista.add(emd.getValue());

                        }
                    }
                }
            }
            if (e.getDependencies() != null && e.getDependencies().size() > 0) {
                lista.addAll(0, getFilesOfDependencies(e.getDependencies(), key, where));
            }

        }

        return lista;
    }

    /**
     * Esta es una función auxiliar que devuelve el valor del index.html a partir de un publishedSite dado. Se usa para obtener el valor del index en la subida automática de FTP
     * Se obtienen todos los metadatas del sitio, y a la url de los ficheros se le hace una sustitución para que lo obtenga desde la ruta del sitio, 
     * en vez de desde el wanaba. Las funciones para ello se encuentran en la clase sharedfileutils
     * @param request Request a pelo de la petición
     * @param response Response a pelo de la petición
     * @param s Sitio (publishedSite) del que obtener el index.html
     * @return cadena de texto con el valor del index.html
     */
    private String getHtmlFromPublishedSite(HttpServletRequest request, HttpServletResponse response, PublishedSite s, String serviceUrl) {
        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale());
        
        List<String> JS_LIBS = getMetadatasOfPublishedSiteBy(s, EnumMetadata.JS, EnumWhere.LIBRARY);
        List<String> CSS_LIBS = getMetadatasOfPublishedSiteBy(s, EnumMetadata.CSS, EnumWhere.LIBRARY);
        List<String> HTML_LIBS = getMetadatasOfPublishedSiteBy(s, EnumMetadata.HTML, EnumWhere.LIBRARY);
        List<String> JS_SCRIPTS = getMetadatasOfPublishedSiteBy(s, EnumMetadata.JS, EnumWhere.SCRIPT);
        List<String> CSS_SCRIPTS = getMetadatasOfPublishedSiteBy(s, EnumMetadata.CSS, EnumWhere.SCRIPT);
        List<String> HTML_SCRIPTS = getMetadatasOfPublishedSiteBy(s, EnumMetadata.HTML, EnumWhere.SCRIPT);

        JS_LIBS = SharedFileUtils.changeRouteScript(JS_LIBS, EnumMetadata.JS, EnumWhere.LIBRARY, s.getDomain(), this);
        CSS_LIBS = SharedFileUtils.changeRouteScript(CSS_LIBS, EnumMetadata.CSS, EnumWhere.LIBRARY, s.getDomain(), this);
        //changeRouteScript(HTML_LIBS, EnumMetadata.HTML, EnumWhere.LIBRARY, s.getDomain());
        JS_SCRIPTS = SharedFileUtils.changeRouteScript(JS_SCRIPTS, EnumMetadata.JS, EnumWhere.SCRIPT, s.getDomain(), this);
        CSS_SCRIPTS = SharedFileUtils.changeRouteScript(CSS_SCRIPTS, EnumMetadata.CSS, EnumWhere.SCRIPT, s.getDomain(), this);
        //changeRouteScript(HTML_SCRIPTS, EnumMetadata.HTML, EnumWhere.SCRIPT, s.getDomain());

        String HEADER = getPublishedHeader(s);
        String FOOTER = getPublishedFooter(s);

        HEADER = SharedFileUtils.changeSingleRoute(HEADER, EnumMetadata.HTML, EnumWhere.HEADER, s.getDomain(), this);
        FOOTER = SharedFileUtils.changeSingleRoute(FOOTER, EnumMetadata.HTML, EnumWhere.FOOTER, s.getDomain(), this);

        List<Route> routes = getPublishedRoutes(s);

        for (Route r : routes) {
            r.setTemplateUrl(SharedFileUtils.changeSingleRoute(r.getTemplateUrl(), EnumMetadata.HTML, EnumWhere.VIEW, s.getDomain(), this));
        }

        return getHtml(ctx, s.getName(), s.getDomain(), serviceUrl, JS_LIBS, CSS_LIBS, HTML_LIBS, JS_SCRIPTS, CSS_SCRIPTS, HTML_SCRIPTS, HEADER, FOOTER, routes);
    }

    /**
     * Esta es una función auxiliar que devuelve el valor del index.html a partir de un sitio dado, con su revisión en caso de que venga. 
     * Se usa para obtener el valor del index en las previews del sitio
     * @param request Request a pelo de la petición
     * @param response Response a pelo de la petición
     * @param s Sitio del que obtener el index.html
     * @param revision nº de revisión del que obtener el index.html
     * @return cadena de texto con el valor del index.html
     */
    private String getHtmlFromSite(HttpServletRequest request, HttpServletResponse response, Site s, Integer revision) {
        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale());

        String serviceUrl = bo_base_url + s.getName().toUpperCase() + "/datatron";

        if (revision != null) {
            AuditReader reader = AuditReaderFactory.get(entityManager);

            s = reader.find(Site.class, s.getId(), revision);

            ctx.setVariable("rev", "[" + revision + "]");
        }

        List<String> JS_LIBS = getMetadatasOfSiteBy(s, EnumMetadata.JS, EnumWhere.LIBRARY);
        List<String> CSS_LIBS = getMetadatasOfSiteBy(s, EnumMetadata.CSS, EnumWhere.LIBRARY);
        List<String> HTML_LIBS = getMetadatasOfSiteBy(s, EnumMetadata.HTML, EnumWhere.LIBRARY);
        List<String> JS_SCRIPTS = getMetadatasOfSiteBy(s, EnumMetadata.JS, EnumWhere.SCRIPT);
        List<String> CSS_SCRIPTS = getMetadatasOfSiteBy(s, EnumMetadata.CSS, EnumWhere.SCRIPT);
        List<String> HTML_SCRIPTS = getMetadatasOfSiteBy(s, EnumMetadata.HTML, EnumWhere.SCRIPT);

        String HEADER = getHeader(s);
        String FOOTER = getFooter(s);

        List<Route> routes = getRoutes(s);

        return getHtml(ctx, s.getName(), s.getDomain(), serviceUrl, JS_LIBS, CSS_LIBS, HTML_LIBS, JS_SCRIPTS, CSS_SCRIPTS, HTML_SCRIPTS, HEADER, FOOTER, routes);

    }

    /**
     * Esta función se encarga de devolver la cadena de texto del index.html correspondiente a los parámetros enviados.
     * @param ctx Contexto para el motor de templating de thymeleaf
     * @param title Título del sitio
     * @param domain Dominio del que cuelga el sitio
     * @param serviceUrl Ubicación de la url desde la que se obtendrán los servicios
     * @param JS_LIBS Lista de url's que representan librerías JS
     * @param CSS_LIBS Lista de url's que representan librerías CSS
     * @param HTML_LIBS Lista de url's que representan librerías HTML
     * @param JS_SCRIPTS Lista de url's que representan scripts JS
     * @param CSS_SCRIPTS Lista de url's que representan scripts CSS
     * @param HTML_SCRIPTS Lista de url's que representan scripts HTML
     * @param HEADER Ruta al html del header
     * @param FOOTER Ruta al html del footer
     * @param routes Información de las rutas que debe tener en cuenta angular
     * @return cadena de texto con el html del index rellenado
     */
    private String getHtml(
            WebContext ctx,
            String title,
            String domain,
            String serviceUrl,
            List<String> JS_LIBS,
            List<String> CSS_LIBS,
            List<String> HTML_LIBS,
            List<String> JS_SCRIPTS,
            List<String> CSS_SCRIPTS,
            List<String> HTML_SCRIPTS,
            String HEADER,
            String FOOTER,
            List<Route> routes
    ) {

        String boBaseUrl = bo_base_url;

        ctx.setVariable("title", title);
        ctx.setVariable("baseUrl", domain);
        ctx.setVariable("boBaseUrl", boBaseUrl);
        //ctx.setVariable("serviceUrl", boBaseUrl);
        ctx.setVariable("rev", "");

        ctx.setVariable("verticalBar", "|");

        ctx.setVariable("serviceUrl", serviceUrl);

        ctx.setVariable("JS_LIBS", JS_LIBS);
        ctx.setVariable("CSS_LIBS", CSS_LIBS);
        ctx.setVariable("HTML_LIBS", HTML_LIBS);
        ctx.setVariable("JS_SCRIPTS", JS_SCRIPTS);
        ctx.setVariable("CSS_SCRIPTS", CSS_SCRIPTS);
        ctx.setVariable("HTML_SCRIPTS", HTML_SCRIPTS);

        ctx.setVariable("HEADER", HEADER);
        ctx.setVariable("FOOTER", FOOTER);

        ctx.setVariable("routes", routes);

        ctx.setVariable("uploads_base_url_with_asterisk", uploads_base_url + "**");

        String html = "";
        html = htmlTemplateEngine.process("blackbet/index", ctx);
        html = html.replaceAll("&#39;", "'");

        return html;
    }

    /**
     * Este método recorre las páginas que cuelgan de un sitio y se obtiene su ruta, para generar las rutas del sitio
     * @param s Sitio del que obtener las rutas
     * @return Lista de rutas del sitio
     */
    private List<Route> getRoutes(Site s) {
        List<Route> routes = new ArrayList<Route>();
        for (Page p : s.getPages()) {
            Route e = new Route();
            e.setRoute(p.getRoute());
            e.setName(p.getName());
            for (PageMetadata pm : p.getMetaDatas()) {
                if (pm.getKey() == EnumMetadata.HTML) {
                    e.setTemplateUrl(pm.getValue());
                    e.setFileRoute(pm.getScript().getFilePath());
                }
            }
            routes.add(e);
        }
        return routes;
    }

    /**
     * Este método recorre las páginas que cuelgan de un sitio (publishedSite) y se obtiene su ruta, para generar las rutas del sitio (publishedSite)
     * @param s Sitio (publishedSite) del que obtener las rutas
     * @return Lista de rutas del sitio(publishedSite)
     */
    private List<Route> getPublishedRoutes(PublishedSite s) {
        List<Route> routes = new ArrayList<Route>();
        for (PublishedPage p : s.getPages()) {
            Route e = new Route();
            e.setRoute(p.getRoute());
            e.setName(p.getName());
            for (PublishedPageMetadata pm : p.getMetaDatas()) {
                if (pm.getKey() == EnumMetadata.HTML) {
                    e.setTemplateUrl(pm.getValue());
                    e.setFileRoute(pm.getScript().getFilePath());
                }
            }
            routes.add(e);
        }
        return routes;
    }

    /**
     * Devuelve la url del header del sitio s
     * @param s sitio del que obtener header
     * @return  url del header del sitio
     */
    private String getHeader(Site s) {
        SiteMetadata header = getMetadataHeader(s);
        if (header != null) {
            return header.getValue();
        } else {
            return "";
        }
    }

    
    /**
     * Devuelve la url del footer del sitio s
     * @param s sitio del que obtener footer
     * @return  url del footer del sitio
     */
    private String getFooter(Site s) {
        SiteMetadata footer = getMetadataFooter(s);
        if (footer != null) {
            return footer.getValue();
        } else {
            return "";
        }
    }

    /**
     * Devuelve la url del header del PublishedSite s
     * @param s PublishedSite del que obtener header
     * @return  url del header del PublishedSite
     */
    private String getPublishedHeader(PublishedSite s) {
        PublishedSiteMetadata header = getPublishedMetadataHeader(s);
        if (header != null) {
            return header.getValue();
        } else {
            return "";
        }
    }
    
    /**
     * Devuelve la url del footer del PublishedSite s
     * @param s PublishedSite del que obtener footer
     * @return  url del footer del PublishedSite
     */
    private String getPublishedFooter(PublishedSite s) {
        PublishedSiteMetadata footer = getPublishedMetadataFooter(s);
        if (footer != null) {
            return footer.getValue();
        } else {
            return "";
        }
    }

    /**
     * Devuelve el sitemetadata del footer del sitio s
     * @param s sitio del que obtener footer
     * @return  sitemetadata del footer del sitio
     */
    private SiteMetadata getMetadataFooter(Site s) {
        for (SiteMetadata smd : s.getSiteMetadatas()) {
            if (smd.getWhere() == EnumWhere.FOOTER) {
                return smd;
            }
        }
        return null;
    }

    
    /**
     * Devuelve el sitemetadata del header del sitio s
     * @param s sitio del que obtener header
     * @return  sitemetadata del header del sitio
     */
    private SiteMetadata getMetadataHeader(Site s) {
        for (SiteMetadata smd : s.getSiteMetadatas()) {
            if (smd.getWhere() == EnumWhere.HEADER) {
                return smd;
            }
        }
        return null;
    }

    /**
     * Devuelve el publishedsitemetadata del footer del PublishedSite s
     * @param s PublishedSite del que obtener footer
     * @return  publishedsitemetadata del footer del PublishedSite
     */
    private PublishedSiteMetadata getPublishedMetadataFooter(PublishedSite s) {
        for (PublishedSiteMetadata smd : s.getSiteMetadatas()) {
            if (smd.getWhere() == EnumWhere.FOOTER) {
                return smd;
            }
        }
        return null;
    }

    /**
     * Devuelve el publishedsitemetadata del header del PublishedSite s
     * @param s PublishedSite del que obtener header
     * @return  publishedsitemetadata del header del PublishedSite
     */
    private PublishedSiteMetadata getPublishedMetadataHeader(PublishedSite s) {
        for (PublishedSiteMetadata smd : s.getSiteMetadatas()) {
            if (smd.getWhere() == EnumWhere.HEADER) {
                return smd;
            }
        }
        return null;
    }

    /**
     * Método que obtiene los sensitivedatas de un sitio (datos como la cadena de conexión ftp)
     * @param id id del sitio
     * @param model Modelo que inserta spring directamente
     * @return ruta a la template de edición de sensitivedatas
     */
    @RequestMapping(value = "site/sitesensitivedatas/{id}", method = RequestMethod.GET)
    public String sensitiveDatasShow(@PathVariable Integer id, Model model) {
        Site s = siteService.getSiteById(id);

        SiteSensitiveDatas ssd = siteSensitiveDatasService.getBySite(s);

        model.addAttribute("siteSensitiveData", ssd);

        return "sites/sitesensitivedatas";
    }

    /**
     * Método que guarda los sensitivedatas de un sitio (datos como la cadena de conexión ftp)
     * @param ssd sitesensitivedatas recibido
     * @return ruta a la template de edición de sensitivedatas
     */
    @RequestMapping(value = "site/sitesensitivedatas", method = RequestMethod.POST)
    public String sensitiveDatasSave(SiteSensitiveDatas ssd) {

        siteSensitiveDatasService.save(ssd);

        return "redirect:/site/sitesensitivedatas/" + ssd.getSite().getId();
    }

     /**
     * Este método, a partir del id del site, realizará las siguientes acciones:
     * Obtiene el publishedSite correspondiente al site
     * Obtiene todos los ficheros de las librerías, scripts, header, footer, servicios (de lo que esté cacheado en ese momento) y rutas a partir del publishedSite
     * Crea una ruta temporal en la que copiar los ficheros obtenidos en el paso anterior
     * Genera el index.html sustituyendo las rutas en todos los ficheros anteriores por rutas que apuntan al mismo dominio en el que estará el sitio
     * Comprime en zip todo el directorio y lo devuelve al navegador
     * Todos estos métodos escriben en la response su progreso
     * @param id id del sitio cuyo publishedsite se publicará
     * @param request Request a pelo de la petición
     * @param response Response a pelo de la petición
     * @throws Exception 
     */
    @RequestMapping(value = "site/downloadproduction/{id}", method = RequestMethod.GET, produces="application/zip")
    public void DownloadPublishedSite(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        Logger.getLogger(SiteController.class.getName()).log(Level.INFO, "Descargando publishedSite");
        
        Site s = siteService.getSiteById(id);

        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\""+s.getName()+".zip\"");

        
        PublishedSite ps = publishedSiteService.getPublishedSiteByparent_site_id(id);

        List<String> JS_LIBS = getFilesMetadatasOfPublishedSiteBy(ps, EnumMetadata.JS, EnumWhere.LIBRARY);
        List<String> CSS_LIBS = getFilesMetadatasOfPublishedSiteBy(ps, EnumMetadata.CSS, EnumWhere.LIBRARY);
        List<String> HTML_LIBS = getFilesMetadatasOfPublishedSiteBy(ps, EnumMetadata.HTML, EnumWhere.LIBRARY);
        List<String> JS_SCRIPTS = getFilesMetadatasOfPublishedSiteBy(ps, EnumMetadata.JS, EnumWhere.SCRIPT);
        List<String> CSS_SCRIPTS = getFilesMetadatasOfPublishedSiteBy(ps, EnumMetadata.CSS, EnumWhere.SCRIPT);
        List<String> HTML_SCRIPTS = getFilesMetadatasOfPublishedSiteBy(ps, EnumMetadata.HTML, EnumWhere.SCRIPT);

        String HTML_HEADER = getPublishedMetadataHeader(ps).getScript().getFilePath();
        String HTML_FOOTER = getPublishedMetadataFooter(ps).getScript().getFilePath();

        String SERVICE = cachedMediaService.getMediaByName(ps.getName()).getFullData();

        List<String> filePathRoutes = new ArrayList<>();
        List<Route> routes = getPublishedRoutes(ps);
        for (Route r : routes) {
            filePathRoutes.add(r.getFileRoute());
        }
        filePathRoutes = new ArrayList(new LinkedHashSet(filePathRoutes));
        //Vamos a chequear si existe un directorio (del sistema de ficheros, no de nuestra bbdd) para usarlo de manera temporal de cara a la autosubida

        String routeIntermediateUpload = uploads_directory + "temp_dir_to_upload" + java.io.File.separator;

        java.io.File tempDirectory = new java.io.File(routeIntermediateUpload);

        if (tempDirectory.exists()) {
            //Eliminamos directorios
            FileUtils.cleanDirectory(tempDirectory);
        } else {
            //Creamos el directorio
            tempDirectory.mkdirs();
        }
        String serviceUrl = bo_base_url + s.getName().toUpperCase() + "/datatron";
        
        String index = getHtmlFromPublishedSite(request, response, ps, serviceUrl);

        java.io.File indexFile = new java.io.File(tempDirectory, "index.html");
        FileUtils.write(indexFile, index);

        java.io.File mediasFile = new java.io.File(tempDirectory, "service.json");
        FileUtils.write(mediasFile, SERVICE, "UTF-8");

        Logger.getLogger(SiteController.class.getName()).log(Level.INFO, "Empezamos a replicar el sitio en la carpeta temporal");
        
        SharedFileUtils.copyToTempDirectory(filePathRoutes, EnumMetadata.HTML, EnumWhere.VIEW, tempDirectory);

        Logger.getLogger(SiteController.class.getName()).log(Level.INFO, "2");

        SharedFileUtils.copyToTempDirectory(JS_LIBS, EnumMetadata.JS, EnumWhere.LIBRARY, tempDirectory);
        Logger.getLogger(SiteController.class.getName()).log(Level.INFO, "3");
        SharedFileUtils.copyToTempDirectory(CSS_LIBS, EnumMetadata.CSS, EnumWhere.LIBRARY, tempDirectory);
        Logger.getLogger(SiteController.class.getName()).log(Level.INFO, "4");
        SharedFileUtils.copyToTempDirectory(JS_SCRIPTS, EnumMetadata.JS, EnumWhere.SCRIPT, tempDirectory);
        Logger.getLogger(SiteController.class.getName()).log(Level.INFO, "5");
        SharedFileUtils.copyToTempDirectory(CSS_SCRIPTS, EnumMetadata.CSS, EnumWhere.SCRIPT, tempDirectory);
        Logger.getLogger(SiteController.class.getName()).log(Level.INFO, "6");

        SharedFileUtils.copyToTempDirectory(Arrays.asList(HTML_HEADER), EnumMetadata.HTML, EnumWhere.HEADER, tempDirectory);
        SharedFileUtils.copyToTempDirectory(Arrays.asList(HTML_FOOTER), EnumMetadata.HTML, EnumWhere.FOOTER, tempDirectory);

        List<String> indexServiceHeaderFooter = new ArrayList<>();
        indexServiceHeaderFooter.add(indexFile.getAbsolutePath());
        indexServiceHeaderFooter.add(mediasFile.getAbsolutePath());

        SharedFileUtils.copyToTempDirectory(indexServiceHeaderFooter, null, null, tempDirectory);
        
        //ahora comprimimos la carpeta temporal en zip
        List<String> fileList = getFileList(tempDirectory);
        
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());

        try {

            for (String filePath : fileList) {

                //
                // Creates a zip entry.
                //
                String name = filePath.substring(tempDirectory.getAbsolutePath().length() + 1,
                        filePath.length());
                ZipEntry zipEntry = new ZipEntry(name);
                zos.putNextEntry(zipEntry);

                //
                // Read file content and write to zip output stream.
                //
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                //
                // Close the zip entry and the file input stream.
                //
                zos.closeEntry();
                fis.close();
            }

            //
            // Close zip output stream and file output stream. This will
            // complete the compression process.
            //
            if (zos != null) {
                zos.finish();
                zos.flush();
                IOUtils.closeQuietly(zos);
            }
            response.getOutputStream().flush();

        } catch (IOException e) {
        }
    }
    
    /**
     * Este método devuelve la lista de ficheros recursivamente de un directorio
     * @param directory directorio en el que buscar
     * @return lista de ficheros con ruta absoluta bajo el directorio
     */
    private List<String> getFileList(java.io.File directory) {
        List<String> fileList = new ArrayList<>();
        java.io.File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for (java.io.File file : files) {
                if (file.isFile()) {
                    fileList.add(file.getAbsolutePath());
                } else {
                    fileList.addAll(getFileList(file));
                }
            }
        }
        return fileList;
    }
    
    /**
     * Este método, a partir del id del site, realizará las siguientes acciones:
     * Obtiene el publishedSite correspondiente al site
     * Obtiene los datos del ftp del sitio
     * Obtiene todos los ficheros de las librerías, scripts, header, footer, servicios (de lo que esté cacheado en ese momento) y rutas a partir del publishedSite
     * Crea una ruta temporal en la que copiar los ficheros obtenidos en el paso anterior
     * Genera el index.html sustituyendo las rutas en todos los ficheros anteriores por rutas que apuntan al mismo dominio en el que estará el sitio
     * Comienza a enviar por ftp todos los ficheros generados, apoyándose en la clase sharedfileutils
     * Todos estos métodos escriben en la response su progreso
     * @param id id del sitio cuyo publishedsite se publicará
     * @param request Request a pelo de la petición
     * @param response Response a pelo de la petición
     * @throws Exception 
     */
    @RequestMapping(value = "site/compileandsendbyftp/{id}", method = RequestMethod.GET)
    @ResponseBody
    public void CompileAndSendByFtp(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Site s = siteService.getSiteById(id);

        SiteSensitiveDatas ssd = siteSensitiveDatasService.getBySite(s);
        PublishedSite ps = publishedSiteService.getPublishedSiteByparent_site_id(id);

        List<String> JS_LIBS = getFilesMetadatasOfPublishedSiteBy(ps, EnumMetadata.JS, EnumWhere.LIBRARY);
        List<String> CSS_LIBS = getFilesMetadatasOfPublishedSiteBy(ps, EnumMetadata.CSS, EnumWhere.LIBRARY);
        List<String> HTML_LIBS = getFilesMetadatasOfPublishedSiteBy(ps, EnumMetadata.HTML, EnumWhere.LIBRARY);
        List<String> JS_SCRIPTS = getFilesMetadatasOfPublishedSiteBy(ps, EnumMetadata.JS, EnumWhere.SCRIPT);
        List<String> CSS_SCRIPTS = getFilesMetadatasOfPublishedSiteBy(ps, EnumMetadata.CSS, EnumWhere.SCRIPT);
        List<String> HTML_SCRIPTS = getFilesMetadatasOfPublishedSiteBy(ps, EnumMetadata.HTML, EnumWhere.SCRIPT);

        String HTML_HEADER = getPublishedMetadataHeader(ps).getScript().getFilePath();
        String HTML_FOOTER = getPublishedMetadataFooter(ps).getScript().getFilePath();

        String SERVICE = cachedMediaService.getMediaByName(ps.getName()).getFullData();

        List<String> filePathRoutes = new ArrayList<>();
        List<Route> routes = getPublishedRoutes(ps);
        for (Route r : routes) {
            filePathRoutes.add(r.getFileRoute());
        }
        filePathRoutes = new ArrayList(new LinkedHashSet(filePathRoutes));
        //Vamos a chequear si existe un directorio (del sistema de ficheros, no de nuestra bbdd) para usarlo de manera temporal de cara a la autosubida

        String routeIntermediateUpload = uploads_directory + "temp_dir_to_upload" + java.io.File.separator;

        java.io.File tempDirectory = new java.io.File(routeIntermediateUpload);

        if (tempDirectory.exists()) {
            //Eliminamos directorios
            FileUtils.cleanDirectory(tempDirectory);
        } else {
            //Creamos el directorio
            tempDirectory.mkdirs();
        }

        String index = getHtmlFromPublishedSite(request, response, ps, "service.json");

        java.io.File indexFile = new java.io.File(tempDirectory, "index.html");
        FileUtils.write(indexFile, index);

        java.io.File mediasFile = new java.io.File(tempDirectory, "service.json");
        FileUtils.write(mediasFile, SERVICE, "UTF-8");

        Logger.getLogger(SiteController.class.getName()).log(Level.INFO, "Empezamos");

        SharedFileUtils.sendFileList(filePathRoutes, EnumMetadata.HTML, EnumWhere.VIEW, tempDirectory, ssd, response.getOutputStream());

        Logger.getLogger(SiteController.class.getName()).log(Level.INFO, "2");

        SharedFileUtils.sendFileList(JS_LIBS, EnumMetadata.JS, EnumWhere.LIBRARY, tempDirectory, ssd, response.getOutputStream());
        Logger.getLogger(SiteController.class.getName()).log(Level.INFO, "3");
        SharedFileUtils.sendFileList(CSS_LIBS, EnumMetadata.CSS, EnumWhere.LIBRARY, tempDirectory, ssd, response.getOutputStream());
        Logger.getLogger(SiteController.class.getName()).log(Level.INFO, "4");
        SharedFileUtils.sendFileList(JS_SCRIPTS, EnumMetadata.JS, EnumWhere.SCRIPT, tempDirectory, ssd, response.getOutputStream());
        Logger.getLogger(SiteController.class.getName()).log(Level.INFO, "5");
        SharedFileUtils.sendFileList(CSS_SCRIPTS, EnumMetadata.CSS, EnumWhere.SCRIPT, tempDirectory, ssd, response.getOutputStream());
        Logger.getLogger(SiteController.class.getName()).log(Level.INFO, "6");

        SharedFileUtils.sendFileList(Arrays.asList(HTML_HEADER), EnumMetadata.HTML, EnumWhere.HEADER, tempDirectory, ssd, response.getOutputStream());
        SharedFileUtils.sendFileList(Arrays.asList(HTML_FOOTER), EnumMetadata.HTML, EnumWhere.FOOTER, tempDirectory, ssd, response.getOutputStream());

        List<String> indexServiceHeaderFooter = new ArrayList<>();
        indexServiceHeaderFooter.add(indexFile.getAbsolutePath());
        indexServiceHeaderFooter.add(mediasFile.getAbsolutePath());

        SharedFileUtils.sendFileList(indexServiceHeaderFooter, null, null, tempDirectory, ssd, response.getOutputStream());

    }

    /**
     * Esta función publica un sitio, es decir, hace el copiado de los datos de las estructuras site-sitemetadata-page-pagemetadata a sus correspondientes
     * versiones published. Es necesario que haya un sitio published para que se generen los medias, y para que se pueda enviar por ftp
     * @param id id del sitio a publicar
     * @param revNumber nº de revisión a publicar
     */
    @RequestMapping("site/publish/{id}")
    @ResponseBody
    public void publishSite(@PathVariable Integer id, @RequestParam(value = "revNumber", required = false) Integer revNumber) {
        Site sToPublish;

        if (revNumber != null) {
            AuditReader reader = AuditReaderFactory.get(entityManager);
            sToPublish = reader.find(Site.class, id, revNumber);
        } else {
            sToPublish = siteService.getSiteById(id);
            revNumber = createSiteRevision(sToPublish).intValue();

        }
        PublishedSite oldPublishedSite = null;
        PublishedSite publishedSite = publishedSiteService.getPublishedSiteByparent_site_id(id);
        if (publishedSite == null) {
            publishedSite = new PublishedSite();
        } else {
            oldPublishedSite = publishedSite;
            
            publishedSite = new PublishedSite();

        }

        publishedSite.setCreation(DateTime.now().toDate());
        publishedSite.setDomain(sToPublish.getDomain());
        publishedSite.setGlobal_css(sToPublish.getGlobal_css());
        publishedSite.setHas_footer(sToPublish.getHas_footer());
        publishedSite.setHas_header(sToPublish.getHas_header());
        publishedSite.setName(sToPublish.getName());
        publishedSite.setParentSiteId(sToPublish.getId());
        publishedSite.setRevisionNumber(revNumber);
        publishedSite.setUpdated(DateTime.now().toDate());
        publishedSite.setVersion(sToPublish.getVersion());

        List<PublishedPage> pps = new ArrayList<>();

        for (Page p : sToPublish.getPages()) {
            PublishedPage pp = new PublishedPage();

            pp.setContent(p.getContent());
            pp.setCreation(DateTime.now().toDate());
            pp.setCss(p.getCss());
            pp.setJs(p.getJs());
            pp.setName(p.getName());
            pp.setPublication(DateTime.now().toDate());
            pp.setRoute(p.getRoute());
            pp.setSite(publishedSite);
            pp.setUpdatation(DateTime.now().toDate());
            pp.setUpdated(DateTime.now().toDate());
            pp.setVersion(p.getVersion());

            List<PublishedPageMetadata> ppmds = new ArrayList<>();

            for (PageMetadata pmd : p.getMetaDatas()) {
                PublishedPageMetadata ppmd = new PublishedPageMetadata();

                ppmd.setCreation(DateTime.now().toDate());
                ppmd.setElementMetadata(pmd.getElementMetadata());
                ppmd.setKey(pmd.getKey());
                ppmd.setPage(pp);
                ppmd.setScript(pmd.getScript());
                ppmd.setUpdated(DateTime.now().toDate());
                ppmd.setValue(pmd.getValue());
                ppmd.setVersion(pmd.getVersion());

                ppmds.add(ppmd);
            }

            pp.setMetaDatas(ppmds);
            pps.add(pp);
        }

        publishedSite.setPages(pps);

        List<PublishedSiteMetadata> psms = new ArrayList<>();

        for (SiteMetadata sm : sToPublish.getSiteMetadatas()) {
            PublishedSiteMetadata psm = new PublishedSiteMetadata();

            psm.setCreation(DateTime.now().toDate());
            psm.setElementMetadata(sm.getElementMetadata());
            psm.setKey(sm.getKey());
            psm.setOrder(sm.getOrder());
            psm.setScript(sm.getScript());
            psm.setSite(publishedSite);
            psm.setUpdated(DateTime.now().toDate());
            psm.setValue(sm.getValue());
            psm.setVersion(sm.getVersion());
            psm.setWhere(sm.getWhere());

            psms.add(psm);
        }

        publishedSite.setSiteMetadatas(psms);

        publishedSiteService.saveSite(publishedSite);
        
        if (oldPublishedSite != null) {
            //y para acabr eliminamos el anterior publishedsite despues de cambiar la referencia de los medias
            Iterable<PublishedMedia> pms = publishedMediaService.listAllMediasBySite(oldPublishedSite);
            for (PublishedMedia pm : pms) {
                pm.setSite(publishedSite);
                publishedMediaService.saveMedia(pm);
            }

            publishedSiteService.deleteSite(oldPublishedSite.getId());
        }
    }

    /**
     * Este método genera una nueva revisión dummy de un sitio
     * @param s sitio del que generar la revisión
     * @return número de revisión creado
     */
    private Number createSiteRevision(Site s) {
        s.setVersion((s.getVersion() != null ? s.getVersion() : 1) + 1);

        siteService.saveSite(s);

        AuditReader reader = AuditReaderFactory.get(entityManager);
        List<Number> lista = reader.getRevisions(Site.class, s.getId());

        return lista.get(lista.size() - 1);
    }

}
