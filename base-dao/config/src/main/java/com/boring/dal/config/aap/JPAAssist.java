package com.boring.dal.config.aap;

import com.boring.dal.config.ObjectUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Component
public class JPAAssist implements AnnotationAssist {
    @Override
    public Class<? extends Annotation> getEntityAnnotation() {
        return Entity.class;
    }

    @Override
    public Class<? extends Annotation> getEntityIdAnnotation() {
        return Id.class;
    }

    @Override
    public String getDbFieldName(Field field) {
    	if(Modifier.isStatic(field.getModifiers())||Modifier.isFinal(field.getModifiers()))
    		return null;
        Column fa = field.getAnnotation(Column.class);
        if(fa==null) {
        	if(getDbTableName(field.getDeclaringClass())==null)
        		return null;
        	else
				return ObjectUtil.camelToSnake(field.getName());
		}
        if(StringUtils.isEmpty(fa.name())){
            return ObjectUtil.camelToSnake(field.getName());
        }else{
            return fa.name();
        }
    }

    @Override
    public String getDbTableName(Class cls) {
        if(cls.getAnnotation(Entity.class)==null)
            return null;
        Table t= (Table) cls.getAnnotation(Table.class);
        if(t==null)
            return null;
        if(StringUtils.isEmpty(t.name())){
            return ObjectUtil.camelToSnake(cls.getSimpleName());
        }else{
            return t.name();
        }
    }
}
