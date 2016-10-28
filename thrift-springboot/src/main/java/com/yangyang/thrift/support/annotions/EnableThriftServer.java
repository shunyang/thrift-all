package com.yangyang.thrift.support.annotions;

import com.yangyang.thrift.support.service.ThriftConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by chenshunyang on 2016/9/23.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ThriftConfiguration.class)
@AutoConfigureAfter
public @interface EnableThriftServer {
    Class<?> genClass() ;
}
