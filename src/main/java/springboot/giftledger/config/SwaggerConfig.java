package springboot.giftledger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI openApi() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }
    
    private Info apiInfo() {
        return new Info()
                .title("GiftLedger API")
                .description("축의금 관리 시스템")
                .version("v0.1");
    }
}
