package nl.ou.testar.TagVisualization;

import es.upv.staq.testar.StateManagementTags;
import org.fruit.alayer.Tags;
import org.fruit.alayer.windows.UIATags;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DefaultTagFilter {

    private static final Set<String> allAvailableTags;
    static{
        // Fill the list with all the available tags.
        Set<String> tmpList = new HashSet<>();
        UIATags.getAllTags().forEach(t -> tmpList.add(t.name()));
        Tags.getAllBaseTags().forEach(t -> tmpList.add(t.name()));
        StateManagementTags.getAllTags().forEach(t -> tmpList.add(t.name()));
        allAvailableTags = Collections.unmodifiableSet(tmpList);
    }
    public static Set<String> getList() {
        return allAvailableTags;
    }
}
