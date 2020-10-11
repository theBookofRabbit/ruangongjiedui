package cn.ruangong.jiedui;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Configure implements WebMvcConfigurer {
    //添加swagger支持
    @Bean
    public Docket createRestApi() {
        // 创建 Docket 对象
        return new Docket(DocumentationType.SWAGGER_2) // 文档类型，使用 Swagger2
                .apiInfo(this.apiInfo()) // 设置 API 信息
                // 扫描 Controller 包路径，获得 API 接口
                .select()
                //第一篇代码是
                .apis(Predicates.or(
                        RequestHandlerSelectors.basePackage("cn.ruangong.jiedui")
                ))//注意这里)
                .paths(PathSelectors.any())
                // 构建出 Docket 对象
                .build();
    }
    /**
     * 创建 API 信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("结对编程接口文档")
                .description("正如你所见，这是一份简陋的swagger接口文档。")
                .version("1.0.0") // 版本号
                .contact(new Contact("pkj", "https://www.cnblogs.com/dayixinsheng/", "3172864829@qq.com")) // 联系人
                .build();
    }
}
