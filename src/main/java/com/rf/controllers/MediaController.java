package com.rf.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rf.data.entities.Media;
import com.rf.data.entities.MediaData;
import com.rf.data.entities.MediaDataXtra;
import com.rf.data.entities.MediaXtra;
import com.rf.data.entities.Site;
import com.rf.data.entities.SiteSensitiveDatas;
import com.rf.data.entities.filemanager.File;
import com.rf.data.entities.filemanager.Folder;
import com.rf.data.entities.published_entities.CachedMedia;
import com.rf.data.entities.published_entities.PublishedMedia;
import com.rf.data.entities.published_entities.PublishedMediaData;
import com.rf.data.entities.published_entities.PublishedMediaDataXtra;
import com.rf.data.entities.published_entities.PublishedMediaXtra;
import com.rf.data.entities.published_entities.PublishedSite;
import com.rf.data.enums.EnumDataType;
import com.rf.data.repositories.FileManager.FileRepository;
import com.rf.data.repositories.FileManager.FolderRepository;
import com.rf.filemanager.FileManager;
import com.rf.services.MediaDataService;
import com.rf.services.MediaService;
import com.rf.services.SiteSensitiveDatasService;
import com.rf.services.SiteService;
import com.rf.services.filemanager.FileService;
import com.rf.services.filemanager.FolderService;
import com.rf.services.published_services.CachedMediaService;
import com.rf.services.published_services.IPublishedSiteService;
import com.rf.services.published_services.PublishedMediaService;
import com.rf.services.published_services.PublishedSiteService;
import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.io.FileUtils;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MediaController {

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

    private SiteSensitiveDatasService siteSensitiveDatasService;


    private IPublishedSiteService publishedSiteService;

    private PublishedMediaService publishedMediaService;

    private SiteService siteService;

    private CachedMediaService cachedMediaService;


    private EntityManager entityManager;


    private MediaService mediaService;

    private MediaDataService mediaDataService;
    @Autowired
    public void setSiteSensitiveDatasService(SiteSensitiveDatasService siteSensitiveDatasService) {
        this.siteSensitiveDatasService = siteSensitiveDatasService;
    }
    @Autowired
    public void setPublishedSiteService(PublishedSiteService publishedSiteService) {
        this.publishedSiteService = publishedSiteService;
    }
    @Autowired
    public void setPublishedMediaService(PublishedMediaService publishedMediaService) {
        this.publishedMediaService = publishedMediaService;
    }
    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
    @Autowired
    public void setCachedMediaService(CachedMediaService cachedMediaService) {
        this.cachedMediaService = cachedMediaService;
    }
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    @Autowired
    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Autowired
    public void setMediaDataService(MediaDataService mediaDataService) {
        this.mediaDataService = mediaDataService;
    }

    /**
     * Elimina un mediadata por su id
     * @param id id del mediadata a eliminar
     * @return ok
     */
    @ResponseBody
    @RequestMapping("medias/deletemediadata/{id}")
    public String deleteMediaData(@PathVariable Integer id) {

        MediaData md = mediaDataService.getMediaDataById(id);

        md.getMedia().getMediaData().remove(md);
        md.setMedia(null);

        mediaDataService.deleteMediaData(id);

        return "ok";
    }

    /**
     * Asigna un media a un sitio, de manera que al calcular los servicios del sitio, encuentre a este media
     * @param mediaid id del media
     * @param siteid id del sitio
     * @return ok
     */
    @ResponseBody
    @RequestMapping("medias/servemedia/{mediaid}/tosite/{siteid}")
    public String serveMediaToSite(@PathVariable Integer mediaid, @PathVariable Integer siteid) {
        Media m = mediaService.getMediaById(mediaid);
        Site s = siteService.getSiteById(siteid);
        m.getSitesOfMedia().add(s);

        mediaService.saveMedia(m);

        return "ok";
    }

    /**
     * Elimina la asignación de un media a un sitio, de manera que al calcular los servicios, deje de encontrar a este media
     * @param mediaid id del media
     * @param siteid id del sitio
     * @return ok
     */
    @ResponseBody
    @RequestMapping("medias/servemedia/{mediaid}/quitsite/{siteid}")
    public String serveMediaQuitSite(@PathVariable Integer mediaid, @PathVariable Integer siteid) {
        Media m = mediaService.getMediaById(mediaid);
        Site s = null;
        for (Site sEx : m.getSitesOfMedia()) {
            if (sEx.getId().equals(siteid)) {
                s = sEx;
            }
        }
        m.getSitesOfMedia().remove(s);

        mediaService.saveMedia(m);

        return "ok";
    }

    /**
     * Devuelve los mediaXtra de un media en json para su edición
     * @param id id del media
     * @return json representativo del mediaXtra
     */
    @ResponseBody
    @RequestMapping("medias/getmediaxtra/{id}")
    public Object getMediaXtra(@PathVariable Integer id) {
        Media m = mediaService.getMediaById(id);

        return m.getMediaXtra();
    }

    /**
     * Actualiza los contenidos de los mediaXtra de un media desde el formulario
     * @param params colección clave-valor de datos enviados por el formulario
     * @param id id del mediaXtra
     * @return 
     */
    @ResponseBody
    @RequestMapping(value = "medias/setmediaxtra/{id}", method = RequestMethod.POST)
    public Object setMediaXtra(@RequestParam MultiValueMap<String, String> params, @PathVariable Integer id) {
        Media m = mediaService.getMediaById(id);

        for (MediaXtra mx : m.getMediaXtra()) {

            if (params.containsKey(mx.getXtraKey())) {
                mx.setXtraValue(params.getFirst(mx.getXtraKey()));
            }

        }

        m = mediaService.saveMedia(m);
        return m.getMediaXtra();
    }

    /**
     * Devuelve el json de un mediaData para su edición 
     * @param id id del mediadata para su edición
     * @return representación en JSON del mediaData
     */
    @ResponseBody
    @RequestMapping("medias/getmediadata/{id}")
    public Object getMediaData(@PathVariable Integer id) {

        MediaData md = mediaDataService.getMediaDataById(id);

        return md;
    }

    /**
     * Obtiene el mediaData default para un media
     * @param id id del media del que se obtendrá su mediadata default
     * @return json representativo del mediadata default
     */
    @ResponseBody
    @RequestMapping("medias/getdefaultmediadatabymediadata/{id}")
    public Object getMediaDataDefault(@PathVariable Integer id) {

        MediaData md = mediaDataService.getMediaDataById(id);

        for (MediaData mdDefault : md.getMedia().getMediaData()) {
            if (mdDefault.getIsDefaultMediaData()) {
                return mdDefault;
            }
        }

        return null;
    }

    /**
     * Crea un nuevo mediadata a partir del mediadata default del media. Hace uso de la librería XStream, 
     * que se encarga de serializar un objeto, de manera que con la serialización y deserialización se obtenga 
     * un nuevo objeto, sin ninguna referencia al anterior
     * @param id id del media dónde se añadirá el mediadata
     * @return OK si todo bien, KO si fallo
     */
    @ResponseBody
    @RequestMapping("medias/createmediadata/{id}")
    public Object createMediaDataXtra(@PathVariable Integer id) {

        Media m = mediaService.getMediaById(id);

        MediaData defaultMediaData = null;

        for (MediaData md : m.getMediaData()) {
            if (md != null && md.getIsDefaultMediaData() != null && md.getIsDefaultMediaData()) {
                defaultMediaData = md;
            }
        }
        if (defaultMediaData != null) {
            for (MediaDataXtra mdx : defaultMediaData.getMediaDataXtra()) {
                mdx.setXtraDescription(mdx.getXtraDescription());
            }

            XStream xs = new XStream();
            //IMPORTANTE la autodetección de anotaciones, para no pasar datos de ID's de las entidades
            xs.autodetectAnnotations(true);

            MediaData newMediaData = (MediaData) xs.fromXML(xs.toXML(defaultMediaData));

            newMediaData.setMedia(m);
            newMediaData.setIsDefaultMediaData(false);
            m.getMediaData().add(newMediaData);

            for (MediaDataXtra mdx : newMediaData.getMediaDataXtra()) {
                mdx.setMediaData(newMediaData);
                mdx.setXtraValue("");
                mdx.setIsDefaultXtraData(false);
            }

            //mediaDataService.saveMediaData(newMediaData);
            mediaService.saveMedia(m);
            return "OK";
        } else {
            return "KO";
        }

    }

    /**
     * Actualiza los contenidos de un mediaData de un media desde el formulario
     * @param params colección clave-valor de datos enviados por el formulario
     * @param id id del mediaData
     * @return JSON representativo del mediaData
     */
    @ResponseBody
    @RequestMapping(value = "medias/setmediadata/{id}", method = RequestMethod.POST)
    public Object setMediaData(@RequestParam MultiValueMap<String, String> params, @PathVariable Integer id, HttpServletRequest request) {
        MediaData md = mediaDataService.getMediaDataById(id);
        for (MediaDataXtra mdx : md.getMediaDataXtra()) {
            if (params.containsKey(mdx.getXtraKey())) {

                mdx.setXtraValue(params.getFirst(mdx.getXtraKey()));

            }
        }
        if (params.containsKey("publicationDate") && !params.getFirst("publicationDate").isEmpty()) {
            DateTime dtPublication = DateTime.parse(params.getFirst("publicationDate"));
            md.setPublication(dtPublication.toDate());
        }

        if (params.containsKey("expirationDate") && !params.getFirst("expirationDate").isEmpty()) {
            DateTime dtExpiration = DateTime.parse(params.getFirst("expirationDate"));
            md.setExpiration(dtExpiration.toDate());
        }

        if (params.containsKey("deletionDate") && !params.getFirst("deletionDate").isEmpty()) {
            DateTime dtDeletion = DateTime.parse(params.getFirst("deletionDate"));
            md.setDeletion(dtDeletion.toDate());
        }

        mediaDataService.saveMediaData(md);

        return md;
    }

    /**
     * Este método se encarga de actualizar la estructura de media-mediaXtra, y de media-mediaData-mediaDataXtra 
     * a partir del json definitorio que se añade al media en el formulario
     * @param oldMedia El media que existía en bbdd, en caso de que existiese
     * @param newMedia El media tal como viene desde el formulario, sin persistir aún
     * @return El Media sin persistir en BBDD, ya con la estructura final
     * @throws IOException 
     */
    private Media setStructureByJson(Media oldMedia, Media newMedia) throws IOException {
        Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Empezando con la comprobación de estructura del json definitorio");
        String json = newMedia.getDefinitionJson();

        if (oldMedia != null && oldMedia.getDefinitionJson() != null) {
            //Tenemos que crear o recrear el media
            Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Media existente, comprobando el json definitorio");

            if (!oldMedia.getDefinitionJson().equals(json)) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode actualObj = null;

                actualObj = mapper.readTree(json);
                //en este caso, el media existía pero el json es distinto, así que hay que actualizar
                Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Media existente, actualización de estructura");

                oldMedia.setDefinitionJson(json);
                oldMedia.setName(newMedia.getName());

                oldMedia = updateMediaXtraOf(oldMedia, actualObj);
                oldMedia = updateMediaDataOf(oldMedia, actualObj);
                //newMedia = mediaService.saveMedia(oldMedia);
                return oldMedia;
            } else {
                //en este caso, el media existía y el json es el mismo, asín q devolvemos directamente
                Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Media existente con el mismo json, na que hacer");
                return oldMedia;
            }
        } else {
            //es algo nuevo, más facil
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = null;

            actualObj = mapper.readTree(json);
            Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Media no existente, creación de estructura");
            newMedia = setUpMediaXtraOf(newMedia, actualObj);
            newMedia = setUpMediaDataOf(newMedia, actualObj);
            //newMedia = mediaService.saveMedia(newMedia);
            return newMedia;
        }
    }

    /**
     * Este método crea la estructura de MediaXtra de un media a partir de su JSON definitorio
     * @param m Media en el que crear la estructura
     * @param actualObj Nodo Json padre del JSON definitorio
     * @return Media con la estructura de mediaXtras creadas
     */
    private Media setUpMediaXtraOf(Media m, JsonNode actualObj) {
        Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Creando estructura mediaxtras");
        Iterator<Map.Entry<String, JsonNode>> nodes = actualObj.fields();

        while (nodes.hasNext()) {
            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes.next();

            if (!entry.getValue().isArray()) {
                //Si no soy array, son datos que dependen directamente de media, asín que lo meto en mediaXtra

                Iterator<Map.Entry<String, JsonNode>> mediaXtras = entry.getValue().fields();
                while (mediaXtras.hasNext()) {

                    Map.Entry<String, JsonNode> mediaXtraJson = (Map.Entry<String, JsonNode>) mediaXtras.next();

                    MediaXtra mx = new MediaXtra();
                    mx.setMedia(m);
                    mx.setVersion(0);
                    mx.setXtraKey(mediaXtraJson.getKey());

                    mx.setIsDefaultXtraData(true);

                    mx.setXtraValue(mediaXtraJson.getValue().findValue("value").asText());
                    mx.setXtraDescription(mediaXtraJson.getValue().findValue("description").asText());
                    mx.setXtraType(EnumDataType.valueOf(mediaXtraJson.getValue().findValue("type").asText()));

                    //mx.setXtraValue(mediaXtraJson.getValue().asText());
                    Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "MediaXtra [{0}][{1}][{2}]",
                            new Object[]{
                                mediaXtraJson.getValue().findValue("value").asText(),
                                mediaXtraJson.getValue().findValue("description").asText(),
                                mediaXtraJson.getValue().findValue("type").asText()
                            }
                    );

                    Boolean existo = false;

                    for (MediaXtra comprobator : m.getMediaXtra()) {
                        if (comprobator.getXtraKey().equals(mx.getXtraKey()) && comprobator.getIsDefaultXtraData().equals(mx.getIsDefaultXtraData())) {

                            Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Comprobando, pero al estar creando no debería pasar por aqui");

                            comprobator.setXtraType(mx.getXtraType());
                            comprobator.setXtraValue(mx.getXtraValue());

                            existo = true;
                            break;
                        }
                    }
                    if (!existo) {
                        Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Añadiendo");
                        m.getMediaXtra().add(mx);
                    }
                }
            }
        }
        return m;
    }
    
    /**
     * Este método crea la estructura de MediaData-MediaDataXtra de un media a partir de su JSON definitorio
     * @param m Media en el que crear la estructura
     * @param actualObj Nodo Json padre del JSON definitorio
     * @return Media con la estructura de mediaDatas y mediaDataXtras creadas
     */
    private Media setUpMediaDataOf(Media m, JsonNode actualObj) {
        Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Creando estructura mediadatas");
        Iterator<Map.Entry<String, JsonNode>> nodes = actualObj.fields();
        while (nodes.hasNext()) {
            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes.next();

            if (entry.getValue().isArray()) {
                for (final JsonNode objNode : entry.getValue()) {
                    MediaData md = new MediaData();
                    md.setCreated(DateTime.now().toDate());
                    md.setMedia(m);
                    md.setVersion(0);
                    md.setIsDefaultMediaData(true);

                    //Seteamos un identificador el cual vamos a asignar temporalmente al mediadata, de manera que luego podamos saber cual es una vez guardado
                    String identifier = "itsme";
                    md.setObjValue(identifier);

                    m.getMediaData().add(md);

                    for (MediaData mdsearch : m.getMediaData()) {
                        if (mdsearch.getObjValue().equals(identifier)) {
                            md = mdsearch;
                            md.setObjValue("");
                            break;
                        }
                    }
                    Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Empezando a recorrer el array");

                    Iterator<Map.Entry<String, JsonNode>> mediaDataXtras = objNode.fields();
                    while (mediaDataXtras.hasNext()) {
                        Map.Entry<String, JsonNode> entryXtra = mediaDataXtras.next();
                        MediaDataXtra mdx = new MediaDataXtra();

                        mdx.setIsDefaultXtraData(true);
                        mdx.setMediaData(md);
                        mdx.setVersion(0);
                        mdx.setXtraKey(entryXtra.getKey());

                        mdx.setXtraValue(entryXtra.getValue().findValue("value").asText());
                        mdx.setXtraDescription(entryXtra.getValue().findValue("description").asText());
                        mdx.setXtraType(EnumDataType.valueOf(entryXtra.getValue().findValue("type").asText()));

                        Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "MediaDataXtra [{0}][{1}][{2}]",
                                new Object[]{
                                    entryXtra.getValue().findValue("value").asText(),
                                    entryXtra.getValue().findValue("description").asText(),
                                    entryXtra.getValue().findValue("type").asText()
                                }
                        );

                        md.getMediaDataXtra().add(mdx);

                    }

                }
            }
        }
        return m;
    }

    /**
     * Este método actualiza la estructura de MediaXtra de un media a partir de su JSON definitorio. Se crearán los campos que no existiesen anteriormente
     * y se actualizará la descripción y el tipo de dato de aquellas que sí existiesen. NO ELIMINA CAMPOS QUE DEJEN DE APARECER EN EL JSON
     * @param m Media en el que actualizar la estructura
     * @param actualObj Nodo Json padre del JSON definitorio
     * @return Media con la estructura de mediaXtras creadas
     */
    private Media updateMediaXtraOf(Media m, JsonNode actualObj) {
        Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Actualizando estructura mediaxtras");
        Iterator<Map.Entry<String, JsonNode>> nodes = actualObj.fields();
        Boolean existo = false;

        while (nodes.hasNext()) {
            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes.next();
            if (!entry.getValue().isArray()) {
                //Si no soy array, son datos que dependen directamente de media, asín que lo meto en mediaXtra

                Iterator<Map.Entry<String, JsonNode>> mediaXtras = entry.getValue().fields();
                while (mediaXtras.hasNext()) {
                    existo = false;
                    Map.Entry<String, JsonNode> mediaXtraJson = (Map.Entry<String, JsonNode>) mediaXtras.next();
                    for (MediaXtra mx : m.getMediaXtra()) {
                        if (mx.getXtraKey().equals(mediaXtraJson.getKey())) {
                            existo = true;
                            //Soy la misma key, asi que hago los cambios nesezarios
                            mx.setXtraDescription(mediaXtraJson.getValue().findValue("description").asText());
                            mx.setXtraType(EnumDataType.valueOf(mediaXtraJson.getValue().findValue("type").asText()));

                            Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "ACTUALIZANDO MediaXtra [{0}][{1}]",
                                    new Object[]{
                                        mediaXtraJson.getValue().findValue("description").asText(),
                                        mediaXtraJson.getValue().findValue("type").asText()
                                    }
                            );

                            /*if (mx.getIsDefaultXtraData()) {
                                    mx.setXtraValue(mediaXtraJson.getValue().findValue("value").asText());
                                }*/
                            //No se debe setear el value, ya que en estos el mecanismo de defaultxtradata no funciona igual, al no ser repetitivo
                        }

                    }
                    if (!existo) {
                        MediaXtra mx = new MediaXtra();
                        mx.setMedia(m);
                        mx.setVersion(0);
                        mx.setXtraKey(mediaXtraJson.getKey());

                        mx.setIsDefaultXtraData(true);

                        mx.setXtraValue(mediaXtraJson.getValue().findValue("value").asText());
                        mx.setXtraDescription(mediaXtraJson.getValue().findValue("description").asText());
                        mx.setXtraType(EnumDataType.valueOf(mediaXtraJson.getValue().findValue("type").asText()));

                        Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "ACTUALIZANDO MediaXtra NO EXISTENTE [{0}][{1}][{2}]",
                                new Object[]{
                                    mediaXtraJson.getValue().findValue("value").asText(),
                                    mediaXtraJson.getValue().findValue("description").asText(),
                                    mediaXtraJson.getValue().findValue("type").asText()
                                }
                        );

                        m.getMediaXtra().add(mx);
                    }
                }
            }
        }
        return m;
    }

    /**
     * Este método actualiza la estructura de MediaData-MediaDataXtra de un media a partir de su JSON definitorio. 
     * Se recorrerán todos los mediaDataXtra de cada mediaData
     * Se crearán todos los nuevos mediaXtras que no existiesen anteriormente en cada mediaData
     * Se actualizará la descripción y el tipo de dato de aquellos mediaDataXtras que sí existiesen en cada mediaData.
     * NO ELIMINA CAMPOS QUE DEJEN DE APARECER EN EL JSON
     * @param m Media en el que actualizar la estructura
     * @param actualObj Nodo Json padre del JSON definitorio
     * @return Media con la estructura de mediaXtras creadas
     */
    private Media updateMediaDataOf(Media m, JsonNode actualObj) {
        Iterator<Map.Entry<String, JsonNode>> nodes = actualObj.fields();
        Boolean existo = false;
        while (nodes.hasNext()) {
            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes.next();

            if (entry.getValue().isArray()) {
                //Si soy el array, soy el de los mediadataxtras
                for (final JsonNode objNode : entry.getValue()) {
                    for (MediaData md : m.getMediaData()) {
                        Iterator<Map.Entry<String, JsonNode>> mediaDataXtras = objNode.fields();
                        while (mediaDataXtras.hasNext()) {
                            existo = false;
                            Map.Entry<String, JsonNode> entryXtra = mediaDataXtras.next();
                            for (MediaDataXtra mdx : md.getMediaDataXtra()) {
                                if (mdx.getXtraKey() != null && mdx.getXtraKey().equals(entryXtra.getKey())) {
                                    existo = true;
                                    mdx.setXtraDescription(entryXtra.getValue().findValue("description").asText());
                                    mdx.setXtraType(EnumDataType.valueOf(entryXtra.getValue().findValue("type").asText()));
                                    if (mdx.getIsDefaultXtraData()) {
                                        mdx.setXtraValue(entryXtra.getValue().findValue("value").asText());
                                    }

                                }

                            }
                            if (!existo) {
                                MediaDataXtra mdx = new MediaDataXtra();

                                mdx.setIsDefaultXtraData(true);
                                mdx.setMediaData(md);
                                mdx.setVersion(0);
                                mdx.setXtraKey(entryXtra.getKey());

                                mdx.setXtraValue(entryXtra.getValue().findValue("value").asText());
                                mdx.setXtraDescription(entryXtra.getValue().findValue("description").asText());
                                mdx.setXtraType(EnumDataType.valueOf(entryXtra.getValue().findValue("type").asText()));

                                md.getMediaDataXtra().add(mdx);
                            }

                        }
                    }
                }
            }
        }
        return m;

    }

    /**
     * Método que se encarga de guardar un Media. 
     * Primero intentará obtener, si existe, el existente.
     * Después se decide si se va a hacer un cambio de estructura
     * Se procesan las url's externas en mediadatas
     * Y se guarda el media
     * @param m Media a guardar
     * @return Media guardado
     */
    private Media saveMedia(Media m) {
        Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "saveMedia!");

        if (m.getName().isEmpty()) {
            return null;
        }

        Media oldMedia = null;
        if (m.getId() != null) {
            Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Es una actualización!");
            oldMedia = mediaService.getMediaById(m.getId());
        }

        try {
            Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "setstructurebyjson!");
            m = setStructureByJson(oldMedia, m);
        } catch (IOException ex) {
            Logger.getLogger(MediaController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Aqui creo la ruta del directorio exclusivo del media
        FileManager fm = new FileManager(folderService, fileService, folderRepository, fileRepository, uploads_directory, uploads_base_url);

        //Se genera u obtiene una carpeta bajo Medias única para este media
        Folder f = fm.createOrGetFolderIfExists("/", "MEDIA");
        f = fm.createOrGetFolderIfExists(f.getName(), "MEDIA_" + m.getName());
        m.setFileManagerRoute(f.getName());

        Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Empezando con los mediadatas");

        for (MediaData md : m.getMediaData()) {
            md.setMedia(m);
            for (MediaDataXtra mdx : md.getMediaDataXtra()) {
                mdx.setMediaData(md);

                //Aqui, si nos viene una url directamente a pelo, deberíamos obtener el binario, y crear el fichero automágicamente
                if (mdx.getXtraType().equals(EnumDataType.IMAGE)
                        || mdx.getXtraType().equals(EnumDataType.PRINCIPAL_IMAGE)
                        || mdx.getXtraType().equals(EnumDataType.VIDEO)) {
                    //Si el fichero no contiene la url de uploads_base_url, hacemos la obtención y guardado
                    Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Es un fichero!");
                    Logger.getLogger(MediaController.class.getName()).log(Level.INFO, mdx.getXtraValue());
                    if (!mdx.getXtraValue().contains(uploads_base_url) && !mdx.getXtraValue().isEmpty()) {
                        Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Y está fuera de nuestra url, así que lo obtenemos y asignamos!");
                        File fileFromUrl = fm.createAndSaveFileFromUrl(mdx.getXtraValue(), f.getName());

                        if (fileFromUrl != null) {
                            mdx.setXtraValue(fileFromUrl.getUrlPath());
                        }
                    }
                }
            }
        }

        for (MediaXtra mx : m.getMediaXtra()) {
            mx.setMedia(m);
        }

        mediaService.saveMedia(m);

        return m;
    }

    /**
     * Método para guardar el media desde una llamada POST
     * @param m Media a guardar en bbdd
     * @return Si venía sin nombre, vuelve al formulario de creación. Si sí tiene nombre, se guardará en base de datos
     */
    @RequestMapping(value = "medias", method = RequestMethod.POST)
    public String saveMediaPost(Media m) {
        if (!m.getName().isEmpty()) {
            m = saveMedia(m);
            return "redirect:medias/edit/" + m.getId();
        } else {
            return "redirect:medias/new";
        }
    }

    
    /**
     * Este método crea una revisión nueva en apache Envers del media.
     * @param m Media del que crear revisión nueva
     * @return Número de la nueva revisión creada
     */
    private Number createMediaRevision(Media m) {
        m.setVersion(m.getVersion() + 1);
        mediaService.saveMedia(m);
        AuditReader reader = AuditReaderFactory.get(entityManager);
        List<Number> lista = reader.getRevisions(Media.class, m.getId());

        return lista.get(lista.size() - 1);
    }

    
    /**
     * Este método se encarga, por este orden
     * 1- Por cada sitio que tenga asignado el Media
     * 1.1- Se comprueba si dicho sitio está publicado
     * 1.1b- Si no está publicado, se escribe por log y se va al siguiente sitio
     * 1.2- Se realiza el parseo de Media-MediaXtra-MediaData-MediaDataXtra a PublishedMedia-PublishedMediaXtra-PublishedMediaData-PublishedMediaDataXtra, añadiendo el id del sitio publicado
     * 1.3- Se obtiene el json que representan todos los PublishedMedias pertenecientes al PublishedSite, aprovechando para tener en cuenta la fecha de borrado para no añadirlo y se guarda en la tabla CachedMedia con el nombre del PublishedSite
     * 1.4- Se genera el fichero Service.json a partir del json de los publishedMedias del PublishedSite
     * 1.5- En caso de que se tengan datos de FTP, se envía por FTP el fichero con el servicio
     * @param mediaid Id del media que compilar, cachear y enviar
     * @param mediarev Revisión específica que publicar
     * @return Vacío si OK
     * @throws JsonProcessingException
     * @throws IOException
     * @throws Exception 
     */
    @RequestMapping("medias/compileandcachemedia/{mediaid}/{mediarev}")
    @ResponseBody
    public String compileAndCacheMedia(@PathVariable Integer mediaid, @PathVariable Integer mediarev) throws JsonProcessingException, IOException, Exception {
        //A partir del media que hemos lanzado, se hará:
        //  Se va a parsear hacia publishedMedia el media en cuestión, o la revisión indicada, para cada sitio PUBLICADO en que aparezca el media (a partir del getMediasOfSite o viceversa)
        //  Se van a coger todos los publishedMedia de cada sitio PUBLICADO en que aparezca el media (a partir del getMediasOfSite o viceversa)
        //  Se transformará el media a json, y se publicará para cada posible sitio la versión cachedMedia
        Media mediaToCompile;
        ObjectMapper om = new ObjectMapper();

        Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Lanzado compileAndCacheMedia para el media [{0}] REV[{1}]", new Object[]{mediaid, mediarev});

        if (mediarev == -1) {
            mediaToCompile = mediaService.getMediaById(mediaid);
            mediarev = createMediaRevision(mediaToCompile).intValue();
        } else {
            AuditReader reader = AuditReaderFactory.get(entityManager);
            mediaToCompile = reader.find(Media.class, mediaid, mediarev);
        }

        Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "El media sirve a [{0}] sitios", mediaToCompile.getSitesOfMedia().size());

        for (Site s : mediaToCompile.getSitesOfMedia()) {
            PublishedSite pSite = publishedSiteService.getPublishedSiteByparent_site_id(s.getId());

            //Sólo si el sitio existe y está publicado
            if (pSite != null) {
                Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "El sitio [{0}] esta publicado con id [{1}]", new Object[]{s.getId(), pSite.getId()});

                PublishedMedia pm = new PublishedMedia();
                //Pues pasito a pasito vamos creando todos los publishedMedias para cada uno de los sitios

                pm.setCreation(DateTime.now().toDate());
                pm.setDeleted(null);
                pm.setFileManagerRoute(mediaToCompile.getFileManagerRoute());
                pm.setName(mediaToCompile.getName());
                pm.setParentMediaId(mediaToCompile.getId());
                pm.setRevisionNumber(mediarev);
                pm.setSite(pSite);
                pm.setUpdated(DateTime.now().toDate());
                pm.setVersion(mediaToCompile.getVersion());

                List<PublishedMediaXtra> pmxs = new ArrayList<>();

                for (MediaXtra mx : mediaToCompile.getMediaXtra()) {
                    PublishedMediaXtra pmx = new PublishedMediaXtra();

                    pmx.setIsDefaultXtraData(mx.getIsDefaultXtraData());
                    pmx.setMedia(pm);
                    pmx.setVersion(mx.getVersion());
                    pmx.setXtraDescription(mx.getXtraDescription());
                    pmx.setXtraKey(mx.getXtraKey());
                    pmx.setXtraType(mx.getXtraType());
                    pmx.setXtraValue(mx.getXtraValue());

                    pmxs.add(pmx);
                }

                pm.setMediaXtra(pmxs);

                List<PublishedMediaData> pmds = new ArrayList<>();

                for (MediaData md : mediaToCompile.getMediaData()) {
                    PublishedMediaData pmd = new PublishedMediaData();

                    pmd.setCreated(DateTime.now().toDate());
                    pmd.setIsDefaultMediaData(md.getIsDefaultMediaData());
                    pmd.setMedia(pm);
                    pmd.setObjAction(md.getObjAction());
                    pmd.setObjValue(md.getObjValue());
                    pmd.setPublication(md.getPublication());
                    pmd.setDeletion(md.getDeletion());
                    pmd.setExpiration(md.getExpiration());

                    pmd.setUpdated(DateTime.now().toDate());

                    List<PublishedMediaDataXtra> pmdxs = new ArrayList<>();
                    for (MediaDataXtra mdx : md.getMediaDataXtra()) {
                        PublishedMediaDataXtra pmdx = new PublishedMediaDataXtra();

                        pmdx.setIsDefaultXtraData(mdx.getIsDefaultXtraData());
                        pmdx.setMediaData(pmd);
                        pmdx.setVersion(mdx.getVersion());
                        pmdx.setXtraDescription(mdx.getXtraDescription());
                        pmdx.setXtraKey(mdx.getXtraKey());
                        pmdx.setXtraType(mdx.getXtraType());
                        pmdx.setXtraValue(mdx.getXtraValue());

                        pmdxs.add(pmdx);
                    }

                    pmd.setMediaDataXtra(pmdxs);

                    pmds.add(pmd);
                }

                pm.setMediaData(pmds);

                om.enable(SerializationFeature.INDENT_OUTPUT);
                Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Parseo de mediaToPublish a publishedMedia terminado: {0}", om.writeValueAsString(pm));
                om.disable(SerializationFeature.INDENT_OUTPUT);

                PublishedMedia oldPublished = publishedMediaService.getMediaByNameAndSite(mediaToCompile.getName(), pSite);

                if (oldPublished != null) {
                    Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Existe un antiguo publishedMedia publicado para el sitio, se eliminar\u00e1");
                    publishedMediaService.deleteMedia(oldPublished.getId());
                }

                Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Creando publishedMedia");
                publishedMediaService.saveMedia(pm);

                //Hasta aqui hemos pasado desde las clases Media a las PublishedMedia los datos para el sitio ps
                //Ahora, para el sitio se deben obtener sus publishedmedias, filtrar el deletiondate y obtener el json correspondiente, para dejarlo en cachedmedia
                Iterable<PublishedMedia> medias = publishedMediaService.listAllMediasBySite(pSite);
                Boolean bNoAnadirEste = false;

                Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Filtrando los medias por su posible delete date");

                for (PublishedMedia m : medias) {

                    List<PublishedMediaData> pmdsFiltered = new ArrayList<>();

                    for (PublishedMediaData md : m.getMediaData()) {
                        bNoAnadirEste = false;

                        for (PublishedMediaDataXtra mdx : md.getMediaDataXtra()) {

                            if (mdx.getXtraType().equals(EnumDataType.PRINCIPAL_NAME)) {
                                Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "publishedMediaData [{0}]", mdx.getXtraValue());
                                break;
                            }

                        }

                        Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Chequeando deletion_date en media [{0}] del sitio [{1}]", new Object[]{m.getName(), m.getSite().getName()});
                        if (md.getDeletion() != null) {
                            Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "El deletion_date tiene valor [{0}]", new Object[]{md.getDeletion()});
                            DateTime dtTime = new DateTime(md.getDeletion());

                            if (dtTime.isBeforeNow()) {
                                Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Lo cual es anterior a hoy, as\u00ed que no a\u00f1adiremos este publishedMediaData");
                                //Si la fecha de borrado es anterior a hoy, no debemos añadirlo
                                bNoAnadirEste = true;
                            }
                        }

                        if (!bNoAnadirEste) {
                            pmdsFiltered.add(md);
                        }
                    }

                    m.setMediaData(pmdsFiltered);

                    Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "A\u00f1adimos el publishedMedia [{0}]", m.getName());

                }
                Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Ahora se realizará el cachedMedia");

                String compiledData = om.writeValueAsString(medias);

                Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "El valor compilado es: {0}", compiledData);

                CachedMedia cm = cachedMediaService.getMediaByName(pSite.getName());
                if (cm == null) {
                    Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "No había ningún compilado para el sitio aún");
                    cm = new CachedMedia();
                }
                cm.setName(pSite.getName());
                cm.setFullData(compiledData);

                cm = cachedMediaService.saveMedia(cm);

                //y ahora subimos
                Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Y ahora subimos");

                String routeIntermediateUpload = uploads_directory + "temp_dir_to_upload" + java.io.File.separator;

                java.io.File tempDirectory = new java.io.File(routeIntermediateUpload);

                if (tempDirectory.exists()) {
                    //Eliminamos directorios
                    FileUtils.cleanDirectory(tempDirectory);
                } else {
                    //Creamos el directorio
                    tempDirectory.mkdirs();
                }
                String SERVICE = cm.getFullData();

                java.io.File mediasFile = new java.io.File(tempDirectory, "service.json");
                FileUtils.write(mediasFile, SERVICE, "UTF-8");
                Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "Escrito el fichero en la ruta intermedia");

                CountDownLatch latch = new CountDownLatch(1);

                String pathFileLocal = "file:" + tempDirectory.getAbsolutePath().replace("\\", "/") + "?recursive=true&noop=true&autoCreate=true";
                SiteSensitiveDatas ssd = siteSensitiveDatasService.getBySite(s);

                CamelContext context = new DefaultCamelContext();
                String pathFileFtp = ssd.getConnectionStringFtp();

                context.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() {
                        from(pathFileLocal)
                                .log("empiezo a subir ${file:name}")
                                .to(pathFileFtp)
                                .log("acabo de subir ${file:name}")
                                .process((Exchange exchng) -> {

                                    latch.countDown();
                                });
                    }

                });

                try {
                    context.start();
                    latch.await();
                } catch (Exception e) {

                } finally {
                    context.stop();
                }
            } else {

                Logger.getLogger(MediaController.class.getName()).log(Level.INFO, "El sitio [{0}] no está publicado", s.getId());

            }
        }

        return "";
    }

    
    /**
     * Se encarga de obtener de base de datos un media por su id, y añadirlo a modelo para editarlo en la plantilla del formulario de medias
     * @param id mediaId a editar
     * @param model modelo que inserta spring
     * @param revNumber numero de revisión a editar
     * @return ruta a la plantilla de formulario
     */
    @RequestMapping("medias/edit/{id}")
    public String edit(@PathVariable Integer id, Model model, @RequestParam(value = "revNumber", required = false) Integer revNumber) {

        Media m = mediaService.getMediaById(id);
        //Y ahora las revisiones            
        AuditReader reader = AuditReaderFactory.get(entityManager);

        List<Number> revisions = reader.getRevisions(Media.class, id);
        if (revNumber != null) {
            reader = AuditReaderFactory.get(entityManager);

            m = reader.find(Media.class, id, revNumber);

            model.addAttribute("rev", "[" + revNumber + "]");
        }

        model.addAttribute("revisions", revisions);

        model.addAttribute("media", m);

        model.addAttribute("sites", siteService.listAllSites());

        return "medias/mediaform";
    }

    /**
     * Setea en modelo los valores para la creación de un nuevo media
     * @param model Modelo que inserta spring
     * @return ruta a la plantilla de formulario
     */
    @RequestMapping("medias/new")
    public String create(Model model) {

        model.addAttribute("revisions", null);

        model.addAttribute("media", new Media());

        model.addAttribute("sites", siteService.listAllSites());

        return "medias/mediaform";
    }

    /**
     * Devuelve la lista de medias existentes según el usuario logado
     * @param model Modelo que inserta spring
     * @return ruta a la plantilla
     */
    @RequestMapping(value = "/medias", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("medias", mediaService.listAllMediasByLoggedInUsers());
        return "medias/medias";
    }

}
