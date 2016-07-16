package ws.cpcs.adsiuba.jpaui.model;

/**
 * Name conversions
 */
public class NameUtils {

    public static String spaceSeparated(String camelCase) {
        String name = camelCase.replaceAll("\\B([A-Z])", " $1");
        if (Character.isUpperCase(name.charAt(0)))
            return name;
        else
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    public static String plural(String name) {
        return name + (name.matches(".*(s|se|sh|ge|ch)$") ? "es" : "s");
    }

    public static String dashSeparated(String spaceSeparated) {
        return spaceSeparated.replaceAll("\\s+","-").toLowerCase();
    }
}
