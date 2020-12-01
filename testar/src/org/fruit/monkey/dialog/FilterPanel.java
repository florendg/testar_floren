/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2020 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.fruit.monkey.dialog;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.monkey.SettingsPanel;

import javax.swing.*;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static org.fruit.monkey.dialog.ToolTipTexts.label1TTT;
import static org.fruit.monkey.dialog.ToolTipTexts.label2TTT;

public class FilterPanel extends SettingsPanel {

    private static final long serialVersionUID = 1572649050808020748L;

    private JTextArea txtClickFilter;
    private JTextArea txtProcessFilter;

    public FilterPanel() {
        JLabel filterByTitleLabel = new JLabel(
                "Filter actions by the widget its TITLE property (regular expression):");
        filterByTitleLabel.setToolTipText(label1TTT);
        JLabel killProcessByNameLabel = new JLabel(
                "Kill processes by name (regular expression):");
        killProcessByNameLabel.setToolTipText(label2TTT);

        JScrollPane filterByTitlePane = new JScrollPane();
        JScrollPane killProcessByNamePane = new JScrollPane();

        GroupLayout gl_jPanelFilters = new GroupLayout(this);
        this.setLayout(gl_jPanelFilters);
        gl_jPanelFilters.setHorizontalGroup(
                gl_jPanelFilters.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gl_jPanelFilters.createSequentialGroup()
                                .addGroup(gl_jPanelFilters.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(gl_jPanelFilters.createSequentialGroup()
                                                .addGap(24)
                                                .addComponent(killProcessByNamePane, PREFERRED_SIZE, 572, PREFERRED_SIZE))
                                        .addGroup(gl_jPanelFilters.createSequentialGroup()
                                                .addGap(24)
                                                .addComponent(filterByTitlePane, PREFERRED_SIZE, 572, PREFERRED_SIZE))
                                        .addGroup(gl_jPanelFilters.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(gl_jPanelFilters.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(killProcessByNameLabel, PREFERRED_SIZE, 357, PREFERRED_SIZE)
                                                        .addComponent(filterByTitleLabel, PREFERRED_SIZE, 381, PREFERRED_SIZE))))
                                .addContainerGap(24, Short.MAX_VALUE))
        );
        gl_jPanelFilters.setVerticalGroup(
                gl_jPanelFilters.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gl_jPanelFilters.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(filterByTitleLabel)
                                .addGap(18)
                                .addComponent(filterByTitlePane, DEFAULT_SIZE, 108, Short.MAX_VALUE)
                                .addGap(18)
                                .addComponent(killProcessByNameLabel)
                                .addGap(18)
                                .addComponent(killProcessByNamePane, PREFERRED_SIZE, 121, PREFERRED_SIZE)
                                .addGap(22))
        );

        txtProcessFilter = new JTextArea();
        txtProcessFilter.setLineWrap(true);
        killProcessByNamePane.setViewportView(txtProcessFilter);

        txtClickFilter = new JTextArea();
        txtClickFilter.setLineWrap(true);
        filterByTitlePane.setViewportView(txtClickFilter);

    }

    /**
     * Populate Filter Fields from Settings structure.
     *
     * @param settings The settings to load.
     */
    public void populateFrom(final Settings settings) {
        txtClickFilter.setText(settings.get(ConfigTags.ClickFilter));
        txtProcessFilter.setText(settings.get(ConfigTags.ProcessesToKillDuringTest));
    }

    /**
     * Retrieve information from the Filter  GUI.
     *
     * @param settings reference to the object where the settings will be stored.
     */
    public void extractInformation(final Settings settings) {
        settings.set(ConfigTags.ClickFilter, txtClickFilter.getText());
        settings.set(ConfigTags.ProcessesToKillDuringTest, txtProcessFilter.getText());
    }
}
