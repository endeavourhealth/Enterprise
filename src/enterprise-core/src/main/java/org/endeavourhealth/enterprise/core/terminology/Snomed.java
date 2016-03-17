package org.endeavourhealth.enterprise.core.terminology;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Drew on 17/03/2016.
 */
public abstract class Snomed {

    private static final Logger LOG = LoggerFactory.getLogger(Snomed.class);
    private static final String TERMLEX = "http://termlex.org/";

    private static LinkedList<Client> webClients = new LinkedList<>();

    public static List<String> getChildren(String conceptCode) {
        JsonElement json = executeTermlexGet("hierarchy/" + conceptCode + "/children");

        List<String> ret = new ArrayList<>();

        JsonArray array = json.getAsJsonArray();
        for (int i=0; i<array.size(); i++) {
            JsonElement child = array.get(i);
            String childCode = child.getAsString();
            ret.add(childCode);
        }

        return ret;
    }

    private synchronized static Client borrowClient() {
        try {
            return webClients.pop();
        } catch (NoSuchElementException nsee) {
        }

        return ClientBuilder.newClient();
    }
    private synchronized static void returnClient(Client client) {
        webClients.push(client);
    }

    private static JsonElement executeTermlexGet(String path) {

        Client client = borrowClient();
        WebTarget target = client.target(TERMLEX + path);
        //target.path(path);

        Invocation.Builder request = target.request();
        request.accept(MediaType.APPLICATION_JSON);

        Response response = request.get();

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            String s = response.readEntity(String.class);

            JsonParser parser = new JsonParser();
            return parser.parse(s);
        } else {
            LOG.error("Error performing termlex query to {} - status code {}", path, response.getStatus());
            return null;
        }
    }


    public static void main(String[] args) {

        List<String> v = getChildren("195967001");
        for (String s: v) {
            System.out.println("Child " + s);
        }

    }
}
