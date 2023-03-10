package com.bestkayz.kkit.plugins.core;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author: Kayz
 * @create: 2022-05-12
 **/
@Slf4j
@AllArgsConstructor
public class JarPluginFactory implements PluginFactory{

    private String path;

    @Override
    @SneakyThrows
    public List<Plugin> scanPlugins() {
        File rootDir = new File(path);
        if (!rootDir.isDirectory()){
            return new ArrayList<>();
        }else {
            String[] fileList = rootDir.list();
            if (fileList == null ||fileList.length == 0) return new ArrayList<>();
            List<Plugin> list = new ArrayList<>();
            for (String filepath : fileList) {
                if (filepath.endsWith(".jar")){
                    String jarPath = path+"\\"+filepath;
                    URL url = parseURL(jarPath);
                    PluginClassLoader pluginClassLoader = new PluginClassLoader(new URL[]{url},Thread.currentThread().getContextClassLoader());
                    list.add(new JarPlugin(pluginClassLoader,jarPath));
                }
            }

            return list;
        }
        
    }


    @SneakyThrows
    public URL parseURL(String jarPath) {
        String url;
        if (jarPath.startsWith("http")) {
            if (jarPath.endsWith("/")) {
                jarPath = jarPath.substring(0, jarPath.length() - 1);
            }
            url = "jar:" + jarPath + "!/";
        } else {
            if (jarPath.startsWith("/")) {
                jarPath = jarPath.substring(1);
            }
            url = "jar:file:/" + jarPath + "!/";
        }
        return new URL(url);
    }
}
