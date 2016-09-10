package com.yangyang.thrift.util;


import java.io.File;


/**
 * 文件工具类
 */
public class FileUtil {

    private FileUtil() {
        //工具类无需对象实例化
    }

    public static String getJarExecPath(Class clazz) {
        String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (OSUtil.getOSname().equals(OSType.Windows)) {
            return path.substring(1);
        }
        return path;
    }


    /**
     * 当前目录路径
     */
    public static String currentWorkDir = System.getProperty("user.dir") + "/";


    /**
     * 创建(多级)目录
     *
     * @param filePath 完整的文件名(类似：/usr/a/b/c/d.xml)
     */
    public static void createDirs(String filePath) {


        File file = new File(filePath);
        if (file.exists()) {
            return;
        }
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

    }


}
