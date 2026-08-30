package com.example.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import com.example.validation.StateValidation;

@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {StateValidation.class })
public @interface State {
    // 提供校验失败后的提示信息
    String message() default "state参数的值只能是已发布或者草稿";
    // 指定分组
    Class<?>[] groups() default { };
    // 负载，获取到State注解的附加信息
    Class<? extends Payload>[] payload() default { };
}