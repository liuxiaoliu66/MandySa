package mandysax.core.annotation;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindLayoutId {
    int value()
}
