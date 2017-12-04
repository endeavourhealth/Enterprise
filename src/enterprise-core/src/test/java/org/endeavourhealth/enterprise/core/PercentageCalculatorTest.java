package org.endeavourhealth.enterprise.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class PercentageCalculatorTest {

    @Test(expected = IllegalArgumentException.class)
    public void calculatePercentageNumeratorGreaterThanDenominator() {
        PercentageCalculator.calculatePercentage(0, 100);
        fail();
    }

    @Test()
    public void calculatePercentageZeroDenominatorAndNumerator() {
        double actual = PercentageCalculator.calculatePercentage(0, 0);
        assertEquals(0, actual, 0);
    }

    @Test()
    public void calculatePercentage100DenominatorAndNumerator() {
        double actual = PercentageCalculator.calculatePercentage(100, 100);
        assertEquals(100, actual, 0);
    }

    @Test()
    public void calculatePercentageZeroNumerator() {
        double actual = PercentageCalculator.calculatePercentage(100, 0);
        assertEquals(0, actual, 0);
    }

    @Test()
    public void calculatePercentageZeroDenominator() {
        double actual = PercentageCalculator.calculatePercentage(0, -100);
        assertEquals(0, actual, 0);
    }

    @Test()
    public void calculatorPercentAlmost100Percent() {
        double actual = PercentageCalculator.calculatePercentage(1000000, 999999);
        assertEquals(99.9999d, actual, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculatorPercentStringNumeratorGreaterThanDenominator() {
        PercentageCalculator.calculatorPercentString(0, 100);
    }

    @Test()
    public void calculatorPercentStringZeroDenominatorAndNumerator() {
        String actual = PercentageCalculator.calculatorPercentString(0, 0);
        assertEquals("0%", actual);
    }

    @Test()
    public void calculatorPercentString100DenominatorAndNumerator() {
        String actual = PercentageCalculator.calculatorPercentString(100, 100);
        assertEquals("100%", actual);
    }

    @Test()
    public void calculatorPercentStringZeroNumerator() {
        String actual = PercentageCalculator.calculatorPercentString(100, 0);
        assertEquals("0%", actual);
    }

    @Test()
    public void calculatorPercentStringZeroDenominator() {
        String actual = PercentageCalculator.calculatorPercentString(0, -100);
        assertEquals("0%", actual);
    }

    @Test()
    public void calculatorPercentStringAlmostZeroPercent() {
        String actual = PercentageCalculator.calculatorPercentString(1000000, 1);
        assertEquals("0.01%", actual);
    }

    @Test()
    public void calculatorPercentStringAlmost100Percent() {
        String actual = PercentageCalculator.calculatorPercentString(1000000, 999999);
        assertEquals("99.99%", actual);
    }
}