package com.bestkayz.kkit.plugins.boot;

import com.bestkayz.kkit.plugins.core.Plugin;
import com.bestkayz.kkit.plugins.core.PluginFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author: Kayz
 * @create: 2022-05-13
 **/
@Slf4j
@Component
@ConditionalOnBean(PluginsLoaderConfig.class)
public class PluginsStarter implements ApplicationRunner {

    @Autowired
    private PluginFactory pluginFactory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始扫描插件 ...");
        List<Plugin> pluginList =  pluginFactory.scanPlugins();
        for (Plugin plugin : pluginList) {
            try {
                Map<String,Class<?>> map = plugin.resolveClass();
                for (Map.Entry<String, Class<?>> stringClassEntry : map.entrySet()) {
                    log.info(stringClassEntry.getKey());
                }
            }catch (Throwable throwable){
                log.error("插件加载异常 插件:{}  错误:{}",plugin.getPluginName(), throwable.getMessage());
            }

        }
        log.info("插件加载完毕 ...");
    }
}
