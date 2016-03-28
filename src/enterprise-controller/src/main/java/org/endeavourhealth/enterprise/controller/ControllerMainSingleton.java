package org.endeavourhealth.enterprise.controller;

class ControllerMainSingleton {

    private static ControllerMain controllerMainSingleton;

    public synchronized static void register(ControllerMain controllerMain) throws Exception {
        if (controllerMainSingleton != null)
            throw new IllegalStateException("Register has already been called");

        controllerMainSingleton = controllerMain;
    }

    public synchronized static void requestStartNewJob() throws Exception {
        if (controllerMainSingleton == null)
            throw new IllegalStateException("Register has not yet been called");

        controllerMainSingleton.requestStartNewJob();
    }
}
