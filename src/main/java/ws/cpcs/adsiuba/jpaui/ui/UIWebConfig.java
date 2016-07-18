package ws.cpcs.adsiuba.jpaui.ui;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport
@ComponentScan("ws.cpcs.adsiuba.jpaui.ui.templates")
public class UIWebConfig {
}
