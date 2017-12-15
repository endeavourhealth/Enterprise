package org.endeavourhealth.enterprise.core.mocks;
import org.endeavourhealth.enterprise.core.terminology.termlex.Termlex;

import java.util.Arrays;
import java.util.List;

public class MockTermlex implements Termlex {
    /* MOCK HIERARCHY

        1 __            No Children

        2 __            1 Level
            |__ 21
            |__ 22

        3 __            3 Levels
            |__ 31
            |  |__ 311
            |  |__ 312
            |
            |__ 32
               |__ 321
               |__ 322
                  |__ 3221

     */
    @Override
    public List<String> getDescendants(String conceptCode) {
        if ("2".equals(conceptCode)) return Arrays.asList("21", "22");
        if ("3".equals(conceptCode)) return Arrays.asList("31", "311", "312", "32", "321", "322", "3221");
        if ("31".equals(conceptCode)) return Arrays.asList("311", "312");
        if ("32".equals(conceptCode)) return Arrays.asList("321", "322", "3221");
        if ("322".equals(conceptCode)) return Arrays.asList("3221");

        return Arrays.asList(new String[] {});
    }

    @Override
    public List<String> getChildren(String conceptCode) {
        if ("2".equals(conceptCode)) return Arrays.asList("21", "22");
        if ("3".equals(conceptCode)) return Arrays.asList("31", "32");
        if ("31".equals(conceptCode)) return Arrays.asList("311", "312");
        if ("32".equals(conceptCode)) return Arrays.asList("321", "322");
        if ("322".equals(conceptCode)) return Arrays.asList("3221");

        return Arrays.asList(new String[] {});
    }

    @Override
    public String getPreferredTerm(String conceptCode) {
        if ("1".equals(conceptCode)) return "One";
        if ("2".equals(conceptCode)) return "Two";
        if ("21".equals(conceptCode)) return "TwentyOne";
        if ("22".equals(conceptCode)) return "TwentyTwo";
        if ("3".equals(conceptCode)) return "Three";
        if ("31".equals(conceptCode)) return "ThirtyOne";
        if ("311".equals(conceptCode)) return "ThreeEleven";
        if ("312".equals(conceptCode)) return "ThreeTwelve";
        if ("32".equals(conceptCode)) return "ThirtyTwo";
        if ("321".equals(conceptCode)) return "ThreeTwentyOne";
        if ("322".equals(conceptCode)) return "ThreeTwentyTwo";
        if ("3221".equals(conceptCode)) return "ThirtyTwoTwentyOne";
        return null;
    }
}
