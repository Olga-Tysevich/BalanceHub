package by.testtask.balancehub.conf.jackson;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static by.testtask.balancehub.utils.Constants.DATE_OF_BIRTHDAY_PATTERN;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customDateFormats() {
        return builder -> builder.deserializerByType(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_OF_BIRTHDAY_PATTERN)));
    }
}
