package com.boring.autogen.main;

import com.boring.autogen.model.AutoGenConfig;
import com.boring.autogen.model.AutoInterfaceDef;
import com.boring.dal.YamlFileLoader;
import com.boring.dal.config.DataAccessConfigFile;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutoGen {


    public static void main(String[] args) throws Exception{
//        AutoInterfaceDef aif=new AutoInterfaceDef();
//        aif.className="com.test.At";
//        aif.fileName="At";
//        aif.pkgName="com.test";
//        AutoMethodDef amf=new AutoMethodDef();
//        amf.params.put("p1",Integer.class);
//        amf.returnClass="Long";
//        amf.methodName="testMethod";
//        amf.exceptions.add(Exception.class.getName());
//        amf.remote="getRemote";
//        amf.targetList="tLst";
//        aif.methods.add(amf);


        AutoGen ag=new AutoGen();
        Configuration tempCfg = ag.getConfiguration();
        InterfaceDefHelper helper=new InterfaceDefHelper();
        Template temp = tempCfg.getTemplate("remoteint.ftl");

        List<AutoInterfaceDef> allInts=new ArrayList<>();
        AutoGenConfig agcfg= YamlFileLoader.loadConfigFromPath("auto.yaml",AutoGenConfig.class);
        for (Iterator<AutoGenConfig.SourceDef> iterator = agcfg.auto.source.iterator(); iterator.hasNext(); ) {
            AutoGenConfig.SourceDef sd =  iterator.next();
            String dacFile = sd.dao + "/src/main/resources/dao.yml";
            DataAccessConfigFile daf=YamlFileLoader.loadConfigFromStream(new FileInputStream(dacFile), DataAccessConfigFile.class);
            JITDAConfig jitdaConfig=new JITDAConfig(daf,sd.lookup);
            List<Class> c1 = jitdaConfig.loadAllClazz();
            for (Iterator<Class> classIterator = c1.iterator(); classIterator.hasNext(); ) {
                Class c =  classIterator.next();
                AutoInterfaceDef mds = helper.buildInterfaceDefinitionfromClass(jitdaConfig, c,agcfg);
                allInts.add(mds);
            }
        }

        Map<String, String> skipList = agcfg.auto.skip.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
        for (Iterator<AutoInterfaceDef> iterator = allInts.iterator(); iterator.hasNext(); ) {
            AutoInterfaceDef intDef =  iterator.next();
            if(!skipList.containsKey(intDef.entityClass.getTypeName()))
                ag.printInterface(intDef,agcfg,temp);
        }


    }


    public void printInterface(AutoInterfaceDef aif,AutoGenConfig config, Template temp) throws Exception{
        StringBuilder targetDir=new StringBuilder();
        targetDir.append(config.auto.output.module).append("/src/main/java");
        String[] p=aif.className.split("\\.");
        for (int i = 0; i < p.length-1; i++) {
            String d = p[i];
            targetDir.append("/").append(d);
        }
        String d=targetDir.toString();

        new File(d).mkdirs();

        Map root = new HashMap();
        root.put("idf",aif);
        root.put("util",new SignatureUtil());

        String f=targetDir.append("/").append(aif.fileName).append(".java").toString();
        Writer out = new FileWriter(f);
        temp.process(root, out);
        out.flush();
        out.close();
    }

    public Configuration getConfiguration(){

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);

        ClassTemplateLoader ctl = new ClassTemplateLoader(getClass(), "/");
        cfg.setTemplateLoader(ctl);

        cfg.setDefaultEncoding("UTF-8");

        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        cfg.setLogTemplateExceptions(false);

        cfg.setWrapUncheckedExceptions(true);

        cfg.setFallbackOnNullLoopVariable(false);

        return cfg;
    }
}
