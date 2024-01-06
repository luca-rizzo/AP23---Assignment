package it.unipi.m598992.xmlSerializer.annotations;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface XMLfield {
    String name() default "";
    String type();
}
