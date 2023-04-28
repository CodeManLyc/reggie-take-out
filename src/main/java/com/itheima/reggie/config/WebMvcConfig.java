package com.itheima.reggie.config;

import com.itheima.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 配置类
 *
 * @Author Lyc
 * @Date 2023/4/21 16:39
 * @Description: 配置类
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 设置静态资源的映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射");
        //只要请求路径是以backend开头的，不管在其下面有几级路径，那么全都把其映射到（去访问classpath路径下的backend）classpath:/backend/
        //因为resources包在进行编译之后也就是进入到reggie包的路径下了 即进入到了类路径下面 所以是classpath
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展消息转换器   消息转换器可以对接收或者返回的消息进行转换，比如解决中文乱码、json中Long精度丢失等，我们可以使用系统提供的消息转换器
     * 或者我们自定义的转换器。通过converters.add()来进行注册使用。会把这里添加的converter依次放在最高优先级(List的头部)。有多个自定义的converter时，
     * 可以改变相互之间的顺序，但是都在内置的converter前面。
     * @param converters the list of configured converters to extend
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器的对象
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0,mappingJackson2HttpMessageConverter);
    }
}
