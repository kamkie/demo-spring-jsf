package com.example.util;

import com.google.common.base.Predicate;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class JsfUtil {
    private static final String JS_JQUERY_DEFINED = "return typeof jQuery != 'undefined';";
    private static final String JS_PRIMEFACES_DEFINED = "return typeof PrimeFaces != 'undefined';";
    private static final String JS_JQUERY_ACTIVE = "return jQuery.active != 0;";
    private static final String JS_PRIMEFACES_QUEUE_NOT_EMPTY = "return !PrimeFaces.ajax.Queue.isEmpty();";

    public static Predicate<WebDriver> waitForJQueryAndPrimeFaces() {
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
