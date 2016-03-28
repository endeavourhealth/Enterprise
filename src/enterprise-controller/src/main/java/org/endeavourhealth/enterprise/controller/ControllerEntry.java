package org.endeavourhealth.enterprise.controller;

import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerEntry {

    public static void main(String[] args) throws Exception {

        try (ControllerMain main = new ControllerMain()) {

            ControllerMainSingleton.register(main);
            main.start();

            System.out.println("");
            System.out.println("Press enter to exit");
            System.in.read();
        }

        System.exit(0);
    }
}
