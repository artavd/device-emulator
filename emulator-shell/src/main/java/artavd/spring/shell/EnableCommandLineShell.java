package artavd.spring.shell;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(value = { ShellConfiguration.class })
public @interface EnableCommandLineShell {
}
