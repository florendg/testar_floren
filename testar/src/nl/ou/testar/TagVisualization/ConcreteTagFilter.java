package nl.ou.testar.TagVisualization;

import org.fruit.alayer.Tag;

import java.util.Set;

public class ConcreteTagFilter implements ITagFilter {
    private Set<String> filter;

    public void setFilter(Set<String> newFilter) {
        filter = newFilter;
    }

    public boolean visualizeTag(Tag t)
    {
        return filter.contains(t.name());
    }
}
