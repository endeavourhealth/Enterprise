package org.endeavourhealth.enterprise.controller;

public class ControllerEntry {

    public static void main(String[] args) throws Exception {

        try (ControllerMain main = new ControllerMain()) {

            ControllerMainSingleton.register(main);
            main.start();

            System.out.println("");
            System.out.println("Press enter to exit");
            //noinspection ResultOfMethodCallIgnored
            System.in.read();
        }

        System.exit(0);
    }
}
