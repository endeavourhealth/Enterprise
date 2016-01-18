package org.endeavourhealth.discovery.core;

import org.endeavourhealth.discovery.core.entities.model.DataContainer;
import org.endeavourhealth.discovery.core.entities.retrieval.EntityRetriever;

import java.io.IOException;

public class CoreMain {
    public static void main(String[] args) throws IOException {

        System.out.println("Started");

        EntityRetriever retriever = new EntityRetriever(null);
        DataContainer container = retriever.getEntityContainer(6034);


//        Scanner scanner = new Scanner(System.in);
//        scanner.nextLine();
        System.exit(0);
    }
}
