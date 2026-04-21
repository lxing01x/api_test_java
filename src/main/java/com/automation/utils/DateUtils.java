package com.automation.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateUtils {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_ONLY_FORMAT = "yyyy-MM-dd";
    private static final String TIME_ONLY_FORMAT = "HH:mm:ss";

    private DateUtils() {
    }

    public static String getCurrentDateTime() {
        return getCurrentDateTime(DEFAULT_FORMAT);
    }

    public static String getCurrentDateTime(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String dateTime = LocalDateTime.now().format(formatter);
        log.debug("Current datetime: {}", dateTime);
        return dateTime;
    }

    public static String getCurrentDate() {
        return getCurrentDateTime(DATE_ONLY_FORMAT);
    }

    public static String getCurrentTime() {
        return getCurrentDateTime(TIME_ONLY_FORMAT);
    }

    public static String getTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DEFAULT_FORMAT);
    }

    public static String formatDateTime(LocalDateTime dateTime, String format) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formatted = dateTime.format(formatter);
        log.debug("Formatted datetime: {}", formatted);
        return formatted;
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr, DEFAULT_FORMAT);
    }

    public static LocalDateTime parseDateTime(String dateTimeStr, String format) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            log.error("Failed to parse datetime: {}", dateTimeStr, e);
            return null;
        }
    }

    public static String addDays(int days) {
        return addDays(days, DEFAULT_FORMAT);
    }

    public static String addDays(int days, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String result = LocalDateTime.now().plusDays(days).format(formatter);
        log.debug("DateTime after adding {} days: {}", days, result);
        return result;
    }

    public static String subtractDays(int days) {
        return subtractDays(days, DEFAULT_FORMAT);
    }

    public static String subtractDays(int days, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String result = LocalDateTime.now().minusDays(days).format(formatter);
        log.debug("DateTime after subtracting {} days: {}", days, result);
        return result;
    }

    public static String addHours(int hours) {
        return addHours(hours, DEFAULT_FORMAT);
    }

    public static String addHours(int hours, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String result = LocalDateTime.now().plusHours(hours).format(formatter);
        log.debug("DateTime after adding {} hours: {}", hours, result);
        return result;
    }

    public static String addMinutes(int minutes) {
        return addMinutes(minutes, DEFAULT_FORMAT);
    }

    public static String addMinutes(int minutes, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String result = LocalDateTime.now().plusMinutes(minutes).format(formatter);
        log.debug("DateTime after adding {} minutes: {}", minutes, result);
        return result;
    }
}
