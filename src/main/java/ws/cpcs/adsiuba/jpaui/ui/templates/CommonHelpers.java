package ws.cpcs.adsiuba.jpaui.ui.templates;

import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommonHelpers {

    public static String eq(Object first, Object second) {
        return Objects.equals(first, second) ? "true" : null;
    }

    public static StringBuilder each(Collection list, Options opts) throws IOException {
        StringBuilder buff = new StringBuilder();
        if (list != null) {
            int i = 0;
            if (opts.hash("reversed") != null) {
                List copy = new ArrayList(list);
                Collections.reverse(copy);
                list = copy;
            }
            for (Object e: list) {
                opts.data("index", i);
                opts.data("first", i == 0 ? true : null);
                opts.data("last", i + 1 == list.size() ? true : null);
                buff.append(opts.fn(e));
                i++;
            }
        }
        return buff;
    }

    public static String concat(Object param, Options opts) {
        return Stream.concat(Stream.of(param), Stream.of(opts.params))
                .map(String::valueOf)
                .collect(Collectors.joining(opts.hash("sep", "")));
    }
}
