package org.endeavourhealth.enterprise.core;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public final class PercentageCalculator {

    private static final NumberFormat FORMATTER = new DecimalFormat("##.##");

    public static double calculatePercentage(double denominator, double numerator) {
        return (numerator * 100d) / denominator;
    }
    public static String calculatorPercentString(double denominator, double numerator) {
        double per = calculatePercentage(denominator, numerator);
        if (per == 0d) {
            return "0%";
        } else if (per == 100d) {
            return "100%";
        } else {
            return FORMATTER.format(per) + "%";
        }


    }
}
