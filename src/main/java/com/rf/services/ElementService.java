package com.rf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.Element;
import com.rf.data.repositories.ElementRepository;

/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class ElementService implements IElementService {

    private ElementRepository elementRepository;

    @Autowired
    public void setProductRepository(ElementRepository elementRepository) {
        this.elementRepository = elementRepository;
    }

    @Override
    public Iterable<Element> listAllElements() {
        return elementRepository.findAll();
    }

    @Override
    public Element getCustomElementById(Integer id) {
        return elementRepository.findOne(id);
    }

    @Override
    public Element saveElement(Element e) {
        return elementRepository.save(e);
    }
}
