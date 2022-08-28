package com.itheima.reggie.config;

import com.itheima.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

//   在WebMvcConfig配置类中扩展Spring mvc的消息转换器，在此消息转换器中使用提供的对象转换器进行Java 对象向Json 数据的转换

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 扩展Mvc 的消息转换器
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java 对象转换成为Json 数据
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上边的对象转换器对象追加到Mvc 框架的转换器容器,index 表示调用这个转换器的顺序
        converters.add(0,messageConverter);
    }

}
