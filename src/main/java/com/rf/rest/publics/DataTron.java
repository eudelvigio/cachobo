package com.rf.rest.publics;

import com.rf.data.entities.Site;
import com.rf.data.entities.live_config.LiveConfig;
import com.rf.data.entities.published_entities.CachedMedia;
import com.rf.data.entities.published_entities.PublishedMedia;
import com.rf.data.entities.userdata.ConscientUserData;
import com.rf.data.entities.userdata.UserData;
import com.rf.services.SiteService;
import com.rf.services.UserData.ConscientUserDataService;
import com.rf.services.UserData.UserDataService;
import com.rf.services.live_config.LiveConfigService;
import com.rf.services.published_services.CachedMediaService;
import com.rf.services.published_services.PublishedMediaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * El datatron es un api rest que sirve los posibles publishedmedias mediante rest para previews del sitio
 * También expone una serie de métodos para el guardado y obtención de datos del usuario, como el apuntado a promociones, juegos favoritos
 * @author mortas
 */
@Api(value="DataTron", description = "El datatron es un api rest que sirve los posibles publishedmedias mediante rest para previews del sitio. También expone una serie de métodos para el guardado y obtención de datos del usuario, como el apuntado a promociones, juegos favoritos...")
@RestController
public class DataTron {


    private LiveConfigService liveConfigService;

    @Autowired
    public void setLiveConfigService(LiveConfigService liveConfigService) {
        this.liveConfigService = liveConfigService;
    }

    private SiteService siteService;

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
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

    private UserDataService userDataService;

    @Autowired
    public void setUserDataService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    private ConscientUserDataService conscientUserDataService;

    @Autowired
    public void setConscientUserDataService(ConscientUserDataService conscientUserDataService) {
        this.conscientUserDataService = conscientUserDataService;
    }

    /**
     * Mantenido por compatibilidad con las versiones de los sitios que obtenían los servicios desde el datatron del wanaba
     * Obtiene el cachedMedia, instantánea generada al publicar los medias de un sitio, que representa todos los datos almacenados en los medias del sitio de nombre {client}
     * @param client Nombre del sitio, que se usa para buscar la instantánea
     * @return json con los medias del sitio almacenados en el cachedMediaService
     */
    @ApiOperation(value = "Mantenido por compatibilidad con las versiones de los sitios que obtenían los servicios desde el datatron del wanaba. Obtiene el cachedMedia, instantánea generada al publicar los medias de un sitio, que representa todos los datos almacenados en los medias del sitio de nombre {client}.")
    @RequestMapping(value = "/public/{client}/datatron", method = RequestMethod.GET)
    @ResponseBody
    public String getAllMediaData(@PathVariable String client) {
        CachedMedia cm = cachedMediaService.getMediaByName(client);

        return cm.getFullData();
    }

    /**
     * Este método devuelve los medias publicados en un momento dado, pero no los cacheados, de manera que sirve para hacer las previews desde wanaba
     * @param client Nombre del sitio, que se usa para buscar los medias
     * @return json con los publishedmedias del sitio solicitado
     */
    @ApiOperation(value = "Este método devuelve los medias publicados en un momento dado, pero no los cacheados, de manera que sirve para hacer las previews desde wanaba.")
    @RequestMapping(value = "/public/{client}/datatrondev", method = RequestMethod.GET)
    public Iterable<PublishedMedia> getAllMediaDataDev(@PathVariable String client) {
        //TODO: hay que devolver esto bien, que no se hace
        Iterable<PublishedMedia> medias = publishedMediaService.listAllMedias();
        for (PublishedMedia m : medias) {
            m.setSite(null);
        }

        return medias;
    }

    /**
     * Este método sirve para devolver los distintos liveconfigs que tiene un sitio, normalmente, cortinillas
     * Datos que deben ser tomados en cuenta en el mismo momento en el que se setean, como la cortinilla
     * @param client Nombre del sitio por el que se obtendrán los liveconfigs
     * @return lista de liveconfigs
     */
    @ApiOperation(value = "Este método sirve para devolver los distintos liveconfigs que tiene un sitio, normalmente, cortinillas. Datos que deben ser tomados en cuenta en el mismo momento en el que se setean, como la cortinilla.")
    @RequestMapping(value = "/public/{client}/liveconfigs", method = RequestMethod.GET)
    public Iterable<LiveConfig> getLiveConfigsOfSite(@PathVariable String client) {
        Site s = siteService.getSiteByName(client);

        Iterable<LiveConfig> liveConfigs = liveConfigService.getLiveConfigsBySite(s);

        return liveConfigs;
    }

    /**
     * Método que sirve para guardar los userData, que serían datos para trackear usuarios.
     * No usado
     * @param userData Lista de userDatas que envía cliente a servidor
     * @param request request, para obtener determinadas cabeceras
     */
    @ApiOperation(value = "Método que sirve para guardar los userData, que serían datos para trackear usuarios. No usado")
    @RequestMapping(value = "/public/{client}/datatron", method = RequestMethod.POST)
    public void obtainUserData(@RequestBody List<UserData> userData, HttpServletRequest request) {

        for (UserData ud : userData) {
            ud.setIp(request.getRemoteAddr());
            ud.setxForwardedIp(request.getHeader("X-FORWARDED-FOR"));
            ud.setUserAgent(request.getHeader("user-agent"));
        }

        userDataService.saveUsersDatas(userData);

    }

    /**
     * endpoint para guardar los datos que genera un usuario conscientemente, como pueden ser el apuntarse a promociones o la lista de favoritos
     * @param conscientUserData Lista, recibida como json y convertida por jackson en objetos java, que representan datos del usuario
     * @param client Cliente desde el que se están obteniendo los datos. Sobreescribe cualquier cliente que pudiese venir en la lista de conscientUserData
     * @param userid Clave que representa de manera única al usuario. Sobreescribe cualquier userid que pudiese venir en la lista de conscientUserData. Normalmente, usercode de MT
     */
    @ApiOperation(value = "endpoint para guardar los datos que genera un usuario conscientemente, como pueden ser el apuntarse a promociones o la lista de favoritos")
    @RequestMapping(value = "/public/{client}/datatron/{userid}", method = RequestMethod.POST)
    public void setConscientUserData(@RequestBody List<ConscientUserData> conscientUserData, @PathVariable(value = "client") String client, @PathVariable(value = "userid") String userid) {
        for (ConscientUserData cud : conscientUserData) {
            cud.setSite(client);
            cud.setUserId(userid);
        }

        conscientUserDataService.saveConscientUserDatas(conscientUserData);
    }

    /**
     * endpoint desde el que se obtiene el conscientuserdata de una determinada key para un determinado usuario
     * @param userid clave que representa de manera única al usuario
     * @param client no usado
     * @param key key que se quiere devolver
     * @return 
     */
    @ApiOperation(value = "endpoint desde el que se obtiene el conscientuserdata de una determinada key para un determinado usuario")
    @RequestMapping(value = "/public/{client}/datatron/{userid}/{key}", method = RequestMethod.GET)
    public Iterable<ConscientUserData> obtainConscientUserData(@PathVariable(value = "userid") String userid, @PathVariable(value = "client") String client, @PathVariable(value = "key") String key) {
        return conscientUserDataService.listConscientUserDatasByKeyAndUserId(key, userid);
    }

    /**
     * endpoint al que llamar para guardar un único conscientUserData
     * @param userid clave única del usuario. sobreescribe el valor que pueda venir en el objeto conscientUserData de la request
     * @param key key que se quiere devolver. sobreescribe el valor que pueda venir en el objeto conscientUserData de la request
     * @param client cliente desde el que se está guardando el conscientUserData. sobreescribe el valor que pueda venir en el objeto conscientUserData de la request
     * @param conscientUserData Resto de valores del conscientuserdata
     * @return conscientuserdata recién guardado
     */
    @ApiOperation(value = "endpoint al que llamar para guardar un único conscientUserData")
    @RequestMapping(value = "/public/{client}/datatron/{userid}/{key}", method = RequestMethod.POST)
    public ConscientUserData saveConscientUserDataByKey(@PathVariable(value = "userid") String userid, @PathVariable(value = "key") String key, @PathVariable(value = "client") String client, @RequestBody ConscientUserData conscientUserData) {
        conscientUserData.setUserId(userid);
        conscientUserData.setKey(key);
        conscientUserData.setSite(client);

        return conscientUserDataService.saveConscientUserData(conscientUserData);
    }

    /**
     * Devuelve la lista de conscientUserData "Importantes", marca que se le puede dar al guardar el conscientuserdata
     * @param userid clave que representa de manera única al usuario
     * @return lista de conscientuserdatas importantes del userid
     */
    @ApiOperation(value = "Devuelve la lista de conscientUserData \"Importantes\", marca que se le puede dar al guardar el conscientuserdata")
    @RequestMapping(value = "/public/{client}/datatron/{userid}", method = RequestMethod.GET)
    public Iterable<ConscientUserData> obtainImportantConscientUserDataOfUser(@PathVariable(value = "userid") String userid) {
        return conscientUserDataService.listConscientUserDatasByUserIdImportants(userid);
    }

    /**
     * Devuelve la lista de todos los conscientUserData, independientemente del valor de "Importante"
     * @param userid clave que representa de manera única al usuario
     * @return lista de conscientuserdatas del userid
     */
    @ApiOperation(value = "Devuelve la lista de todos los conscientUserData, independientemente del valor de \"Importante\"")
    @RequestMapping(value = "/public/{client}/datatron/{userid}/all", method = RequestMethod.GET)
    public Iterable<ConscientUserData> obtainAllConscientUserDataOfUser(@PathVariable(value = "userid") String userid) {
        return conscientUserDataService.listConscientUserDatasByUserId(userid);
    }
}
