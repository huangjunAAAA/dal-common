package com.boring.dal.remote.autorpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RemoteAccess {
    String remote() default "";
    String targetList() default "";
    Class<?> before() default void.class;
    Class<?> after() default void.class;
}
