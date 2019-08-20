package org.endeavour.enterprise.fhir;

import org.endeavour.enterprise.dal.GetRecord;
import org.hl7.fhir.dstu3.model.*;
import org.keycloak.KeycloakPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FhirStu3 {

    private static final Logger LOG = LoggerFactory.getLogger(FhirStu3.class);

    public static Patient getPatient(String itemUuid) throws Exception {
        org.hl7.fhir.dstu3.model.Patient patient = new org.hl7.fhir.dstu3.model.Patient();

        try {
            List<Object[]> patients = GetRecord.getPatient("26");

            String givenName = patients.get(0)[0].toString();
            String familyName = patients.get(0)[1].toString();
            String dob = patients.get(0)[2].toString();
            String address_line_1 = patients.get(0)[3].toString();
            String city = patients.get(0)[4].toString();
            String postcode = patients.get(0)[5].toString();
            String title = patients.get(0)[6].toString();

            patient.addName().addGiven(givenName).setFamily(familyName).addPrefix(title);

            patient.getBirthDateElement().setValueAsString(dob);
            Address address = new Address();

            address.setUse(Address.AddressUse.HOME);
            address.setType(Address.AddressType.PHYSICAL);
            address.addLine(address_line_1);
            address.setCity(city);
            address.setPostalCode(postcode);


            patient.addAddress(address);
            patient.setId("26");


        } catch (Exception ex) {
            System.out.println(ex);
            LOG.error("getPatient transform error: ", ex);
        }

        return patient;
    }


}
