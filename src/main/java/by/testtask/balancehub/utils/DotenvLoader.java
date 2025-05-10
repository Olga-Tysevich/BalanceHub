package by.testtask.balancehub.utils;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class DotenvLoader {
    public static void load() {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("JWT_ACCESS_KEY_SECRET", Objects.requireNonNull(dotenv.get("JWT_ACCESS_KEY_SECRET")));
        System.setProperty("JWT_ACCESS_KEY_EXPIRATION_TIME", Objects.requireNonNull(dotenv.get("JWT_ACCESS_KEY_EXPIRATION_TIME")));
        System.setProperty("JWT_REFRESH_KEY_SECRET", Objects.requireNonNull(dotenv.get("JWT_REFRESH_KEY_SECRET")));
        System.setProperty("JWT_REFRESH_KEY_EXPIRATION_TIME", Objects.requireNonNull(dotenv.get("JWT_REFRESH_KEY_EXPIRATION_TIME")));

        System.setProperty("BALANCE_HUB_DB_EPORT", Objects.requireNonNull(dotenv.get("BALANCE_HUB_DB_EPORT")));
        System.setProperty("BALANCE_HUB_DB_NAME", Objects.requireNonNull(dotenv.get("BALANCE_HUB_DB_NAME")));
        System.setProperty("BALANCE_HUB_DB_USER", Objects.requireNonNull(dotenv.get("BALANCE_HUB_DB_USER")));
        System.setProperty("BALANCE_HUB_DB_PASSWORD", Objects.requireNonNull(dotenv.get("BALANCE_HUB_DB_PASSWORD")));

        System.setProperty("BALANCE_HUB_WEB_ALLOWED_SOURCES", Objects.requireNonNull(dotenv.get("BALANCE_HUB_WEB_ALLOWED_SOURCES")));
        System.setProperty("BALANCE_HUB_WEB_ALLOWED_METHODS", Objects.requireNonNull(dotenv.get("BALANCE_HUB_WEB_ALLOWED_METHODS")));
        System.setProperty("BALANCE_HUB_WEB_ALLOWED_HEADERS", Objects.requireNonNull(dotenv.get("BALANCE_HUB_WEB_ALLOWED_HEADERS")));
        System.setProperty("BALANCE_HUB_WEB_IGNORED_URLS", Objects.requireNonNull(dotenv.get("BALANCE_HUB_WEB_IGNORED_URLS")));
    }
}