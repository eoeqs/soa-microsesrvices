package org.fergoeqs.web.config;

import org.fergoeqs.web.provider.ObjectMapperProvider;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(org.fergoeqs.web.rest.OrganizationResource.class);
        classes.add(ObjectMapperProvider.class);
        return classes;
    }
}