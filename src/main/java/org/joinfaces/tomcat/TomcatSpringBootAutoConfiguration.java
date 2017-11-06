package org.joinfaces.tomcat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatSpringBootAutoConfiguration {
    private JsfTomcatContextCustomizer customizer = new JsfTomcatContextCustomizer();

    @Bean
    public JsfTomcatApplicationListener jsfTomcatApplicationListener() {
        return JsfTomcatApplicationListener.builder().context(this.customizer.getContext()).build();
    }
}
