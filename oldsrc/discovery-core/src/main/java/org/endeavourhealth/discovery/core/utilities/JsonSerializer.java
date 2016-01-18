package org.endeavourhealth.discovery.core.utilities;

import com.google.gson.Gson;

public class JsonSerializer {
    public static String serialize(Object source) {
        Gson gson = new Gson();
        String json = gson.toJson(source);
        return json;
    }

    public static byte[] serializeAsBytes(Object source) {
        return serialize(source).getBytes();
    }

    public static <T> T deserialize(String value, Class<T> classOfT) {
        Gson gson = new Gson();
        T r = gson.fromJson(value, classOfT);
        return r;
    }

//    public static <T> T deserialize(Class<T> valueType, byte[] value) throws DeserializationException {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            return mapper.readValue(value, valueType);
//        } catch (IOException e) {
//            String message = "Could not deserialise to " + valueType.getTypeName();
//
//            throw new DeserializationException(message, e);
//        }
//    }
}