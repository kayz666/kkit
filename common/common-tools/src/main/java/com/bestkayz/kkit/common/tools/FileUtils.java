package com.bestkayz.kkit.common.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileUtils {

    public static File writeBytesToFile(String path,byte[] bytes) throws Exception {
        File file = new File(path);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdir();
        }
        return writeBytesToFile(file,bytes);
    }


    public static File writeBytesToFile(File file,byte[] bytes) throws Exception {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(bytes);
        bos.flush();
        bos.close();
        return file;
    }
}
