package com.example.config;

import com.sun.faces.config.FacesInitializer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class JsfConfig implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        sc.setInitParameter("javax.faces.WEBAPP_RESOURCES_DIRECTORY", "/META-INF/views");
        sc.setInitParameter("primefaces.CLIENT_SIDE_VALIDATION", "true");
        sc.setInitParameter("primefaces.THEME", "bootstrap");
        sc.setInitParameter("javax.faces.FACELETS_SKIP_COMMENTS", "true");
        sc.setInitParameter("primefaces.FONT_AWESOME", "true");
        sc.setInitParameter("javax.faces.DEFAULT_SUFFIX", ".xhtml");
        sc.setInitParameter("javax.faces.PARTIAL_STATE_SAVING_METHOD", "true");
        sc.setInitParameter("javax.faces.PROJECT_STAGE", "Development");
        sc.setInitParameter("facelets.DEVELOPMENT", "true");
        sc.setInitParameter("javax.faces.FACELETS_REFRESH_PERIOD", "1");
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        FacesServlet servlet = new FacesServlet();

        ServletRegistrationBean registration = new ServletRegistrationBean(servlet, "*.xhtml") {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                FacesInitializer facesInitializer = new FacesInitializer();

                Set<Class<?>> classes = new HashSet<>();
                classes.add(JsfConfig.class);
                facesInitializer.onStartup(classes, servletContext);
            }
        };
        registration.addUrlMappings("*.xhtml");
        registration.setEnabled(true);
        registration.setLoadOnStartup(1);
        return registration;
    }

}
