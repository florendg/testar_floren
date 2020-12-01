package org.fruit.monkey.dialog;

import nl.ou.testar.TagVisualization.DefaultTagFilter;
import nl.ou.testar.TagVisualization.TagFilter;
import org.fruit.monkey.Settings;
import org.fruit.monkey.SettingsPanel;

import javax.swing.*;
import java.awt.*;
import java.util.*;

class TreeSetListModel<T extends Comparable<T>> extends AbstractListModel<T> {
    private static final long serialVersionUID = 1L;
    private TreeSet<T> treeSet;

    public TreeSetListModel() {
        treeSet = new TreeSet<T>();
    }

    public TreeSetListModel(Comparator<? super T> comparator) {
        treeSet = new TreeSet<T>(comparator);
    }

    @Override
    public T getElementAt(int index) {
        if (index < 0 || index >= getSize()) {
            String s = "index, " + index + ", is out of bounds for getSize() = "
                    + getSize();
            throw new IllegalArgumentException(s);
        }
        Iterator<T> iterator = treeSet.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            T t = (T) iterator.next();
            if (index == count) {
                return t;
            }
            count++;
        }
        // out of index. return null. will probably never reach this
        return null;
    }

    @Override
    public int getSize() {
        return treeSet.size();
    }

    public int getIndexOf(T t) {
        int index = 0;
        for (T treeItem : treeSet) {
            if (treeItem.equals(treeItem)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public boolean add(T t) {
        boolean result = treeSet.add(t);
        if (result) {
            int index = getIndexOf(t);
            fireIntervalAdded(this, index, index + 1);
        }
        return result;
    }

    public boolean remove(T t) {
        int index = getIndexOf(t);
        if (index < 0) {
            return false;
        }
        boolean result = treeSet.remove(t);
        fireIntervalRemoved(this, index, index + 1);
        return result;
    }

    public void clear() {
        treeSet.clear();
    }
}

public class SpyModePanel extends SettingsPanel {

    private TreeSetListModel<String> excludeTags = new TreeSetListModel<>();
    private TreeSetListModel<String> includeTags = new TreeSetListModel<>();

    JList excludeList = new JList();
    JList includeList = new JList();


    public SpyModePanel() {
        // Create two lists
        FlowLayout rootLayout = new FlowLayout();
        setLayout(rootLayout);

        Dimension listDimension = new Dimension(275,350);

        // Left Panel
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(new JLabel("Exclude:"));

        excludeList.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        excludeList.setModel(excludeTags);

        JScrollPane leftScrollList = new JScrollPane(excludeList);
        leftScrollList.setPreferredSize(listDimension);
        left.add(leftScrollList);

        // Center Panel
        JPanel middle = new JPanel();
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));

        JButton allInclude = new JButton(">>");
        allInclude.addActionListener(e -> {
            int size = excludeTags.getSize();
            for (int i = 0 ; i < size; ++i){
                includeTags.add(excludeTags.getElementAt(i));
            }
            excludeList.clearSelection();
            excludeTags.clear();
            excludeList.updateUI();
        });
        middle.add(allInclude);

        JButton selectedInclude = new JButton(">");
        selectedInclude.addActionListener(e -> {
                    excludeList.getSelectedValuesList().forEach(i ->{
                        includeTags.add(i.toString());
                        excludeTags.remove(i.toString());
                    });
                    excludeList.clearSelection();
                }
        );
        middle.add(selectedInclude);

        JButton selectedExclude = new JButton("<");
        selectedExclude.addActionListener(e -> {
                    includeList.getSelectedValuesList().forEach(i -> {
                        excludeTags.add(i.toString());
                        includeTags.remove(i.toString());
                    });
                    includeList.clearSelection();
                }
        );
        middle.add(selectedExclude);

        JButton allExclude = new JButton("<<");
        allExclude.addActionListener(e -> {
            int size = includeTags.getSize();
            for (int i = 0 ; i < size; ++i){
                excludeTags.add(includeTags.getElementAt(i));
            }
            includeList.clearSelection();
            includeTags.clear();
            includeList.updateUI();
        });
        middle.add(allExclude);

        // Right Panel
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(new JLabel("Include:"));

        DefaultTagFilter.getList().forEach(i -> includeTags.add(i));
        includeList.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        includeList.setModel(includeTags);
        JScrollPane rightScrollList = new JScrollPane(includeList);
        rightScrollList.setPreferredSize(listDimension);
        right.add(rightScrollList);

        add(left);
        add(middle);
        add(right);
    }

    @Override
    public void extractInformation(Settings settings) {
        // TODO read the information from the settings file.

        // Create and set the new tag filter.
        Set<String> filter = new HashSet<>();
        int size = includeTags.getSize();
        for (int i = 0 ; i < size; ++i){
            filter.add(includeTags.getElementAt(i));
        }
        TagFilter.getInstance().setFilter(filter);
    }

    @Override
    public void populateFrom(Settings settings) {
        // TODO store the information in the settings file.
    }
}
