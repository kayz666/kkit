package com.bestkayz.kkit.plugins.core;

import java.util.List;
import java.util.Map;

/**
 * @author: Kayz
 * @create: 2022-05-13
 **/
public interface Plugin {

    String getPluginName();

    Map<String,Class<?>> resolveClass();

    List<String> getAllClassName();

}
