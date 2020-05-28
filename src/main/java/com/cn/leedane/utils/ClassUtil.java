package com.cn.leedane.utils;

import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author LeeDane
 * 2020年05月17日 14:56
 * Version 1.0
 */
public class ClassUtil {
    private Logger logger = Logger.getLogger(getClass());

    /**
     * 获取指定类的所有子类
     * @param clazz
     * @return
     */
    public List<Class> getAllClassByInterface(Class clazz) {
        List<Class> list = new ArrayList<>();

        //打包完后放到linux环境中的处理方法
        if(!LeedanePropertiesConfig.newInstance().isDebug()){
            ApplicationHome h = new ApplicationHome(getClass());
            File jarF = h.getSource();
            //jarF.getAbsolutePath()： 获取项目jar的路径，比如：/usr/local/leedane/leedaneSpringBoot.jar
            return getClassList(clazz, getClasssFromJarFile(jarF.getAbsolutePath(), "com/cn/leedane/task/spring/remind"));
        }

        // 判断是否是一个接口
        logger.info("clazz="+ clazz.getSimpleName());
        logger.info("clazz.isInterface()=="+ clazz.isInterface());
        if (clazz.isInterface()) {
            try {
                List<Class> allClass = getAllClass(clazz.getPackage().getName());
                /**
                 * 循环判断路径下的所有类是否实现了指定的接口 并且排除接口类自己
                 */
                for (int i = 0; i < allClass.size(); i++) {
                    /**
                     * 判断是不是同一个接口
                     */
                    // isAssignableFrom:判定此 Class 对象所表示的类或接口与指定的 Class
                    // 参数所表示的类或接口是否相同，或是否是其超类或超接口
                    if (clazz.isAssignableFrom(allClass.get(i))) {
                        if (!clazz.equals(allClass.get(i))) {
                            // 自身并不加进去
                            list.add(allClass.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("出现异常", e);
            }
        } else {
            // 如果不是接口，则获取它的所有子类
            try {
                List<Class> allClass = getAllClass(clazz.getPackage().getName());
                logger.info("获取allClass数量："+ allClass.size());
                /**
                 * 循环判断路径下的所有类是否继承了指定类 并且排除父类自己
                 */
               /* for (int i = 0; i < allClass.size(); i++) {
                    if (clazz.isAssignableFrom(allClass.get(i))) {
                        if (!clazz.equals(allClass.get(i))) {
                            // 自身并不加进去
                            list.add(allClass.get(i));
                        }
                    }
                }*/
                return getClassList(clazz, allClass);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("出现异常", e);
            }
        }
        return list;
    }

    /**
     *
     * @param clazz
     * @param classList
     * @return
     */
    private List<Class> getClassList(Class clazz, List<Class> classList) {
        List<Class> list = new ArrayList<>();
        /**
         * 循环判断路径下的所有类是否实现了指定的接口 并且排除接口类自己
         */
        for (int i = 0; i < classList.size(); i++) {
            /**
             * 判断是不是同一个接口
             */
            // isAssignableFrom:判定此 Class 对象所表示的类或接口与指定的 Class
            // 参数所表示的类或接口是否相同，或是否是其超类或超接口
            if (clazz.isAssignableFrom(classList.get(i))) {
                if (!clazz.equals(classList.get(i))) {
                    // 自身并不加进去
                    list.add(classList.get(i));
                }
            }
        }
        return list;
    }

    /**
     * 从jar文件中读取指定目录下面的所有的class文件
     *
     * @param jarPaht
     *            jar文件存放的位置
     * @param filePaht
     *            指定的文件目录
     * @return 所有的的class的对象
     */
    public List<Class> getClasssFromJarFile(String jarPaht, String filePaht) {
        List<Class> clazzs = new ArrayList<Class>();

        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPaht);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        List<JarEntry> jarEntryList = new ArrayList<JarEntry>();

        Enumeration<JarEntry> ee = jarFile.entries();
        while (ee.hasMoreElements()) {
            JarEntry entry = (JarEntry) ee.nextElement();
            String entryName = entry.getName();
            // 过滤我们出满足我们需求的东西
            if (entryName.startsWith(filePaht) && entryName.endsWith(".class")) {
                jarEntryList.add(entry);
            }
        }
        for (JarEntry entry : jarEntryList) {
            String className = entry.getName().replace('/', '.');
            className = className.substring(0, className.length() - 6);

            try {
                clazzs.add(Thread.currentThread().getContextClassLoader().loadClass(className));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return clazzs;
    }

    /**
     * 从一个指定路径下查找所有的类
     *
     * @param packagename
     */
    @SuppressWarnings("rawtypes")
    private List<Class> getAllClass(String packagename) {
        List<Class> list = new ArrayList<>();
        // 返回对当前正在执行的线程对象的引用。
        // 返回该线程的上下文 ClassLoader。
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packagename.replace('.', '/');
        logger.info("path == "+ path);
        try {
            ArrayList<File> fileList = new ArrayList<>();
            /**
             * 这里面的路径使用的是相对路径 如果大家在测试的时候获取不到，请理清目前工程所在的路径 使用相对路径更加稳定！
             * 另外，路径中切不可包含空格、特殊字符等！
             */
            // getResources:查找所有给定名称的资源
            // 获取jar包中的实现类:Enumeration<URL> enumeration =
            // classLoader.getResources(path);
            Enumeration<URL> enumeration = null;
            if(LeedanePropertiesConfig.newInstance().isDebug()){
                enumeration = classLoader.getResources(path);
            }else
                enumeration = classLoader.getResources("../bin/"+path);
            while (enumeration.hasMoreElements()) {

                URL url = enumeration.nextElement();
                logger.info("file的url："+ url);
                // 获取此 URL 的文件名
                fileList.add(new File(url.getFile()));
            }

            logger.info("fileList的数量是："+ fileList.size());
            for (int i = 0; i < fileList.size(); i++) {
                list.addAll(findClass(fileList.get(i), packagename));
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("出现异常", e);
        }
        return list;
    }

    /**
     * 如果file是文件夹，则递归调用findClass方法，或者文件夹下的类 如果file本身是类文件，则加入list中进行保存，并返回
     *
     * @param file
     * @param packagename
     * @return
     */
    @SuppressWarnings("rawtypes")
    private ArrayList<Class> findClass(File file, String packagename) {
        ArrayList<Class> list = new ArrayList<>();
        if (!file.exists()) {
            return list;
        }
        // 返回一个抽象路径名数组，这些路径名表示此抽象路径名表示的目录中的文件。
        File[] files = file.listFiles();
        for (File file2 : files) {
            if (file2.isDirectory()) {
                // assert !file2.getName().contains(".");// 添加断言用于判断
                if (!file2.getName().contains(".")) {
                    ArrayList<Class> arrayList = findClass(file2, packagename + "." + file2.getName());
                    list.addAll(arrayList);
                }
            } else if (file2.getName().endsWith(".class")) {
                try {
                    // 保存的类文件不需要后缀.class
                    list.add(Class.forName(packagename + '.' + file2.getName().substring(0, file2.getName().length() - 6)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    logger.error("出现异常", e);
                }
            }
        }
        return list;
    }

    /**
     * 如果已打成jar包，则返回jar包所在目录
     * 如果未打成jar，则返回target所在目录
     * @return
     */
    public String getRootPath() throws UnsupportedEncodingException {
        // 项目的编译文件的根目录
        String path = URLDecoder.decode(ClassUtil.class.getResource("/").getPath(), String.valueOf(StandardCharsets.UTF_8));
        if (path.startsWith("file:")) {
            int i = path.indexOf(".jar!");
            path = path.substring(0, i);
            path = path.replaceFirst("file:", "");
        }
        // 项目所在的目录
        return new File(path).getParentFile().getAbsolutePath();
    }

    /**
     * 获取项目所在根目录
     * 如果未带打包成jar包，则得到src所在目录
     * 如果打jar包，则得到jar包所在目录
     *
     * @return
     */
    public String getBasePath() {
        File file = new File("");
        return file.getAbsolutePath();
    }
}
