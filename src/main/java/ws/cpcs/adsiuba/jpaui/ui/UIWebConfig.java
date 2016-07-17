package ws.cpcs.adsiuba.jpaui.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import ws.cpcs.adsiuba.jpaui.ui.templates.Templates;

@Configuration
@EnableSpringDataWebSupport
public class UIWebConfig {

    @Bean
    Templates templates() {
        return new Templates();
    }
}
