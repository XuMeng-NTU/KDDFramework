/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annotation.component.input;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Meng
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RequiredInput {

    public String name();

    public String format();
}
