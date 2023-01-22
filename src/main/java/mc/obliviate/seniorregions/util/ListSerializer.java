package mc.obliviate.seniorregions.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ListSerializer<T> {

    private static final String DEFAULT_DELIMITER = ",";

    public String serializeList(List<T> list) {
        return serializeList(list, String::valueOf);
    }

    public String serializeList(List<T> list, Function<T, String> serializer) {
        return serializeList(list, serializer, DEFAULT_DELIMITER);
    }

    public String serializeList(List<T> list, Function<T, String> serializer, String delimiter) {
        List<String> result = new ArrayList<>();
        list.forEach(obj -> result.add(serializer.apply(obj)));
        return String.join(delimiter, result);
    }

    public List<T> deserializeList(String serializedString, Function<String, T> deserializer) {
        if (serializedString.isBlank()) return new ArrayList<>();
        return deserializeList(serializedString, deserializer, DEFAULT_DELIMITER);
    }

    public List<T> deserializeList(String serializedString, Function<String, T> deserializer, String delimiter) {
        List<T> result = new ArrayList<>();
        String[] values = serializedString.split(delimiter);
        for (String value : values) {
            result.add(deserializer.apply(value));
        }
        return result;
    }

}
