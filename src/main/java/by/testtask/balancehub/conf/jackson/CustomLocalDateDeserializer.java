package by.testtask.balancehub.conf.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final String[] FORMATS = {
            "yyyy-MM-dd",
            "dd-MM-yyyy",
            "dd.MM.yyyy",
            "yyyy.MM.dd"
    };

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String date = p.getText();
        for (String format : FORMATS) {
            try {
                return LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IOException("Unable to parse date: " + date);
    }
}