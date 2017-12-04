package org.endeavourhealth.enterprise.core.terminology.termlex;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

public class TermlexWeb implements Termlex {
    private static final String TERMLEX = "http://termlex.org/";
    private static LinkedList<Client> webClients = new LinkedList<>();

    @Override
    public List<String> getDescendants(String conceptCode) {
        JsonElement json = executeTermlexGet("hierarchy/" + conceptCode + "/descendants");
        return getCodesFromJsonArray(json);
    }

    @Override
    public List<String> getChildren(String conceptCode) {
        JsonElement json = executeTermlexGet("hierarchy/" + conceptCode + "/children");
        return getCodesFromJsonArray(json);
    }

    @Override
    public String getPreferredTerm(String conceptCode) {
        JsonElement json = executeTermlexGet("concepts/" + conceptCode);
        if (json == null) {
            return null;
        }
        JsonObject obj = json.getAsJsonObject();
        JsonElement termObj = obj.get("preferredTerm");
        return termObj.getAsString();
    }

    private JsonElement executeTermlexGet(String path) {

        Client client = borrowClient();
        WebTarget target = client.target(TERMLEX + path);
        //target.path(path);

        Invocation.Builder request = target.request();
        request.accept(MediaType.APPLICATION_JSON);

        Response response = request.get();

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            String s = response.readEntity(String.class);
            returnClient(client); //only return on HTTP success

            JsonParser parser = new JsonParser();
            return parser.parse(s);
        } else {
            throw new RuntimeException("Error performing termlex query to " + path + " - status code " + response.getStatus());
        }
    }

    private List<String> getCodesFromJsonArray(JsonElement jsonElement) {
        List<String> ret = new ArrayList<>();

        JsonArray array = jsonElement.getAsJsonArray();
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
}
