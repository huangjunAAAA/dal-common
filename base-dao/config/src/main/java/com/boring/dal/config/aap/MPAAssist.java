package com.boring.dal.config.aap;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.boring.dal.config.ObjectUtil;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class MPAAssist implements AnnotationAssist {
    @Override
    public Class<? extends Annotation> getEntityAnnotation() {
        return TableName.class;
    }

    @Override
    public Class<? extends Annotation> getEntityIdAnnotation() {
        return TableId.class;
    }

    @Override
    public String getDbFieldName(Field field) {
		if(Modifier.isStatic(field.getModifiers())||Modifier.isFinal(field.getModifiers()))
			return null;
        TableField fa = field.getAnnotation(TableField.class);
		if(fa==null) {
			if(getDbTableName(field.getDeclaringClass())==null)
				return null;
			else
				return ObjectUtil.camelToSnake(field.getName());
		}
        if(StringUtils.isEmpty(fa.value())){
            return ObjectUtil.camelToSnake(field.getName());
        }else{
            return fa.value();
        }
    }

    @Override
    public String getDbTableName(Class cls) {
        TableName t= (TableName) cls.getAnnotation(TableName.class);
        if(t==null)
            return null;
        if(StringUtils.isEmpty(t.value())){
            return ObjectUtil.camelToSnake(cls.getSimpleName());
        }else{
            return t.value();
        }
    }
}
