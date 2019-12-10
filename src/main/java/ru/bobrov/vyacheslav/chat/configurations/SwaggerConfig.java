package ru.bobrov.vyacheslav.chat.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static ru.bobrov.vyacheslav.chat.services.Constants.TOKEN_PREFIX;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .globalOperationParameters(
                        List.of(
                                (new ParameterBuilder())
                                        .name(AUTHORIZATION)
                                        .defaultValue(TOKEN_PREFIX + " <token>")
                                        .description("Authorization header. Need in all apis exclude /common/auth/** or /mobile/auth/**")
                                        .modelRef(new ModelRef("string"))
                                        .parameterType("header")
                                        .required(false)
                                        .build()
                        )
                )
                .select()
                .apis(RequestHandlerSelectors.basePackage("ru.bobrov.vyacheslav.chat.controllers"))
                .paths(PathSelectors.regex("/.*"))
                .build().apiInfo(apiEndPointsInfo());
    }

    private ApiInfo apiEndPointsInfo() {
        return new ApiInfoBuilder()
                .title("Chats REST API")
                .description("Chats Management REST API")
                .contact(new Contact("Vyacheslav Bobrov",
                        "https://github.com/VyacheslavBobrov/chat",
                        "bobrov.vy@gmail.com"))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version("1.0.0")
                .build();
    }
}
