/*
 * author : duckbill413
 * created date : 2024-01-07
 * updated date : 2024-01-07
 * description :
 */

package com.example.runner.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Learn Spring")
                .description("""
                        <h2>Spring runner Swagger doc API</h1>
                        <p style="text-align: right">by duckbill413</p>
                        <h3 style="text-align: center">Spring</h2>
                        <ul>
                          <li>Spring package structure</li>
                          <li>JDK 17</li>
                          <li>ResponseEntity</li>
                          <li>ControllerAdvice</li>
                          <li>SwaggerDoc</li>
                        </ul>
                        """)
                .contact(new Contact().name("duckbill413").url("https://github.com/duckbill413").email("uhyeon7399@daum.net"))
                .license(new License().name("Apache License Version 2.0").url("http://www.apache.org/licenses/LICENSE-2.0"))
                .version("v0.0.1");

        var localServer = new Server().description("local server").url("http://localhost:8080");
        var webServer = new Server().description("web server").url("https://i10a702.p.ssafy.io");

//        return new OpenAPI()
//                .info(info);

        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("JWT")
        );

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .addServersItem(localServer)
                .addServersItem(webServer)
                .components(components);
    }
}
