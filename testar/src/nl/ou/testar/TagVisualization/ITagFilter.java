package nl.ou.testar.TagVisualization;

import org.fruit.alayer.Tag;
import java.util.Set;

public interface ITagFilter {
    /**
     * Set the new filter containing the tags we are allowed to visualize.
     * @param newFilter The new filter.
     */
    void setFilter(Set<String> newFilter);

    /**
     * Check if we may visualize the tag.
     * @param tag The tag we want to visualize.
     * @return True if it's allowed to visualize the tag.
     */
    boolean visualizeTag(Tag tag);
}
