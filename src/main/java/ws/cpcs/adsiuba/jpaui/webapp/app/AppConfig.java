package ws.cpcs.adsiuba.jpaui.webapp.app;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ws.cpcs.adsiuba.jpaui.model.descr.EntityDescriptor;
import ws.cpcs.adsiuba.jpaui.model.UIConfig;
import ws.cpcs.adsiuba.jpaui.webapp.app.model.User;
import ws.cpcs.adsiuba.jpaui.webapp.app.model.UserGroup;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@PropertySource("${JPAUI_CONFIG:classpath:config.properties}")
@ComponentScan("ws.cpcs.adsiuba.jpaui.webapp.app")
@EnableJpaRepositories("ws.cpcs.adsiuba.jpaui.webapp.app")
@Import(UIConfig.class)
public class AppConfig {

    @Bean
    PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    DataSource dataSource(Environment env) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(env.getRequiredProperty("db.driver"));
        ds.setUrl(env.getRequiredProperty("db.uri"));
        ds.setUsername(env.getProperty("db.username"));
        ds.setPassword(env.getProperty("db.password"));
        return ds;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(Environment env)
            throws ReflectiveOperationException {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource(env));
        factory.setPackagesToScan(getClass().getPackage().getName());
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter() {{
            setShowSql(true);
        }});
        HibernateJpaDialect dialect = new HibernateJpaDialect();
        factory.setJpaPropertyMap(new HashMap<String,Object>() {{
                put("hibernate.hbm2ddl.auto", env.getProperty("db.auto", "update"));
                put("hibernate.dialect", env.getRequiredProperty("db.dialect"));
        }});
        return factory;
    }

    @Bean
    PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager manager = new JpaTransactionManager();
        manager.setEntityManagerFactory(emf);
        return manager;
    }

    @Bean
    EntityDescriptor userDescriptor() {
        return new EntityDescriptor.Builder(User.class)
                .withIcon("user")
                .build();
    }

    @Bean
    EntityDescriptor groupDescriptor() {
        return new EntityDescriptor.Builder(UserGroup.class)
                .withIcon("eye-open")
                .build();
    }
}
