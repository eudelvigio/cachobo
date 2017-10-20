package com.rf.configuration.prefijators;

import java.io.Serializable;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;

/**
 *
 * @author mortas
 * Esta clase se encargaba de prefijar las tablas de bbdd para pre
 * @deprecated 
 */
public class PrePrefijatorNamingStrategy extends SpringPhysicalNamingStrategy implements Serializable {
    //Si no toco nada digo yo q seguirá igual
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
