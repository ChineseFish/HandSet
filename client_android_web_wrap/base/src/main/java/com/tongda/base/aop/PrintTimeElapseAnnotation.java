package com.tongda.base.aop;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PrintTimeElapseAnnotation {
    String value() default "null";
}
