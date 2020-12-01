package nl.ou.testar.TagVisualization;

public class TagFilter {
    private static ITagFilter instance;

    /**
     * Get the TagFilter interface.
     * @return The TagFilter interface.
     */
    public static ITagFilter getInstance() {
        return instance;
    }

    /**
     * Sets the actual implementation of the interface.
     * @param implementation The concrete implementation of the interface.
     */
    public static void setInstance(ITagFilter implementation) {
        instance = implementation;
    }
}