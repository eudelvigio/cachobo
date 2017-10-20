/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rf.data.dto.blackbet.Route;
import com.rf.data.entities.Element;
import com.rf.data.entities.ElementMetaData;
import com.rf.data.entities.Media;
import com.rf.data.entities.Site;
import com.rf.data.entities.published_entities.CachedMedia;
import com.rf.data.entities.published_entities.PublishedMedia;
import com.rf.data.entities.published_entities.PublishedMediaData;
import com.rf.data.entities.published_entities.PublishedMediaDataXtra;
import com.rf.data.entities.published_entities.PublishedMediaXtra;
import com.rf.data.entities.published_entities.PublishedPage;
import com.rf.data.entities.published_entities.PublishedPageMetadata;
import com.rf.data.entities.published_entities.PublishedSite;
import com.rf.data.entities.published_entities.PublishedSiteMetadata;
import com.rf.data.enums.EnumDataType;
import com.rf.data.enums.EnumMetadata;
import com.rf.data.enums.EnumWhere;
import com.rf.services.ISiteService;
import com.rf.services.MediaService;
import com.rf.services.SiteService;
import com.rf.services.published_services.CachedMediaService;
import com.rf.services.published_services.IPublishedSiteService;
import com.rf.services.published_services.PublishedMediaService;
import com.rf.services.published_services.PublishedSiteService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

/**
 * Esta clase fuel el germen del sistema de revisiones del wanaba, ahora deprecado y traspasado a SiteController y MediaController
 * @deprecated
 * @author mortas
 */
@Controller
public class RevisionsController {

    private ISiteService siteService;

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    private IPublishedSiteService publishedSiteService;

    @Autowired
    public void setPublishedSiteService(PublishedSiteService publishedSiteService) {
        this.publishedSiteService = publishedSiteService;
    }

    private EntityManager entityManager;

    private MediaService mediaService;

    @Autowired
    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    private PublishedMediaService publishedMediaService;

    @Autowired
    public void setPublishedMediaService(PublishedMediaService publishedMediaService) {
        this.publishedMediaService = publishedMediaService;
    }

    private CachedMediaService cachedMediaService;

    @Autowired
    public void setCachedMediaService(CachedMediaService cachedMediaService) {
        this.cachedMediaService = cachedMediaService;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    @Autowired
    private TemplateEngine htmlTemplateEngine;

    @Value("${uploads_base_url}")
    private String uploads_base_url;

    @RequestMapping("revisions/{id}")
    public String showSite(@PathVariable Integer id, Model model) {
        model.addAttribute("site", siteService.getSiteById(id));

        AuditReader reader = AuditReaderFactory.get(entityManager);

        List<Number> revisions = reader.getRevisions(Site.class, id);

        List<?> revisionsCopy = revisions.subList(0, revisions.size());
        Collections.reverse(revisionsCopy);

        model.addAttribute("revisions", revisionsCopy);

        PublishedSite ps = publishedSiteService.getPublishedSiteByparent_site_id(id);

        model.addAttribute("ps", ps);

        Site s = siteService.getSiteById(id);

        Iterable<Media> medias = mediaService.listAllMediasBySite(s);

        Map<String, Iterable<Number>> mediaRevisions = new HashMap<String, Iterable<Number>>();
        for (Media m : medias) {
            Iterable<Number> listarevisiones = reader.getRevisions(Media.class, m.getId());

            mediaRevisions.put(m.getName(), listarevisiones);
            m.setSite(null);
            m.setMediaData(null);
            m.setMediaXtra(null);
        }

        model.addAttribute("medias", medias);
        model.addAttribute("revisionsMedia", mediaRevisions);

        Iterable<PublishedMedia> publishedMedias = publishedMediaService.listAllMediasBySite(ps);

        for (PublishedMedia m : publishedMedias) {
            m.setSite(null);
            m.setMediaData(null);
            m.setMediaXtra(null);
        }

        model.addAttribute("publishedMedias", publishedMedias);

        return "revisions/revisions";
    }

    @RequestMapping("revisions/{siteid}/compilemedias")
    @ResponseBody
    public String compileMedia(@PathVariable Integer siteid) throws JsonProcessingException {

        Site s = siteService.getSiteById(siteid);
        PublishedSite ps = publishedSiteService.getPublishedSiteByparent_site_id(siteid);
        ObjectMapper om = new ObjectMapper();

        String name = s.getName();

        //Iterable<Media> medias = mediaService.listAllMedias();
        Iterable<PublishedMedia> medias = publishedMediaService.listAllMediasBySite(ps);
        List<PublishedMedia> mediasFiltered = new ArrayList<>();
        Boolean bNoAnadirEste = false;

        for (PublishedMedia m : medias) {
            //m.setSite(null);
            bNoAnadirEste = false;
            for (PublishedMediaData md : m.getMediaData()) {
                for (PublishedMediaDataXtra mdx : md.getMediaDataXtra()) {
                    if (mdx.getXtraType().equals(EnumDataType.DELETION_DATE)) {

                        if (mdx.getXtraValue() != null && !mdx.getXtraValue().isEmpty()) {
                            DateTime dtTime = new DateTime(mdx.getXtraValue());

                            if (dtTime.isBeforeNow()) {
                                //Si la fecha de borrado es anterior a hoy, no debemos añadirlo
                                bNoAnadirEste = true;
                            }
                        }

                    }
                }
            }

            if (!bNoAnadirEste) {
                mediasFiltered.add(m);
            }

        }

        String compiledData = om.writeValueAsString(mediasFiltered);

        CachedMedia cm = cachedMediaService.getMediaByName(name);
        if (cm == null) {
            cm = new CachedMedia();
        }
        cm.setName(name);
        cm.setFullData(compiledData);

        cachedMediaService.saveMedia(cm);

        return "";
    }

    @RequestMapping("revisions/{siteid}/setmediapublished/{mediaid}/{mediarev}")
    @ResponseBody
    public String setmediapublished(@PathVariable Integer siteid, @PathVariable Integer mediaid, @PathVariable Integer mediarev) {
        Site s = siteService.getSiteById(siteid);
        PublishedSite ps = publishedSiteService.getPublishedSiteByparent_site_id(siteid);

        //Media m = mediaService.getMediaById(mediaid);
        Media mOfRevision = null;
        if (mediarev == -1) {
            mOfRevision = mediaService.getMediaById(mediaid);
            mediarev = createMediaRevision(mOfRevision).intValue();
        } else {
            AuditReader reader = AuditReaderFactory.get(entityManager);
            mOfRevision = reader.find(Media.class, mediaid, mediarev);
        }

        PublishedMedia pm = new PublishedMedia();

        Class classOfSite = mOfRevision.getClass();

        Class classOfPublishedSite = pm.getClass();

        Method[] methodsOfSite = classOfSite.getMethods();
        Method[] methodsOfPublishedSite = classOfPublishedSite.getMethods();
        for (Integer i = 0; i < methodsOfSite.length; i++) {
            //Los métodos empiezan siempre por el get
            //Y tenemos que tener claro que id no se setea, por ejemplo
            //versión tampoco
            //y los que son objetos hijos, no primitivas, hay que tratarlos para traducir los objetos

            if (methodsOfSite[i].getName().indexOf("get") == 0) {
                if (!Collection.class.isAssignableFrom(methodsOfSite[i].getReturnType())) {

                    if (methodsOfSite[i].getName().equals("getId")) {
                        //Si alguna de las anteriores anotaciones está presente, no deberíamos parsearlas
                        //continue;
                    }

                    String nameGetMethod = methodsOfSite[i].getName();
                    String nameSetMethod = "set" + nameGetMethod.substring(nameGetMethod.indexOf("get") + "get".length());
                    for (Integer j = 0; j < methodsOfPublishedSite.length; j++) {

                        if (methodsOfPublishedSite[j].getName().equals(nameSetMethod)) {
                            try {

                                if (methodsOfSite[i].getName().equals("getSite")) {
                                    //Si es el sitio, tenemos que cambiar para que tire del publishedsite

                                    Site auxSite = (Site) methodsOfSite[i].invoke(mOfRevision, new Object[]{});
                                    PublishedSite psAux = publishedSiteService.getPublishedSiteByparent_site_id(auxSite.getId());
                                    methodsOfPublishedSite[j].invoke(pm, psAux);

                                } else {
                                    Object o = methodsOfSite[i].invoke(mOfRevision, new Object[]{});

                                    methodsOfPublishedSite[j].invoke(pm, o);
                                }

                            } catch (Exception ex) {
                                Logger.getLogger(RevisionsController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            break;
                        }
                    }
                } else {
                    if (!methodsOfSite[i].getName().equals("getSitesOfMedia")) {
                        System.out.println("NOOOOOO PRIMITIVA!!!!!!" + methodsOfSite[i].getName());
                        parseaColecciones(methodsOfSite[i], mOfRevision, pm);
                    }

                }
                //System.out.println(nameSetMethod);
            }

        }
        pm.setParentMediaId(mediaid);
        pm.setSite(ps);
        pm.setRevisionNumber(mediarev);
        //ñapaca
        for (PublishedMediaData pmd : pm.getMediaData()) {
            pmd.setMedia(pm);
            pmd.setId(null);
            for (PublishedMediaDataXtra pmdx : pmd.getMediaDataXtra()) {
                pmdx.setId(null);
                pmdx.setMediaData(pmd);
            }
        }
        for (PublishedMediaXtra pmx : pm.getMediaXtra()) {
            pmx.setId(null);
            pmx.setMedia(pm);
        }

        PublishedMedia pmAux = publishedMediaService.getMediaByParentMediaId(mediaid);
        if (pmAux != null) {
            publishedMediaService.deleteMedia(pmAux.getId());
            //publishedSite.setId(ps.getId());
        }

        pm = publishedMediaService.saveOrUpdateMediaByName(pm);

        return "";
    }

    private Number createMediaRevision(Media m) {
        m.setVersion(m.getVersion() + 1);
        mediaService.saveMedia(m);
        AuditReader reader = AuditReaderFactory.get(entityManager);
        List<Number> lista = reader.getRevisions(Media.class, m.getId());

        return lista.get(lista.size() - 1);
    }

    @RequestMapping("revisions/{id}/createrevisionactual")
    @ResponseBody
    public String createrevisionactual(@PathVariable Integer id) {
        AuditReader reader = AuditReaderFactory.get(entityManager);
        Site s = siteService.getSiteById(id);
        s.setVersion(s.getVersion() + 1);
        siteService.saveSite(s);
        List<Number> lista = reader.getRevisions(Site.class, id);
        return lista.get(lista.size() - 1).toString();

    }

    @RequestMapping("revisions/{id}/setrevisionaspublished/{rev}")
    @ResponseBody
    public void setRevisionAsPublished(@PathVariable Integer id, @PathVariable Integer rev) {
        AuditReader reader = AuditReaderFactory.get(entityManager);

        Site sOfRevision = reader.find(Site.class, id, rev);

        PublishedSite publishedSite = new PublishedSite();
        publishedSite.setRevisionNumber(rev);
        publishedSite.setParentSiteId(id);

        Class classOfSite = sOfRevision.getClass();

        Class classOfPublishedSite = publishedSite.getClass();

        Method[] methodsOfSite = classOfSite.getMethods();
        Method[] methodsOfPublishedSite = classOfPublishedSite.getMethods();
        for (Integer i = 0; i < methodsOfSite.length; i++) {
            //Los métodos empiezan siempre por el get
            //Y tenemos que tener claro que id no se setea, por ejemplo
            //versión tampoco
            //y los que son objetos hijos, no primitivas, hay que tratarlos para traducir los objetos

            if (methodsOfSite[i].getName().indexOf("get") == 0) {
                if (!Collection.class.isAssignableFrom(methodsOfSite[i].getReturnType())) {

                    if (methodsOfSite[i].getName().equals("getId")) {
                        //Si alguna de las anteriores anotaciones está presente, no deberíamos parsearlas
                        //continue;
                    }

                    String nameGetMethod = methodsOfSite[i].getName();
                    String nameSetMethod = "set" + nameGetMethod.substring(nameGetMethod.indexOf("get") + "get".length());
                    for (Integer j = 0; j < methodsOfPublishedSite.length; j++) {
                        if (methodsOfPublishedSite[j].getName().equals(nameSetMethod)) {
                            try {
                                Object o = methodsOfSite[i].invoke(sOfRevision, new Object[]{});

                                methodsOfPublishedSite[j].invoke(publishedSite, o);
                            } catch (Exception ex) {
                                Logger.getLogger(RevisionsController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            break;
                        }
                    }
                } else {
                    if (!methodsOfSite[i].getName().equals("getSitesOfMedia")) {
                        System.out.println("NOOOOOO PRIMITIVA!!!!!!" + methodsOfSite[i].getName());
                        parseaColecciones(methodsOfSite[i], sOfRevision, publishedSite);

                    }

                }

                //System.out.println(nameSetMethod);
            }

        }

        PublishedSite ps = publishedSiteService.getPublishedSiteByparent_site_id(id);
        if (ps != null) {
            Iterable<PublishedMedia> pms = publishedMediaService.listAllMediasBySite(ps);

            for (PublishedMedia pm : pms) {
                pm.setSite(null);
                pm.setParentMediaId(null);
                /*publishedMediaService.saveMedia(pm);
                pm = publishedMediaService.getMediaById(pm.getId());
                if (pm != null) {
                    publishedMediaService.deleteMedia(pm.getId());
                }*/
                //No estoy pudiendo eliminarlos
            }

            publishedSiteService.deleteSite(ps.getId());
            //publishedSite.setId(ps.getId());
        }
        try {

            publishedMediaService.deleteMediaList(publishedMediaService.listAllMediasBySite(null));
        } catch (Exception e) {
            Logger.getLogger(RevisionsController.class.getName()).log(Level.SEVERE, null, e);
        }

        publishedSite = publishedSiteService.saveSite(publishedSite);

        /*publishedSite.setDomain(sOfRevision.getDomain());
        publishedSite.setGlobal_css(sOfRevision.getGlobal_css());
        publishedSite.set*/
    }

    //private 
    private void parseaColecciones(Method getOfCollectionMethodOfSite, Object oOfRevision, Object oToPublish) {
        try {
            System.out.println("parseaColecciones!!!!!!" + getOfCollectionMethodOfSite.getName());
            String nameGetMethod = getOfCollectionMethodOfSite.getName();
            String nameSetMethod = "set" + nameGetMethod.substring(nameGetMethod.indexOf("get") + "get".length());

            Method setOfCollectionMethodOfPublishedSite = null;

            Class publishedSiteClass = oToPublish.getClass();

            Class elementOfPublishedCollectionClass = null;

            for (Method m : publishedSiteClass.getMethods()) {
                if (m.getName().equals(nameSetMethod)) {
                    setOfCollectionMethodOfPublishedSite = m;
                }
                if (m.getName().equals(nameGetMethod)) {

                    ParameterizedType collectionType = (ParameterizedType) m.getGenericReturnType();

                    Type typeArguments = collectionType.getActualTypeArguments()[0];

                    elementOfPublishedCollectionClass = (Class) typeArguments;
                }
            }
            Collection<?> collectionOfSite = (Collection<?>) getOfCollectionMethodOfSite.invoke(oOfRevision, new Object[]{});

            //Aqui ya tengo la coleccion del sitio. Habría que recorrerla, y por cada elemento ir creando otro del correspondiente para añadirlo
            Collection<Object> collectionToPublishedSite = new ArrayList<>();
            for (Object oFrom : collectionOfSite) {

                Method[] methodsOfSite = oFrom.getClass().getMethods();

                //elementOfPublishedCollectionClass.get
                Object oTo = elementOfPublishedCollectionClass.newInstance();
                for (Integer i = 0; i < methodsOfSite.length; i++) {
                    if (!Collection.class.isAssignableFrom(methodsOfSite[i].getReturnType())) {

                        /* if (methodsOfSite[i].getName().equals("getId") ||
                                    methodsOfSite[i].getName().equals("getVersion"))
                            {
                                //Si alguna de las anteriores anotaciones está presente, no deberíamos parsearlas
                                continue;
                            }*/
                        //No soy colección y tengo tós mis campos. A ASIGNAR!   
                        Method[] methodsTo = oTo.getClass().getDeclaredMethods();
                        for (Method mTo : methodsTo) {
                            if (methodsOfSite[i].getName().indexOf("get") == 0 && mTo.getName().equals("set" + methodsOfSite[i].getName().substring("get".length()))) {

                                Object o = methodsOfSite[i].invoke(oFrom, new Object[]{});

                                if (o != null) {
                                    if (mTo.getParameterTypes()[0].getName().equals(methodsOfSite[i].getReturnType().getName())) {
                                        mTo.invoke(oTo, o);
                                    } else {
                                        //Aqui hay q parsear objeto
                                    }
                                }
                                break;
                            }
                        }

                    } else {
                        parseaColecciones(methodsOfSite[i], oFrom, oTo);
                    }
                }

                collectionToPublishedSite.add(oTo);

            }

            setOfCollectionMethodOfPublishedSite.invoke(oToPublish, collectionToPublishedSite);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
            Logger.getLogger(RevisionsController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @RequestMapping(value = "revisions/{id}/showhtml", produces = MediaType.TEXT_HTML)
    @ResponseBody
    public String showHtml(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "revNumber", required = false) Integer revNumber) {
        PublishedSite s = publishedSiteService.getPublishedSiteByparent_site_id(id);

        String html = getHtmlFromSite(request, response, s, revNumber);

        return html;
    }

    @RequestMapping("revisions/{id}/gethtml")
    public void getHtml(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response) {
        PublishedSite s = publishedSiteService.getPublishedSiteByparent_site_id(id);
        String html = getHtmlFromSite(request, response, s, null);
        try {
            InputStream is = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));

            response.setHeader("Content-Disposition", "attachment; filename=index.html");
            IOUtils.copy(is, response.getOutputStream());
            //return "blackbet";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getMetadatasOfSiteBy(PublishedSite s, EnumMetadata key, EnumWhere where) {
        List<String> lista = new ArrayList<>();

        List<PublishedSiteMetadata> metadatas = s.getSiteMetadatas();

        //List<PublishedSiteMetadata> metadatas = s.getPublishedSiteMetadatas();
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
                        if (key.equals(EnumMetadata.HTML)) {
                            lista.add(StringEscapeUtils.unescapeHtml4(metadata.getValue()));
                        } else {
                            //lista.add(metadata.getValue());
                            //Esto aqui estaba mal, hacía que se añadiese css en js
                        }
                    } else {
                        if (key.equals(EnumMetadata.CSS) && metadata.getKey().equals(key)) {
                            //Si no hay where ni tampoco elementmetadata, debería ser el css
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

        return new ArrayList(new LinkedHashSet(lista));
    }

    private List<String> getDataOfDependencies(List<Element> dependencies, EnumMetadata key, EnumWhere where) {
        List<String> lista = new ArrayList<>();
        for (Element e : dependencies) {
            for (ElementMetaData emd : e.getMetaDatas()) {
                if (emd != null && emd.getKey().equals(key) && emd.getWhere().equals(where)) {
                    if (emd != null && emd.getStorage() != null) {
                        //Aqui hay algo raruno

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

    @Value("${bo_base_url}")
    private String bo_base_url;

    private String getBoBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://"
                + // "http" + "://
                request.getServerName()
                + // "myhost"
                ":" + request.getServerPort() + "/"; // ":" + "8080"
    }

    private String getHtmlFromSite(HttpServletRequest request, HttpServletResponse response, PublishedSite s, Integer revision) {
        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale());

        String boBaseUrl = bo_base_url;//getBoBaseUrl(request);

        ctx.setVariable("title", s.getName());
        ctx.setVariable("baseUrl", s.getDomain());
        ctx.setVariable("boBaseUrl", boBaseUrl);
        ctx.setVariable("rev", "");

        if (revision != null) {
            AuditReader reader = AuditReaderFactory.get(entityManager);

            s = reader.find(PublishedSite.class, s.getId(), revision);

            ctx.setVariable("rev", "[" + revision + "]");
        }

        ctx.setVariable("site", s);

        ctx.setVariable("verticalBar", "|");

        List<String> JS_LIBS = getMetadatasOfSiteBy(s, EnumMetadata.JS, EnumWhere.LIBRARY);
        List<String> CSS_LIBS = getMetadatasOfSiteBy(s, EnumMetadata.CSS, EnumWhere.LIBRARY);
        List<String> JS_SCRIPTS = getMetadatasOfSiteBy(s, EnumMetadata.JS, EnumWhere.SCRIPT);
        List<String> CSS_SCRIPTS = getMetadatasOfSiteBy(s, EnumMetadata.CSS, EnumWhere.SCRIPT);

        ctx.setVariable("JS_LIBS", JS_LIBS);
        ctx.setVariable("CSS_LIBS", CSS_LIBS);
        ctx.setVariable("JS_SCRIPTS", JS_SCRIPTS);
        ctx.setVariable("CSS_SCRIPTS", CSS_SCRIPTS);

        String HEADER = getHeader(s);
        String FOOTER = getFooter(s);

        ctx.setVariable("HEADER", HEADER);
        ctx.setVariable("FOOTER", FOOTER);

        List<Route> routes = new ArrayList<Route>();
        for (PublishedPage p : s.getPages()) {
            Route e = new Route();
            e.setRoute(p.getRoute());
            e.setName(p.getName());
            for (PublishedPageMetadata pm : p.getMetaDatas()) {
                if (pm.getKey() == EnumMetadata.HTML) {
                    e.setTemplateUrl(pm.getValue());
                }
            }
            routes.add(e);
        }
        ctx.setVariable("routes", routes);

        ctx.setVariable("uploads_base_url_with_asterisk", uploads_base_url + "**");

        String html = "";
        html = htmlTemplateEngine.process("blackbet/index", ctx);
        html = html.replaceAll("&#39;", "'");

        return html;
    }

    private String getHeader(PublishedSite s) {
        for (PublishedSiteMetadata smd : s.getSiteMetadatas()) {
            if (smd.getWhere() == EnumWhere.HEADER) {
                return smd.getValue();
            }
        }
        return "";
    }

    private String getFooter(PublishedSite s) {
        for (PublishedSiteMetadata smd : s.getSiteMetadatas()) {
            if (smd.getWhere() == EnumWhere.FOOTER) {
                return smd.getValue();
            }
        }
        return "";
    }

}
