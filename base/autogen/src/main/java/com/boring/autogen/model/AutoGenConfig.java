package com.boring.autogen.model;

import java.util.ArrayList;
import java.util.List;

public class AutoGenConfig {

    public List<AGCfg> auto;

    public static class AGCfg{
        public List<SourceDef> source;
        public Output output;
        public List<String> skip=new ArrayList<>();
        public String template;
        public String ending;
    }

    public static class Output{
        public String pkg;
        public String module;
    }

    public static class SourceDef{
        public String dao;
        public List<String> lookup;
    }

}
