package com.boring.dal;


import org.apache.commons.text.CaseUtils;
import org.reflections8.util.ClasspathHelper;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.InputStream;
import java.util.Optional;

public class YamlFileLoader {

    public static <T> T loadConfigFromPath(String configFile, Class<T> tClass) {
        Optional<ClassLoader[]> ld = ClasspathHelper.classLoaders();
        if (ld.isPresent()) {
            ClassLoader[] clds = ld.get();
            for (int i = 0; i < clds.length; i++) {
                ClassLoader clsld = clds[i];
                InputStream in = clsld.getResourceAsStream(configFile);
                Constructor c = new Constructor(tClass);
                c.setPropertyUtils(new PropertyUtils() {
                    @Override
                    public Property getProperty(Class<? extends Object> type, String name) {
                        if (name.indexOf('-') > -1) {
                            name = CaseUtils.toCamelCase(name, false, '-');
                        }
                        return super.getProperty(type, name);
                    }
                });
                Yaml yaml = new Yaml(c);
                return (T) yaml.load(in);
            }
        }
        return null;
    }
}
