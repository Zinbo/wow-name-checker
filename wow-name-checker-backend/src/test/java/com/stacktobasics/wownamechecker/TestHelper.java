package com.stacktobasics.wownamechecker;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.BiPredicate;

public class TestHelper {
    public static final String CHAR_NAME = "Zinbo";
    public static final String CHAR_NAME_2 = "Zinbaan";
    public static final String CHAR_NAME_3 = "Zookie";
    public static final String REALM = "Argent-Dawn";
    public static final String REALM_2 = "Khadgar";
    public static final String REGION = "eu";
    public static final String REGION_2 = "us";

    public static final String EMAIL = "email1@hello.com";
    public static final String EMAIL_2 = "email2@hello.com";
    public static final String EMAIL_3 = "email3@hello.com";
    public static final String EMAIL_4 = "email4@hello.com";

    public static BiPredicate<LocalDateTime, LocalDateTime> similarTime() {
        return (LocalDateTime actualTime, LocalDateTime expectedTime) -> ChronoUnit.SECONDS.between(actualTime, expectedTime) < 10;
    }
}
