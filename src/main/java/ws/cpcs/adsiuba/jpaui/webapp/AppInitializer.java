package ws.cpcs.adsiuba.jpaui.webapp;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ws.cpcs.adsiuba.jpaui.webapp.app.AppConfig;
import ws.cpcs.adsiuba.jpaui.webapp.web.WebConfig;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class AppInitializer implements WebApplicationInitializer {
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(new ContextLoaderListener(getContext(AppConfig.class)));
        ServletRegistration.Dynamic dispatcher =
                servletContext.addServlet("DispatcherServlet",
                        new DispatcherServlet(getContext(WebConfig.class)));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");
    }

    private static AnnotationConfigWebApplicationContext getContext(Class<?> root) {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(root);
        return ctx;
    }
}
