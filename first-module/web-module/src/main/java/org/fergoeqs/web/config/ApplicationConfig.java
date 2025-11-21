package org.fergoeqs.web.config;

import java.util.HashSet;
import java.util.Set;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(org.fergoeqs.web.rest.OrganizationResource.class);
        return classes;
    }
}