package com.example.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.function.Function;

public interface JsfUtil {
    String JS_JQUERY_DEFINED = "return typeof jQuery != 'undefined';";
    String JS_PRIMEFACES_DEFINED = "return typeof PrimeFaces != 'undefined';";
    String JS_JQUERY_ACTIVE = "return jQuery.active != 0;";
    String JS_PRIMEFACES_QUEUE_NOT_EMPTY = "return !PrimeFaces.ajax.Queue.isEmpty();";

    static Function<WebDriver, Boolean> waitForJQueryAndPrimeFaces() {
        return input -> {
            boolean ajax = false;
            boolean jQueryDefined = executeBooleanJavascript(input, JS_JQUERY_DEFINED);
            boolean primeFacesDefined = executeBooleanJavascript(input, JS_PRIMEFACES_DEFINED);

            if (jQueryDefined) {
                ajax = executeBooleanJavascript(input, JS_JQUERY_ACTIVE);
            }
            if (primeFacesDefined) {
                ajax |= executeBooleanJavascript(input, JS_PRIMEFACES_QUEUE_NOT_EMPTY);
            }

            return !ajax;
        };
    }

    private static boolean executeBooleanJavascript(WebDriver input, String javascript) {
        return (Boolean) ((JavascriptExecutor) input).executeScript(javascript);
    }
}
