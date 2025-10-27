package rs.uns.ftn.clouddbadapter.store.dynamo;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal Map <-> AttributeValue mapper supporting basic JSON-like types:
 * String, Number (Integer/Long/Double), Boolean, List, Map.
 * For simplicity, everything else is not supported.
 */
public final class DynamoMapper {

    private DynamoMapper() {}

    public static Map<String, AttributeValue> toAttributes(String id, Map<String, Object> data) {
        Map<String, AttributeValue> out = new HashMap<>();
        out.put("id", AttributeValue.builder().s(id).build());
        for (Map.Entry<String, Object> e : data.entrySet()) {
            if ("id".equals(e.getKey())) continue; // ensure our id is authoritative
            out.put(e.getKey(), toAttr(e.getValue()));
        }
        return out;
    }

    public static Map<String, Object> fromAttributes(Map<String, AttributeValue> item) {
        Map<String, Object> out = new HashMap<>();
        for (Map.Entry<String, AttributeValue> e : item.entrySet()) {
            out.put(e.getKey(), fromAttr(e.getValue()));
        }
        return out;
    }

    private static AttributeValue toAttr(Object v) {
        if (v == null) return AttributeValue.builder().nul(true).build();
        if (v instanceof String s) return AttributeValue.builder().s(s).build();
        if (v instanceof Integer i) return AttributeValue.builder().n(Integer.toString(i)).build();
        if (v instanceof Long l) return AttributeValue.builder().n(Long.toString(l)).build();
        if (v instanceof Double d) return AttributeValue.builder().n(Double.toString(d)).build();
        if (v instanceof Boolean b) return AttributeValue.builder().bool(b).build();
        if (v instanceof List<?> list) {
            return AttributeValue.builder()
                    .l(list.stream().map(DynamoMapper::toAttr).toList())
                    .build();
        }
        if (v instanceof Map<?, ?> m) {
            Map<String, AttributeValue> nested = new HashMap<>();
            for (Map.Entry<?, ?> e : m.entrySet()) {
                nested.put(String.valueOf(e.getKey()), toAttr(e.getValue()));
            }
            return AttributeValue.builder().m(nested).build();
        }
        throw new IllegalArgumentException("Unsupported type for Dynamo mapping: " + v.getClass());
    }

    private static Object fromAttr(AttributeValue av) {
        if (av.nul() != null && av.nul()) return null;
        if (av.s() != null) return av.s();
        if (av.n() != null) {
            // Return as Double for simplicity; callers can cast/convert as needed
            try {
                if (av.n().contains("."))
                    return Double.parseDouble(av.n());
                // try Long if possible
                long l = Long.parseLong(av.n());
                if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) return (int) l;
                return l;
            } catch (NumberFormatException e) {
                return av.n(); // fallback raw string
            }
        }
        if (av.bool() != null) return av.bool();
        if (av.l() != null) return av.l().stream().map(DynamoMapper::fromAttr).toList();
        if (av.m() != null) {
            Map<String, Object> m = new HashMap<>();
            av.m().forEach((k, v) -> m.put(k, fromAttr(v)));
            return m;
        }
        return null;
    }
}
