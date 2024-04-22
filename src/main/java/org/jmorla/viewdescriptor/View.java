package org.jmorla.viewdescriptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface View {
    String title() default "";
    String[] stylesheets() default {};
    String[] scripts() default {};
    String entryPoint();
}
