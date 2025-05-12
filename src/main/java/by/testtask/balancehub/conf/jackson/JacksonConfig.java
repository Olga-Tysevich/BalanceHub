package by.testtask.balancehub.conf.jackson;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customDateFormats() {
        return builder -> builder.deserializerByType(LocalDate.class,
                new CustomLocalDateDeserializer());
    }

}
