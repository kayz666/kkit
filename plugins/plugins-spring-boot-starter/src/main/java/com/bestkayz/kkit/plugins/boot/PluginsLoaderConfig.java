package com.bestkayz.kkit.plugins.boot;

import com.bestkayz.kkit.plugins.core.JarPluginFactory;
import com.bestkayz.kkit.plugins.core.PluginFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author: Kayz
 * @create: 2022-05-12
 **/
@Configuration
@ConditionalOnProperty(prefix = "plugins", name = "path",matchIfMissing = false)
public class PluginsLoaderConfig {

    @Value("${plugins.path}")
    private String path;

    @Bean
    public PluginFactory pluginFactory(){
        return new JarPluginFactory(path);
    }

    @Bean
    public PluginsStarter pluginsStarter(){
        return new PluginsStarter();
    }


}
