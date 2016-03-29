package org.endeavourhealth.enterprise.processornode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessorNodeEntry {

    private final static Logger logger = LoggerFactory.getLogger(ProcessorNodeEntry.class);

    public static void main(String[] args) throws Exception {

        logger.info("Application starting");

        ProcessorNodeMain main = new ProcessorNodeMain();
        main.start();

        System.out.println("");
        System.out.println("Press enter to exit");
        System.in.read();

        System.exit(0);
    }
}
