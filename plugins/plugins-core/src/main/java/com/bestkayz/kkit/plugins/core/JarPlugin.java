package com.bestkayz.kkit.plugins.core;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author: Kayz
 * @create: 2022-05-12
 **/
@Slf4j
public class JarPlugin implements Plugin{

    private final PluginClassLoader pluginClassLoader;

    private final String jarPath;

    public JarPlugin(PluginClassLoader pluginClassLoader, String jarPath) {
        this.pluginClassLoader = pluginClassLoader;
        this.jarPath = jarPath;
    }

    @Override
    public String getPluginName() {
        return jarPath;
    }

    @SneakyThrows
    public Map<String,Class<?>> resolveClass(){
        JarFile jarFile = new JarFile(jarPath);
        List<JarEntry> entries = jarFile
                .stream().collect(Collectors.toList());
        Map<String,Class<?>> map = new HashMap<>();
        for (JarEntry entry : entries) {
            log.info(entry.getName());
            if (entry.getName().endsWith(".class")){
                String className = entry.getName().replaceAll("/",".");
                className = className.substring(0,className.length()-6);
                Class<?> clazz = pluginClassLoader.findPluginClass(className);
                map.put(className,clazz);
            }
        }
        return map;
    }

    @Override
    public List<String> getAllClassName() {
        return null;
    }




}
