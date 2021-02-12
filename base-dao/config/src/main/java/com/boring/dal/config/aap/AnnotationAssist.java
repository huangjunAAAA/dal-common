package com.boring.dal.config.aap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface AnnotationAssist {
    Class<? extends Annotation> getEntityAnnotation();
    Class<? extends Annotation> getEntityIdAnnotation();
    String getDbFieldName(Field field);
    String getDbTableName(Class cls);
}
