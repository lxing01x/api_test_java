package com.automation.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Slf4j
public class DataGenerator {

    private static final Random random = new Random();

    private DataGenerator() {
    }

    public static String generateUUID() {
        String uuid = UUID.randomUUID().toString();
        log.debug("Generated UUID: {}", uuid);
        return uuid;
    }

    public static String generateRandomString(int length) {
        String randomString = RandomStringUtils.randomAlphabetic(length).toLowerCase();
        log.debug("Generated random string: {}", randomString);
        return randomString;
    }

    public static String generateRandomNumeric(int length) {
        String numeric = RandomStringUtils.randomNumeric(length);
        log.debug("Generated random numeric: {}", numeric);
        return numeric;
    }

    public static String generateRandomAlphanumeric(int length) {
        String alphanumeric = RandomStringUtils.randomAlphanumeric(length);
        log.debug("Generated random alphanumeric: {}", alphanumeric);
        return alphanumeric;
    }

    public static String generateEmail(String prefix) {
        String email = String.format("%s_%s@example.com", 
                prefix.toLowerCase(), 
                generateRandomNumeric(6));
        log.debug("Generated email: {}", email);
        return email;
    }

    public static String generatePhoneNumber() {
        String phone = String.format("1%s%s", 
                generateRandomNumeric(1),
                generateRandomNumeric(9));
        log.debug("Generated phone number: {}", phone);
        return phone;
    }

    public static int generateRandomInt(int min, int max) {
        int value = random.nextInt(max - min + 1) + min;
        log.debug("Generated random int: {} (range: {}-{})", value, min, max);
        return value;
    }

    public static long generateRandomLong(long min, long max) {
        long value = min + (long) (random.nextDouble() * (max - min));
        log.debug("Generated random long: {} (range: {}-{})", value, min, max);
        return value;
    }

    public static double generateRandomDouble(double min, double max) {
        double value = min + random.nextDouble() * (max - min);
        log.debug("Generated random double: {} (range: {}-{})", value, min, max);
        return value;
    }

    public static boolean generateRandomBoolean() {
        boolean value = random.nextBoolean();
        log.debug("Generated random boolean: {}", value);
        return value;
    }

    public static String generateCurrentDate(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String date = LocalDate.now().format(formatter);
        log.debug("Generated current date: {}", date);
        return date;
    }

    public static String generateCurrentDateTime(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String dateTime = LocalDateTime.now().format(formatter);
        log.debug("Generated current datetime: {}", dateTime);
        return dateTime;
    }

    public static String generateDate(int daysOffset, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String date = LocalDate.now().plusDays(daysOffset).format(formatter);
        log.debug("Generated date with offset {}: {}", daysOffset, date);
        return date;
    }

    @SafeVarargs
    public static <T> T randomFromList(T... items) {
        if (items == null || items.length == 0) {
            return null;
        }
        T selected = items[random.nextInt(items.length)];
        log.debug("Selected random item from list: {}", selected);
        return selected;
    }
}
