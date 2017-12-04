package org.endeavourhealth.enterprise.core.terminology;

import org.endeavourhealth.enterprise.core.mocks.MockTermlex;
import org.endeavourhealth.enterprise.core.querydocument.models.CodeSet;
import org.endeavourhealth.enterprise.core.querydocument.models.CodeSetValue;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

public class SnomedTest {
    private MockTermlex mockTermlex;

    @Before
    public void setup() {
        mockTermlex = new MockTermlex();
        Snomed.termlex = mockTermlex;
    }

    @Test
    public void enumerateConceptsEmptySet() {
        CodeSet codeSet = new CodeSet();
        HashSet<String> actual = Snomed.enumerateConcepts(codeSet);
        assertArrayEquals(new String[] {}, actual.toArray());
    }

    @Test
    public void enumerateConceptsEmptyCodeSetValue() {
        CodeSet codeSet = new CodeSet();

        CodeSetValue codeSetValue = new CodeSetValue();
        codeSet.getCodeSetValue().add(codeSetValue);

        HashSet<String> actual = Snomed.enumerateConcepts(codeSet);
        assertArrayEquals(new String[] {null}, actual.toArray());
    }

    @Test
    public void enumerateConceptsSingle() {
        CodeSet codeSet = new CodeSet();

        CodeSetValue codeSetValue = new CodeSetValue();
        codeSetValue.setCode("1");
        codeSet.getCodeSetValue().add(codeSetValue);

        HashSet<String> actual = Snomed.enumerateConcepts(codeSet);
        assertArrayEquals(new String[] {"1"}, actual.toArray());
    }

    @Test
    public void enumerateConceptsMulti() {
        CodeSet codeSet = new CodeSet();

        CodeSetValue codeSetValue = new CodeSetValue();
        codeSetValue.setCode("1");
        codeSet.getCodeSetValue().add(codeSetValue);

        codeSetValue = new CodeSetValue();
        codeSetValue.setCode("2");
        codeSet.getCodeSetValue().add(codeSetValue);

        codeSetValue = new CodeSetValue();
        codeSetValue.setCode("3");
        codeSet.getCodeSetValue().add(codeSetValue);

        HashSet<String> actual = Snomed.enumerateConcepts(codeSet);
        assertArrayEquals(new String[] {"1", "2", "3"}, actual.toArray());
    }

    @Test
    public void enumerateConceptsSingleInclusiveNoChildren() {
        CodeSet codeSet = new CodeSet();

        CodeSetValue codeSetValue = new CodeSetValue();
        codeSetValue.setCode("1");
        codeSetValue.setIncludeChildren(true);
        codeSet.getCodeSetValue().add(codeSetValue);

        List<String> actual = new ArrayList(Snomed.enumerateConcepts(codeSet));
        List<String> expected = Arrays.asList("1");

        assertEquals(expected.size(), actual.size());
        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void enumerateConceptsSingleInclusive1Level() {
        CodeSet codeSet = new CodeSet();

        CodeSetValue codeSetValue = new CodeSetValue();
        codeSetValue.setCode("2");
        codeSetValue.setIncludeChildren(true);
        codeSet.getCodeSetValue().add(codeSetValue);

        List<String> actual = new ArrayList(Snomed.enumerateConcepts(codeSet));
        List<String> expected = Arrays.asList("2", "21", "22");

        assertEquals(expected.size(), actual.size());
        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void enumerateConceptsSingleInclusive3Levels() {
        CodeSet codeSet = new CodeSet();

        CodeSetValue codeSetValue = new CodeSetValue();
        codeSetValue.setCode("3");
        codeSetValue.setIncludeChildren(true);
        codeSet.getCodeSetValue().add(codeSetValue);

        List<String> actual = new ArrayList(Snomed.enumerateConcepts(codeSet));
        List<String> expected = Arrays.asList("3", "31", "311", "312", "32", "321", "322", "3221");

        assertEquals(expected.size(), actual.size());
        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void enumerateConceptsMultiWithChildren() {
        CodeSet codeSet = new CodeSet();

        CodeSetValue codeSetValue = new CodeSetValue();
        codeSetValue.setCode("1");
        codeSetValue.setIncludeChildren(true);
        codeSet.getCodeSetValue().add(codeSetValue);

        codeSetValue = new CodeSetValue();
        codeSetValue.setCode("2");
        codeSetValue.setIncludeChildren(true);
        codeSet.getCodeSetValue().add(codeSetValue);

        codeSetValue = new CodeSetValue();
        codeSetValue.setCode("3");
        codeSetValue.setIncludeChildren(true);
        codeSet.getCodeSetValue().add(codeSetValue);

        List<String> actual = new ArrayList(Snomed.enumerateConcepts(codeSet));
        List<String> expected = Arrays.asList("1", "2", "21", "22", "3", "31", "311", "312", "32", "321", "322", "3221");

        assertEquals(expected.size(), actual.size());
        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void enumerateConceptsSingleInclusive1LevelExclusiveSingle() {
        CodeSet codeSet = new CodeSet();

        CodeSetValue codeSetValue = new CodeSetValue();
        codeSetValue.setCode("2");
        codeSetValue.setIncludeChildren(true);
        codeSet.getCodeSetValue().add(codeSetValue);

        CodeSetValue exclusion = new CodeSetValue();
        exclusion.setCode("21");
        codeSetValue.getExclusion().add(exclusion);

        List<String> actual = new ArrayList(Snomed.enumerateConcepts(codeSet));
        List<String> expected = Arrays.asList("2", "22");

        assertEquals(expected.size(), actual.size());
        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void enumerateConceptsSingleInclusive3LevelsExclusiveSingleLevel2() {
        CodeSet codeSet = new CodeSet();

        CodeSetValue codeSetValue = new CodeSetValue();
        codeSetValue.setCode("3");
        codeSetValue.setIncludeChildren(true);
        codeSet.getCodeSetValue().add(codeSetValue);

        CodeSetValue exclusion = new CodeSetValue();
        exclusion.setCode("31");
        codeSetValue.getExclusion().add(exclusion);

        List<String> actual = new ArrayList(Snomed.enumerateConcepts(codeSet));
        List<String> expected = Arrays.asList("3", "311", "312", "32", "321", "322", "3221");

        assertEquals(expected.size(), actual.size());
        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void enumerateConceptsSingleInclusive3LevelsExclusiveSingleLevel2Inclusive() {
        CodeSet codeSet = new CodeSet();

        CodeSetValue codeSetValue = new CodeSetValue();
        codeSetValue.setCode("3");
        codeSetValue.setIncludeChildren(true);
        codeSet.getCodeSetValue().add(codeSetValue);

        CodeSetValue exclusion = new CodeSetValue();
        exclusion.setCode("31");
        exclusion.setIncludeChildren(true);
        codeSetValue.getExclusion().add(exclusion);

        List<String> actual = new ArrayList(Snomed.enumerateConcepts(codeSet));
        List<String> expected = Arrays.asList("3", "32", "321", "322", "3221");

        assertEquals(expected.size(), actual.size());
        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }

    @Test(expected = RuntimeException.class)
    public void enumerateConceptsSingleExclusionOfExclusion() {
        CodeSet codeSet = new CodeSet();

        CodeSetValue codeSetValue = new CodeSetValue();
        codeSetValue.setCode("3");
        codeSetValue.setIncludeChildren(true);
        codeSet.getCodeSetValue().add(codeSetValue);

        CodeSetValue exclusion = new CodeSetValue();
        exclusion.setCode("31");
        codeSetValue.getExclusion().add(exclusion);

        CodeSetValue exclusionExclusion = new CodeSetValue();
        exclusionExclusion.setCode("311");
        exclusion.getExclusion().add(exclusionExclusion);

        Snomed.enumerateConcepts(codeSet);
        fail();
    }

    @Test
    public void getDescendantsUsingCache() {
    }

    @Test
    public void getDescendants() {
    }

    @Test
    public void getChildren() {
    }

    @Test
    public void getPreferredTerm() {
    }
}