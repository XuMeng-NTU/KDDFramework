/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annotation.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import enumeration.component.ComponentType;

/**
 *
 * @author Meng
 */
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Component {
    public ComponentType type();
}
