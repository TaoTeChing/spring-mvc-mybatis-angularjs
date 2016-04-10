package com.crell.core.listener;

import com.crell.core.constant.BaseResources;
import com.crell.core.util.PropertiesUtil;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;
import java.util.Properties;

public class ApplicationListener extends ContextLoaderListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        Properties properties = PropertiesUtil.loadProperties("domainConfig.properties");
        BaseResources.addDomain(BaseResources.DOMAIN,properties.getProperty("domain"));
        super.contextInitialized(event);
    }
}
