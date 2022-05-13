package com.bestkayz.kkit.plugins.core;

import com.bestkayz.kkit.common.core.base.Result;

/**
 * @author: Kayz
 * @create: 2022-05-10
 **/
public interface JobHandler {
    Result<String> execute(String var1);
}
