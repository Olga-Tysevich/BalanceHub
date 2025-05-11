package by.testtask.balancehub.conf.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = p.currentToken();

        if (currentToken == JsonToken.VALUE_STRING) {
            String date = p.getText();
            return parse(date);
        } else if (currentToken == JsonToken.START_ARRAY) {
            ArrayNode node = p.getCodec().readTree(p);
            return parse(node);
        }

        throw new IOException("Unable to parse date. Unexpected token: " + currentToken);

    }

    // Обработка только формата [1982, 12, 3]
    private LocalDate parse(ArrayNode node) throws IOException {
        if (node.size() == 3) {
            int year = node.get(0).asInt();
            int month = node.get(1).asInt();
            int day = node.get(2).asInt();
            return LocalDate.of(year, month, day);
        }

        throw new IOException("Unable to parse date: " + node.asText());
    }

    private LocalDate parse(String date) throws IOException {
        for (String format : FORMATS) {
            try {
                return LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IOException("Unable to parse date: " + date);
    }
}