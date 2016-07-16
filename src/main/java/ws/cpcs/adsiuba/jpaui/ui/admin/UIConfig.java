package ws.cpcs.adsiuba.jpaui.ui.admin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;

@Configuration
public class UIConfig {

    @Bean
    ConversionServiceFactoryBean conversionService() {
        return new ConversionServiceFactoryBean();
    }
}
