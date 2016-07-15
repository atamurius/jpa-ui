package ws.cpcs.adsiuba.jpaui.webapp.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ws.cpcs.adsiuba.jpaui.ui.UIWebConfig;

@Configuration
@EnableWebMvc
@ComponentScan("ws.cpcs.adsiuba.jpaui.webapp.web")
@Import(UIWebConfig.class)
public class WebConfig {
}
