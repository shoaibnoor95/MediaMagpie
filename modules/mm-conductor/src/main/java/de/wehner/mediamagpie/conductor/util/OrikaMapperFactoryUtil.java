package de.wehner.mediamagpie.conductor.util;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.User.UserGrantedAuthority;

public class OrikaMapperFactoryUtil {

    private static MapperFactory mapperFactory = null;
    
    public MapperFactory getOrikaMapperFactory() {
        if (mapperFactory == null) {
            mapperFactory = new DefaultMapperFactory.Builder().build();
            ClassMapBuilder<UserGrantedAuthority, UserGrantedAuthority> classMapBuilder = mapperFactory.classMap(User.UserGrantedAuthority.class,
                    User.UserGrantedAuthority.class);
            classMapBuilder.customize(new ma.glasnost.orika.CustomMapper<User.UserGrantedAuthority, User.UserGrantedAuthority>() {

                @Override
                public void mapAtoB(UserGrantedAuthority a, UserGrantedAuthority b, MappingContext context) {
                    super.mapAtoB(a, b, context);
                }

            });
            mapperFactory.registerClassMap(classMapBuilder.byDefault().toClassMap());
        }
        return mapperFactory;
    }

}
