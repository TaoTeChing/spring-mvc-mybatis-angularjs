package com.crell.core.listener;

import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;

public class ApplicationListener extends ContextLoaderListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {

        super.contextInitialized(event);
    }
}
