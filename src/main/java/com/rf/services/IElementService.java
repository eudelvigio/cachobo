package com.rf.services;

import com.rf.data.entities.Element;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface IElementService {

    Iterable<Element> listAllElements();

    Element getCustomElementById(Integer id);

    Element saveElement(Element e);

}
