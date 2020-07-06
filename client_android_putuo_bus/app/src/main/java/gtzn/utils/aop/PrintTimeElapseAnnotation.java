package gtzn.utils.aop;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PrintTimeElapseAnnotation {
    String value() default "null";
}
