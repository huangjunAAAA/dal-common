package com.boring.autogen.main;

import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.config.DataAccessConfigFile;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JITDAConfig extends DataAccessConfig {

    private static final Logger logger = LogManager.getLogger(JITDAConfig.class);

    protected List<String> modules;

    public JITDAConfig(){}

    public JITDAConfig(DataAccessConfigFile raw,List<String> modules) {
        this.raw=raw;
        this.modules=modules;
        init();
    }

    @Override
    protected List<Class> loadConfiguredClass() {

        List<Class> ret=new ArrayList<>();
        ArrayList<String> pkgs = raw.getObjects().getScanPkg();
        for (Iterator<String> iterator = pkgs.iterator(); iterator.hasNext(); ) {
            String pkg =  iterator.next();
            for (Iterator<String> stringIterator = modules.iterator(); stringIterator.hasNext(); ) {
                String module =  stringIterator.next();
                String dir=module+"/src/main/java/"+pkg.replaceAll("\\.","/");
                List<File> r1 = fetchAllEntityFile(new File(dir));
                if(r1.size()>0) {
                    List<Class> c1 = compile(r1);
                    ret.addAll(c1);
                }
            }
        }
        Map<String, Class> tmpmap = ret.stream().collect(Collectors.toMap(s->s.getTypeName(), Function.identity()));
        return new ArrayList<>(tmpmap.values());
    }

    protected List<File> fetchAllEntityFile(File p){
        ArrayList<File> ret=new ArrayList<>();
        if(p.isDirectory()){
            File[] flist = p.listFiles();
            for (int i = 0; i < flist.length; i++) {
                File f = flist[i];
                List<File> r1 = fetchAllEntityFile(f);
                ret.addAll(r1);
            }
        }else if(isEntityFile(p)){
            ret.add(p);
        }
        return ret;
    }

    protected List<Class> compile(List<File> files){
        // 使用JavaCompiler 编译java文件
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = jc.getStandardFileManager(null, null, Charset.forName("UTF-8"));
        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(files.toArray(new File[0]));
        File outputDir=new File("autogen/tmp/classes");
        try {
            FileUtils.deleteDirectory(outputDir);
        } catch (IOException e) {
            logger.error(e,e);
        }
        outputDir.mkdirs();
        List<String> optionList = new ArrayList<>(Arrays.asList("-d",outputDir.getAbsolutePath()));
        JavaCompiler.CompilationTask cTask = jc.getTask(null, fileManager, null, optionList, null, fileObjects);
        Boolean succ = cTask.call();
        try {
            fileManager.close();
        } catch (IOException e) {
            logger.error(e,e);
        }
        if(!succ)
            throw new RuntimeException("some errors exist in entity model files.");

        List<Class> ret=new ArrayList<>();
        URLClassLoader cLoader = null;
        try {
            cLoader = new URLClassLoader(new URL[]{new URL("file://"+outputDir.getAbsolutePath()+"/")});
        } catch (MalformedURLException e) {
            logger.error(e,e);
            return ret;
        }
        for (Iterator<File> iterator = files.iterator(); iterator.hasNext(); ) {
            File f =  iterator.next();
            String cname = getClassNameFromFilePath(f);
            Class<?> c = null;
            try {
                c = cLoader.loadClass(cname);
                ret.add(c);
            } catch (ClassNotFoundException e) {
                logger.error(e,e);
            }
        }
        return ret;
    }

    protected String getClassNameFromFilePath(File f){
        String abf = f.getAbsolutePath();
        abf=abf.replace(".java","");
        String[] parts = abf.split("\\\\|/");
        StringBuilder fname=new StringBuilder();
        boolean main=false,src=false,java=false;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if(fname.length()>0)
                fname.append(".").append(part);
            if(main&&src&&java&&fname.length()==0){
                fname.append(part);
            }
            if(part.equals("main"))
                main=true;
            if(part.equals("src"))
                src=true;
            if(part.equals("java"))
                java=true;
        }
        return fname.toString();
    }

    private boolean isEntityFile(File f){
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            boolean entitytag = false;
            boolean tabletag = false;
            String line = null;
            while (null != (line = br.readLine())) {
                if (line.contains("@Table"))
                    tabletag = true;
                if (line.contains("@Entity"))
                    entitytag = true;
                if (entitytag && tabletag)
                    return true;
            }
        }catch (Exception e){

        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        JITDAConfig d = new JITDAConfig();
        List<File> fs = d.fetchAllEntityFile(new File("testsvr/src/main/java/com/boring"));
        d.compile(fs);
    }
}
