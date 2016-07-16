package ws.cpcs.adsiuba.jpaui.webapp.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ws.cpcs.adsiuba.jpaui.model.EntityDescriptor;
import ws.cpcs.adsiuba.jpaui.ui.admin.UIConfig;
import ws.cpcs.adsiuba.jpaui.webapp.app.model.groups.UserGroup;
import ws.cpcs.adsiuba.jpaui.webapp.app.model.users.ListedUser;
import ws.cpcs.adsiuba.jpaui.webapp.app.model.users.User;
import ws.cpcs.adsiuba.jpaui.webapp.app.model.users.UserRepo;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Collections;
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
    EntityDescriptor<User> userDescriptor() {
        return new EntityDescriptor<>(User.class)
                .withIcon("user")
                .withView("list", ListedUser.class);
    }

    @Bean
    EntityDescriptor<UserGroup> groupDescriptor() {
        return new EntityDescriptor<>(UserGroup.class)
                .withIcon("eye-open");
    }
}
