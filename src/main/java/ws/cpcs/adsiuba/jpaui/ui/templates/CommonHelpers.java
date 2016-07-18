package ws.cpcs.adsiuba.jpaui.ui.templates;

import com.github.jknack.handlebars.Options;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CommonHelpers implements HelperService {

    public String eq(Object first, Object second) {
        return Objects.equals(String.valueOf(first), String.valueOf(second)) ? "true" : null;
    }

    public StringBuilder each(Object coll, Options opts) throws IOException {
        StringBuilder buff = new StringBuilder();
        if (coll != null) {
            int i = 0;
            Collection list;
            if (coll instanceof Map) {
                list = new ArrayList(((Map) coll).entrySet());
            } else if (coll instanceof Collection) {
                list = (Collection) coll;
            } else {
                throw new IllegalArgumentException(coll +
                        " expected to be Collection or Map, but got "+ coll.getClass());
            }
            if (opts.hash("reversed") != null) {
                List copy = new ArrayList((Collection) list);
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

    public StringBuilder times(int n, Options opts) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            opts.data("index", i);
            opts.data("number", i + 1);
            opts.data("first", i == 0 ? true : null);
            opts.data("last", i + 1 == n ? true : null);
            sb.append(opts.fn());
        }
        return sb;
    }

    public String add(int n, Object o) {
        return String.valueOf(Long.valueOf(String.valueOf(o)) + n);
    }

    public String concat(Object param, Options opts) {
        return Stream.concat(Stream.of(param), Stream.of(opts.params))
                .map(String::valueOf)
                .collect(Collectors.joining(opts.hash("sep", "")));
    }
}
