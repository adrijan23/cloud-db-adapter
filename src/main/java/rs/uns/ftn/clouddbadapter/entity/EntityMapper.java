package rs.uns.ftn.clouddbadapter.entity;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Reflection helper for mapping annotated entities (<@Entity>, <@Id>)
 * to and from plain Map<String,Object>.
 */
public final class EntityMapper {

    private EntityMapper() {}

    /** Returns the collection name for the given class. */
    public static String collection(Class<?> type) {
        Entity ann = type.getAnnotation(Entity.class);
        if (ann == null)
            throw new IllegalArgumentException("Missing @Entity on " + type.getName());
        return ann.collection();
    }

    /** Returns the ID field for this type (field annotated with @Id), or throws. */
    private static Field idField(Class<?> type) {
        for (Field f : type.getDeclaredFields()) {
            if (f.isAnnotationPresent(Id.class)) {
                f.setAccessible(true);
                return f;
            }
        }
        throw new IllegalStateException("No @Id field in " + type.getName());
    }

    /** Reads the ID value from the entity, or null if none. */
    public static String idValue(Object entity) {
        try {
            Field f = idField(entity.getClass());
            Object v = f.get(entity);
            return v != null ? v.toString() : null;
        } catch (Exception e) {
            throw new RuntimeException("Cannot get id", e);
        }
    }

    /** Sets the ID field of the entity. */
    public static void setId(Object entity, String id) {
        try {
            Field f = idField(entity.getClass());
            f.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Cannot set id", e);
        }
    }

    /** Converts entity to a simple map. */
    public static Map<String, Object> toMap(Object entity) {
        Map<String, Object> m = new HashMap<>();
        for (Field f : entity.getClass().getDeclaredFields()) {
            try {
                f.setAccessible(true);
                Object v = f.get(entity);
                if (v != null) m.put(f.getName(), v);
            } catch (Exception ignore) {}
        }
        return m;
    }

    /** Creates an entity instance from a map. */
    @SuppressWarnings("unchecked")
    public static <T> T fromMap(Class<T> type, Map<String, Object> m) {
        try {
            T obj = type.getDeclaredConstructor().newInstance();
            for (Field f : type.getDeclaredFields()) {
                if (m.containsKey(f.getName())) {
                    f.setAccessible(true);
                    Object val = m.get(f.getName());
                    if (val != null && !f.getType().isAssignableFrom(val.getClass())) {
                        val = convert(val, f.getType());
                    }
                    f.set(obj, val);
                }
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create " + type.getName(), e);
        }
    }

    private static Object convert(Object v, Class<?> target) {
        if (v == null) return null;
        if (target == String.class) return v.toString();
        if (target == int.class || target == Integer.class) return Integer.parseInt(v.toString());
        if (target == long.class || target == Long.class) return Long.parseLong(v.toString());
        if (target == boolean.class || target == Boolean.class) return Boolean.parseBoolean(v.toString());
        return v;
    }
}
