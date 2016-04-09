package org.endeavourhealth.enterprise.processornode;

import org.apache.commons.lang3.time.StopWatch;

public class Statistics {

    private final StopWatch longTermStopWatch = new StopWatch();
    private final StopWatch patientRetrievalStopWatch = new StopWatch();

    private int initialisationTimeInSeconds;
    private int patientRetrievalTimeInSeconds = 0;
    private int numberOfBatches = 0;
    private int numberOfPatientsRetrieved = 0;

    public void jobStarted() {
        longTermStopWatch.start();
    }

    public void initialisationComplete() {
        initialisationTimeInSeconds = getTimeInSeconds(longTermStopWatch);
    }

    public void processingComplete() {
        longTermStopWatch.stop();
    }

    public void batchReceived() {
        numberOfBatches++;
    }

    public void patientRetrievalStarted() {
        patientRetrievalStopWatch.reset();
        patientRetrievalStopWatch.start();
    }

    public void patientRetrievalStopped(int patients) {
        patientRetrievalStopWatch.stop();
        patientRetrievalTimeInSeconds += getTimeInSeconds(patientRetrievalStopWatch);
        numberOfPatientsRetrieved += patients;
    }

    private int getTimeInSeconds(StopWatch stopwatch) {
        return (int)(stopwatch.getTime() / 1000);
    }

    public int getPatientsRetrieved() {
        return numberOfPatientsRetrieved;
    }

    public int getTotalDurationInSeconds() {
        return getTimeInSeconds(longTermStopWatch);
    }

    public int getInitialisationTimeInSeconds() {
        return initialisationTimeInSeconds;
    }

    public int getPatientRetrievalTimeInSeconds() {
        return patientRetrievalTimeInSeconds;
    }

    public int getNumberOfBatches() {
        return numberOfBatches;
    }
}
