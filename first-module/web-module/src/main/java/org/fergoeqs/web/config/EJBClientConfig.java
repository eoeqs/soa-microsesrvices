package org.fergoeqs.web.config;

import org.fergoeqs.service.OrganizationServiceRemote;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

@Singleton
@Startup
public class EJBClientConfig {

    private volatile OrganizationServiceRemote organizationServiceRemote;

    private Context context;

    private static final String JNDI_NAME =
            "ejb:/organization-ejb/OrganizationServiceImpl!org.fergoeqs.service.OrganizationServiceRemote";

    @PostConstruct
    public void init() {
        try {
            context = new InitialContext(buildJndiProperties());
            System.out.println("[EJB-CLIENT] Lookup: " + JNDI_NAME);

            Object obj = context.lookup(JNDI_NAME);

            if (!(obj instanceof OrganizationServiceRemote)) {
                throw new NamingException(
                        "JNDI lookup did not return OrganizationServiceRemote. Got: " +
                                (obj == null ? "null" : obj.getClass().getName())
                );
            }

            organizationServiceRemote = (OrganizationServiceRemote) obj;

            System.out.println("[EJB-CLIENT] Remote EJB proxy initialized: "
                    + organizationServiceRemote.getClass().getName());

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize EJB remote client", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (context != null) {
            try {
                context.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
    }

    public OrganizationServiceRemote getOrganizationServiceRemote() {
        OrganizationServiceRemote svc = organizationServiceRemote;
        if (svc == null) {
            throw new IllegalStateException("EJB Remote service is not initialized.");
        }
        return svc;
    }

    private Properties buildJndiProperties() {
        String ejbServerHost = getEnv("EJB_SERVER_HOST", "ejb-server");
        String ejbServerPort = getEnv("EJB_SERVER_PORT", "8080");
        String providerUrl = "http-remoting://" + ejbServerHost + ":" + ejbServerPort;

        String ejbUsername = getEnv("EJB_USERNAME", "admin");
        String ejbPassword = getEnv("EJB_PASSWORD", "admin");

        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, providerUrl);

        props.put(Context.SECURITY_PRINCIPAL, ejbUsername);
        props.put(Context.SECURITY_CREDENTIALS, ejbPassword);

        System.out.println("[EJB-CLIENT] Provider URL: " + providerUrl);
        System.out.println("[EJB-CLIENT] Username: " + ejbUsername);

        return props;
    }

    private String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}
