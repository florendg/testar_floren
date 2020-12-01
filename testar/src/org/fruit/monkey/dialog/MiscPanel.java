/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 Open Universiteit - www.ou.nl
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

import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.monkey.SettingsPanel;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import static javax.swing.LayoutStyle.ComponentPlacement.UNRELATED;
import static org.fruit.monkey.dialog.ToolTipTexts.copyFilesTTT;
import static org.fruit.monkey.dialog.ToolTipTexts.deleteFiles;

public class MiscPanel extends SettingsPanel {

  private static final long serialVersionUID = 8435304494218446662L;

  private File currentDirectory;
  private JTable tblCopyFromTo;
  private JTable tblDelete;
  private JTextField txtOutputDir;
  private JTextField txtTempDir;

  public MiscPanel() {
    // Init the global items
    initMiscGlobal();

    // Init the local items
    JScrollPane jScrollPane4 = new JScrollPane();
    jScrollPane4.setViewportView(tblDelete);

    JScrollPane jScrollPane5 = new JScrollPane();
    jScrollPane5.setViewportView(tblCopyFromTo);

    JLabel jLabel16 = new JLabel("Copy Files on SUT Startup:");
    jLabel16.setToolTipText(copyFilesTTT);
    JLabel jLabel8 = new JLabel("Output Directory:");
    JLabel jLabel9 = new JLabel("Temp Directory:");
    JLabel jLabel20 = new JLabel("Delete Files on SUT Startup:");
    jLabel20.setToolTipText(deleteFiles);

    JButton btnSetOutputDir = new JButton("Select");
    btnSetOutputDir.addActionListener(this::btnSetOutputDirActionPerformed);
    btnSetOutputDir.setEnabled(true);

    JButton btnSetTempDir = new JButton("Select");
    btnSetTempDir.addActionListener(this::btnSetTempDirActionPerformed);
    btnSetTempDir.setEnabled(true);

    GroupLayout gl_jPanelMisc = new GroupLayout(this);
    this.setLayout(gl_jPanelMisc);
    gl_jPanelMisc.setHorizontalGroup(
        gl_jPanelMisc.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(gl_jPanelMisc.createSequentialGroup()
                .addContainerGap()
                .addGroup(gl_jPanelMisc.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addComponent(jScrollPane4)
                    .addGroup(gl_jPanelMisc.createSequentialGroup()
                        .addGroup(gl_jPanelMisc.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(gl_jPanelMisc.createSequentialGroup()
                                .addGroup(gl_jPanelMisc.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addGroup(gl_jPanelMisc.createSequentialGroup()
                                        .addComponent(jLabel8, PREFERRED_SIZE, 92, PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtOutputDir, PREFERRED_SIZE, 346, PREFERRED_SIZE))
                                    .addGroup(gl_jPanelMisc.createSequentialGroup()
                                        .addComponent(jLabel9, PREFERRED_SIZE, 92, PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtTempDir, PREFERRED_SIZE, 346, PREFERRED_SIZE))
                                    .addComponent(jLabel16, PREFERRED_SIZE, 393, PREFERRED_SIZE))
                                .addPreferredGap(RELATED)
                                .addGroup(gl_jPanelMisc.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(btnSetOutputDir, PREFERRED_SIZE, 90, PREFERRED_SIZE)
                                    .addComponent(btnSetTempDir, PREFERRED_SIZE, 90, PREFERRED_SIZE)))
                            .addComponent(jLabel20, PREFERRED_SIZE, 393, PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
    );
    gl_jPanelMisc.setVerticalGroup(
        gl_jPanelMisc.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(gl_jPanelMisc.createSequentialGroup()
                .addContainerGap()
                .addGroup(gl_jPanelMisc.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOutputDir, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(btnSetOutputDir, PREFERRED_SIZE, 20, PREFERRED_SIZE))
                .addPreferredGap(RELATED)
                .addGroup(gl_jPanelMisc.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTempDir, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(btnSetTempDir, PREFERRED_SIZE, 20, PREFERRED_SIZE))
                .addPreferredGap(UNRELATED)
                .addComponent(jLabel16)
                .addPreferredGap(RELATED)
                .addComponent(jScrollPane5, PREFERRED_SIZE, 101, PREFERRED_SIZE)
                .addPreferredGap(UNRELATED)
                .addComponent(jLabel20)
                .addPreferredGap(RELATED)
                .addComponent(jScrollPane4, PREFERRED_SIZE, 103, PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
    );

  }

  private void initMiscGlobal() {
    tblDelete = new JTable();
    tblDelete.setModel(new javax.swing.table.DefaultTableModel(
        new Object[50][1], new String[]{"File / Directory"}) {
      private static final long serialVersionUID = 1L;
      Class<?>[] types = new Class<?>[]{String.class};

      public Class<?> getColumnClass(int columnIndex) {
        return types[columnIndex];
      }
    });
    tblDelete.setToolTipText("<html>\nFiles to delete before SUT start: Certain SUTs generate configuration files, temporary files and files<br>\nthat save the system's state. This might be problematic during sequence replay, when you want a<br>\nsystem to always start in the same state. Therefore, you can specify these files, to be deleted<br>\nbefore the SUT gets started. If you double-click the text-area a file dialog will pop up which allows<br>\nselecting files and directories to be deleted.\n</html>");
    tblDelete.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        tblDeleteMouseClicked(evt);
      }
    });

    tblCopyFromTo = new JTable();
    tblCopyFromTo.setModel(new javax.swing.table.DefaultTableModel(
        new Object[50][2], new String[]{
        "Source File / Directory", "Destination"}
    ) {
      private static final long serialVersionUID = 1L;
      Class<?>[] types = new Class<?>[]{String.class, String.class};

      public Class<?> getColumnClass(int columnIndex) {
        return types[columnIndex];
      }
    });
    tblCopyFromTo.setToolTipText("<html>\nFiles to copy before SUT start. It is useful to restore certain<br>\n configuration files to their default. Therefore you can define pairs of paths (copy from / to).<br>\nTESTAR will copy each specified file from the given source location to the given destination.<br>\nSimply double-click the text-area and a file dialog will pop up.\n</html>");
    tblCopyFromTo.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        tblCopyFromToMouseClicked(evt);
      }
    });

    txtOutputDir = new JTextField();
    txtOutputDir.setEditable(false);

    txtTempDir = new JTextField();
    txtTempDir.setEditable(false);
  }

  private void btnSetOutputDirActionPerformed(ActionEvent evt) {
    JFileChooser fd = new JFileChooser();
    fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fd.setCurrentDirectory(currentDirectory);
    if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String file = fd.getSelectedFile().getAbsolutePath();
      txtOutputDir.setText(file);
    }
  }

  private void btnSetTempDirActionPerformed(ActionEvent evt) {
    JFileChooser fd = new JFileChooser();
    fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fd.setCurrentDirectory(currentDirectory);

    if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String file = fd.getSelectedFile().getAbsolutePath();
      txtTempDir.setText(file);
    }
  }

  private void tblCopyFromToMouseClicked(MouseEvent evt) {
    if (tblCopyFromTo.getSelectedColumn() >= 0 && tblCopyFromTo.getSelectedRow() >= 0) {
      JFileChooser fd = new JFileChooser();
      fd.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

      if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        String file = fd.getSelectedFile().getAbsolutePath();
        tblCopyFromTo.setValueAt(file, tblCopyFromTo.getSelectedRow(), tblCopyFromTo.getSelectedColumn());
      }
      else {
        tblCopyFromTo.setValueAt(null, tblCopyFromTo.getSelectedRow(), tblCopyFromTo.getSelectedColumn());
      }
    }
  }

  private void tblDeleteMouseClicked(MouseEvent evt) {
    if (tblDelete.getSelectedRow() >= 0 && tblDelete.getSelectedColumn() >= 0) {
      JFileChooser fd = new JFileChooser();
      fd.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

      if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        String file = fd.getSelectedFile().getAbsolutePath();
        tblDelete.setValueAt(file, tblDelete.getSelectedRow(), tblDelete.getSelectedColumn());
      }
      else {
        tblDelete.setValueAt(null, tblDelete.getSelectedRow(), tblDelete.getSelectedColumn());
      }
    }
  }

  /**
   * Populate Misc Fields from Settings structure.
   *
   * @param settings The settings to load.
   */
  public void populateFrom(final Settings settings) {
    currentDirectory = new File(settings.get(ConfigTags.OutputDir)).getParentFile();

    txtOutputDir.setText(settings.get(ConfigTags.OutputDir));
    txtTempDir.setText(settings.get(ConfigTags.TempDir));

    for (int i = 0; i < tblCopyFromTo.getRowCount(); i++) {
      tblCopyFromTo.setValueAt(null, i, 0);
      tblCopyFromTo.setValueAt(null, i, 1);
    }

    int i = 0;
    for (Pair<String, String> fromTo : settings.get(ConfigTags.CopyFromTo)) {
      tblCopyFromTo.setValueAt(fromTo.left(), i, 0);
      tblCopyFromTo.setValueAt(fromTo.right(), i, 1);
      i++;
    }

    for (i = 0; i < tblDelete.getRowCount(); i++) {
      tblDelete.setValueAt(null, i, 0);
    }

    i = 0;
    for (String f : settings.get(ConfigTags.Delete)) {
      tblDelete.setValueAt(f, i, 0);
      i++;
    }
  }

  /**
   * Retrieve information from the Misc GUI.
   *
   * @param settings reference to the object where the settings will be stored.
   */
  public void extractInformation(final Settings settings) {
    settings.set(ConfigTags.OutputDir, txtOutputDir.getText());
    settings.set(ConfigTags.TempDir, txtTempDir.getText());

    List<Pair<String, String>> copyFromTo = Util.newArrayList();
    for (int i = 0; i < tblCopyFromTo.getRowCount(); i++) {
      String left = (String) tblCopyFromTo.getValueAt(i, 0);
      String right = (String) tblCopyFromTo.getValueAt(i, 1);

      if (left != null && right != null) {
        copyFromTo.add(Pair.from(left, right));
      }
    }
    settings.set(ConfigTags.CopyFromTo, copyFromTo);

    List<String> delete = Util.newArrayList();
    for (int i = 0; i < tblDelete.getRowCount(); i++) {
      String value = (String) tblDelete.getValueAt(i, 0);
      if (value != null) {
        delete.add(value);
      }
    }
    settings.set(ConfigTags.Delete, delete);
  }

  public void checkSettings() throws IllegalStateException {
    for (int i = 0; i < tblCopyFromTo.getRowCount(); i++) {
      String left = (String) tblCopyFromTo.getValueAt(i, 0);
      String right = (String) tblCopyFromTo.getValueAt(i, 1);

      if (left != null || right != null) {
        if ((left != null && right == null) ||
            (left == null && right != null) ||
            left.trim().equals("") ||
            right.trim().equals("")) {
          throw new IllegalStateException(
              "CopyFromTo Table has unfinished entries!");
        }
      }
    }
  }
}
