package com.boring.autogen.main;

import com.boring.autogen.model.AutoGenConfig;
import com.boring.autogen.model.AutoInterfaceDef;
import com.boring.dal.YamlFileLoader;
import com.boring.dal.config.DataAccessConfigFile;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutoGen {


    public static void main(String[] args) throws Exception{

        AutoGen ag=new AutoGen();

        AutoGenConfig globalAgList= YamlFileLoader.loadConfigFromPath("auto.yaml",AutoGenConfig.class);
        for (Iterator<AutoGenConfig.AGCfg> iterator = globalAgList.auto.iterator(); iterator.hasNext(); ) {
            AutoGenConfig.AGCfg agCfg = iterator.next();
            ag.execAgCfg(agCfg);
        }

    }

    public void execAgCfg(AutoGenConfig.AGCfg config) throws Exception{
        InterfaceDefHelper helper=new InterfaceDefHelper();
        List<AutoInterfaceDef> allInts=new ArrayList<>();
        for (Iterator<AutoGenConfig.SourceDef> iterator = config.source.iterator(); iterator.hasNext(); ) {
            AutoGenConfig.SourceDef sd =  iterator.next();
            String dacFile = sd.dao + "/src/main/resources/dao.yml";
            DataAccessConfigFile daf=YamlFileLoader.loadConfigFromStream(new FileInputStream(dacFile), DataAccessConfigFile.class);
            JITDAConfig jitdaConfig=new JITDAConfig(daf,sd.lookup);
            List<Class> c1 = jitdaConfig.loadAllClazz();
            for (Iterator<Class> classIterator = c1.iterator(); classIterator.hasNext(); ) {
                Class c =  classIterator.next();
                AutoInterfaceDef mds = helper.buildInterfaceDefinitionfromClass(jitdaConfig, c,config);
                allInts.add(mds);
            }
        }

        Map<String, String> skipList = config.skip.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
        for (Iterator<AutoInterfaceDef> iterator = allInts.iterator(); iterator.hasNext(); ) {
            AutoInterfaceDef intDef =  iterator.next();
            if(!skipList.containsKey(intDef.entityClass.getTypeName()))
                printInterface(intDef,config);
        }
    }


    public void printInterface(AutoInterfaceDef aif,AutoGenConfig.AGCfg config) throws Exception{
        StringBuilder targetDir=new StringBuilder();
        targetDir.append(config.output.module).append("/src/main/java");
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
        Template temp = getConfiguration().getTemplate(config.template);
        ByteArrayOutputStream bao=new ByteArrayOutputStream();
        Writer out = new PrintWriter(bao);
        temp.process(root, out);
        out.flush();
        out.close();

        String fixed = bao.toString();
//        fixed=new Formatter().formatSource(fixed);
        FileWriter fw=new FileWriter(f);
        fw.write(fixed);
        fw.flush();
        fw.close();
    }


    private Configuration cfg;

    public Configuration getConfiguration(){
        if(cfg!=null)
            return cfg;

        cfg = new Configuration(Configuration.VERSION_2_3_30);

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
