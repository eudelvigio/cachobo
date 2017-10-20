/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.configuration.prefijators;

import java.io.Serializable;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;

/**
 *
 * @author mortas
 * Esta clase se encargaba de prefijar las tablas de bbdd para local
 * @deprecated 
 */
public class LocalPrefijatorNamingStrategy extends SpringPhysicalNamingStrategy implements Serializable {
    /*private static final long serialVersionUID = 1L;
    
    
    private final static String PREFIX = "LOCAL_";

    public static final PhysicalNamingStrategyStandardImpl INSTANCE = new PhysicalNamingStrategyStandardImpl();

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        return new Identifier(addPrefix(name.getText()), name.isQuoted());
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        return new Identifier(name.getText(), name.isQuoted());
    }

    private String addPrefix(final String composedTableName) {
        return PREFIX
                + composedTableName;

    }*/

}
