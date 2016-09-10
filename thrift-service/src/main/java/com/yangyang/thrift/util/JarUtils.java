package com.yangyang.thrift.util;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarUtils {

    /**
     * 创建空的临时目录
     *
     * @return
     */
    public static File createTempDirectory() {
        final String tmpPath = System.getProperty("java.io.tmpdir");
        long current = System.currentTimeMillis();
        File tmpDir = new File(tmpPath + File.separator + "patchDir" + current);
        if (tmpDir.exists()) {
            tmpDir.delete();
            return createTempDirectory();
        }
        tmpDir.mkdirs();
        return tmpDir;
    }

    /**
     * 解压jar文件
     *
     * @param jarFile     要解压的jar文件路径
     * @param destination 解压到哪里
     * @throws IOException
     */
    public static void unJar(String jarFile, String destination) {
        File jar = new File(jarFile);
        File dir = new File(destination);
        unJar(jar, dir);
    }

    /**
     * 解压jar文件
     *
     * @param jarFile     要解压的jar文件路径
     * @param destination 解压到哪里
     * @throws IOException
     */
    public static void unJar(File jarFile, File destination) {
        JarFile jar = null;
        try {
            if (destination.exists() == false) {
                destination.mkdirs();
            }
            jar = new JarFile(jarFile);
            Enumeration<JarEntry> en = jar.entries();
            JarEntry entry = null;
            InputStream input = null;
            BufferedOutputStream bos = null;
            File file = null;
            while (en.hasMoreElements()) {
                entry = en.nextElement();
                input = jar.getInputStream(entry);
                file = new File(destination, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                    continue;
                } else {
                    file.getParentFile().mkdirs();
                }
                bos = new BufferedOutputStream(new FileOutputStream(file));
                byte[] buffer = new byte[8192];
                int length = -1;
                while (true) {
                    length = input.read(buffer);
                    if (length == -1)
                        break;
                    bos.write(buffer, 0, length);
                }
                bos.close();
                input.close();
// IOUtils.copy(input, bos);
            }

            Manifest mf = jar.getManifest();
            if (mf != null) {
                File f = new File(destination, "META-INF/MANIFEST.MF");
                File parent = f.getParentFile();
                if (parent.exists() == false) {
                    parent.mkdirs();
                }
                OutputStream out = new FileOutputStream(f);
                mf.write(out);
                out.flush();
                out.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static String convertPackageToPath(String packageName) {
        String sep = File.separator;
        if (packageName.equals("")) {
            return sep;
        } else {
            return packageName.replace(".", sep) + sep;
        }
    }

    /**
     * @param packageName
     * @param sourceFile
     * @param targetJarFile
     */
    public static void addFileToJar(String packageName, File sourceFile,
                                    File targetJarFile) {
        try {
            File tmpDir = createTempDirectory();
            unJar(targetJarFile, tmpDir);
            String packagePath = convertPackageToPath(packageName);
            File targetFile = new File(tmpDir, packagePath);
            if (sourceFile.isDirectory()) {
                FileUtils.copyDirectoryToDirectory(sourceFile, targetFile);
            } else {
                FileUtils.copyFileToDirectory(sourceFile, targetFile);
            }
            jar(targetJarFile, tmpDir);
            FileUtils.deleteDirectory(tmpDir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param packageName
     * @param sourceFile
     * @param targetJarFile
     */
    public static void delFileFromJar(String packageName, File sourceFile,
                                      File targetJarFile) {
        try {
            File tmpDir = createTempDirectory();
            unJar(targetJarFile, tmpDir);
            String packagePath = convertPackageToPath(packageName);
            File targetFile = new File(tmpDir, packagePath);
            targetFile = new File(targetFile, sourceFile.getName());
            if (targetFile.exists()) {
                targetFile.delete();
            }
            jar(targetJarFile, tmpDir);
            FileUtils.deleteDirectory(tmpDir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 压缩临时文件目录为jar文件 替换jarFile
     *
     * @param jarFile      target
     * @param tmpDirectory
     */
    public static void jar(File jarFile, File tmpDirectory) {
        if (jarFile == null || tmpDirectory == null
                || jarFile.exists() == false || tmpDirectory.exists() == false) {
            return;
        }
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
                    jarFile));
            BufferedInputStream bis;
            List<File> fileList = getAllFiles(tmpDirectory);
            for (int i = 0; i < fileList.size(); i++) {
                File file = (File) fileList.get(i);
                zos
                        .putNextEntry(new ZipEntry(getEntryName(tmpDirectory,
                                file)));
                if (file.isDirectory()) {
                    continue;
                }
                bis = new BufferedInputStream(new FileInputStream(file));
                byte[] buffer = new byte[8192];
                int length = -1;
                while (true) {
                    length = bis.read(buffer);
                    if (length == -1)
                        break;
                    zos.write(buffer, 0, length);
                }
                bis.close();
// IOUtils.copy(bis, zos);
                zos.closeEntry();
            }
            zos.close();
        } catch (Exception ex) {
        }
    }

    public static List<File> getAllFiles(File file) {
        List<File> result = new ArrayList<File>();
        if (file != null) {
            if (file.isDirectory()) {
                File[] ls = file.listFiles();
                for (File t : ls) {
                    List<File> tLst = getAllFiles(t);
                    result.addAll(tLst);
                }
            } else {
                result.add(file);
            }
        }
        return result;
    }

    /**
     * 获得zip entry 字符串
     *
     * @param base
     * @param file
     * @return
     */
    public static String getEntryName(File baseFile, File file) {
        String fileName = file.getPath();
        String result = "";
        try {
            if (baseFile.getParentFile().getParentFile() == null) {
                result = fileName.substring(baseFile.getPath()
                        .length());
            } else {
                result = fileName.substring(baseFile.getPath()
                        .length() + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
