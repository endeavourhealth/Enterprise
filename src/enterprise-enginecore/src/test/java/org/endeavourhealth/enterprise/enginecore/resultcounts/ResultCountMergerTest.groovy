package org.endeavourhealth.enterprise.enginecore.resultcounts

import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentHelper
import org.endeavourhealth.enterprise.enginecore.resultcounts.models.ResultCounts
import org.junit.Test

class ResultCountMergerTest {

    @Test
    void testMerge_simpleMerge_success() {

        Set<String> expectedOrganisations = ["A", "B"];
        UUID jobReportUuid = UUID.randomUUID();
        UUID itemAUuid = UUID.randomUUID();
        UUID itemBUuid = UUID.randomUUID();

        ResultCounts resultCounts1 = new ResultCountBuilder()
                .startNewJobReportResult(jobReportUuid)
                .addResultToJob("A", 5)
                .addResultToJob("B", 10)
                .startNewReportItemResult(itemAUuid)
                .addResultToReportItem("A", 1)
                .addResultToReportItem("B", 2)
                .startNewReportItemResult(itemBUuid)
                .addResultToReportItem("A", 4)
                .addResultToReportItem("B", 8)
                .build();

        ResultCounts resultCounts2 = new ResultCountBuilder()
                .startNewJobReportResult(jobReportUuid)
                .addResultToJob("A", 16)
                .addResultToJob("B", 32)
                .startNewReportItemResult(itemAUuid)
                .addResultToReportItem("A", 64)
                .addResultToReportItem("B", 128)
                .startNewReportItemResult(itemBUuid)
                .addResultToReportItem("A", 256)
                .addResultToReportItem("B", 512)
                .build();

        Map<UUID, Set<String>> jobReportUuidToOrganisations = createJobReportUuidToOrganisations(resultCounts1, expectedOrganisations);

        ResultCountMerger merger = new ResultCountMerger(jobReportUuidToOrganisations);
        merger.merge(resultCounts1);
        merger.merge(resultCounts2);

        ResultCounts result = merger.getResult();

        ResultCounts expected = new ResultCountBuilder()
                .startNewJobReportResult(jobReportUuid)
                .addResultToJob("A", 21)
                .addResultToJob("B", 42)
                .startNewReportItemResult(itemAUuid)
                .addResultToReportItem("A", 65)
                .addResultToReportItem("B", 130)
                .startNewReportItemResult(itemBUuid)
                .addResultToReportItem("A", 260)
                .addResultToReportItem("B", 520)
                .build();

        assertAreCountsSame(result, expected);
    }

    private static void assertAreCountsSame(ResultCounts a, ResultCounts b) {
        String aXml = ResultCountsHelper.serialise(a);
        String bXml = ResultCountsHelper.serialise(b);

        assert aXml.equals(bXml);
    }

    @Test(expected = Exception.class)
    void testMerge_jobOrgListIncorrect_exception() {

        Set<String> expected = ["A", "B"];

        ResultCounts resultCounts = new ResultCountBuilder()
                .startNewJobReportResult(UUID.randomUUID())
                .addResultToJob("A", 1)
                .addResultToJob("C", 1)
                .startNewReportItemResult(UUID.randomUUID())
                .addResultToReportItem("A", 1)
                .addResultToReportItem("B", 1)
                .build();

        Map<UUID, Set<String>> jobReportUuidToOrganisations = createJobReportUuidToOrganisations(resultCounts, expected);

        ResultCountMerger merger = new ResultCountMerger(jobReportUuidToOrganisations);
        merger.merge(resultCounts);
    }

    @Test(expected = Exception.class)
    void testMerge_jobItemOrgListIncorrect_exception() {

        Set<String> expected = ["A", "B"];

        ResultCounts resultCounts = new ResultCountBuilder()
                .startNewJobReportResult(UUID.randomUUID())
                .addResultToJob("A", 1)
                .addResultToJob("B", 1)
                .startNewReportItemResult(UUID.randomUUID())
                .addResultToReportItem("B", 1)
                .addResultToReportItem("C", 1)
                .build();

        Map<UUID, Set<String>> jobReportUuidToOrganisations = createJobReportUuidToOrganisations(resultCounts, expected);

        ResultCountMerger merger = new ResultCountMerger(jobReportUuidToOrganisations);
        merger.merge(resultCounts);
    }

    @Test(expected = Exception.class)
    void testMerge_jobReportUuidIncorrect_exception() {

        Set<String> expected = ["A", "B"];

        ResultCounts resultCounts = new ResultCountBuilder()
                .startNewJobReportResult(UUID.randomUUID())
                .addResultToJob("A", 1)
                .addResultToJob("B", 1)
                .startNewReportItemResult(UUID.randomUUID())
                .addResultToReportItem("A", 1)
                .addResultToReportItem("B", 1)
                .build();

        Map<UUID, Set<String>> jobReportUuidToOrganisations = new HashMap<>();
        jobReportUuidToOrganisations.put(UUID.randomUUID(), expected);

        ResultCountMerger merger = new ResultCountMerger(jobReportUuidToOrganisations);
        merger.merge(resultCounts);
    }

    private static Map<UUID, Set<String>> createJobReportUuidToOrganisations(ResultCounts resultCounts, Set<String> organisationIds) {
        Map<UUID, Set<String>> map = new HashMap<>();
        UUID jobReportUuid = QueryDocumentHelper.parseMandatoryUuid(resultCounts.jobReport.get(0).getJobReportUuid());

        map.put(jobReportUuid, organisationIds);

        return map;
    }
}
