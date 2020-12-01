/***************************************************************************************************
*
* Copyright (c) 2013 - 2020 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 - 2020 Open Universiteit - www.ou.nl
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

package org.fruit.monkey;

import es.upv.staq.testar.serialisation.LogSerialiser;
import javafx.util.Pair;
import nl.ou.testar.StateModel.Settings.StateModelPanel;

import org.fruit.Util;
import org.fruit.monkey.dialog.AboutPanel;
import org.fruit.monkey.dialog.FilterPanel;
import org.fruit.monkey.dialog.GeneralPanel;
import org.fruit.monkey.dialog.MiscPanel;
import org.fruit.monkey.dialog.OraclePanel;
import org.fruit.monkey.dialog.SpyModePanel;
import org.fruit.monkey.dialog.TimingPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.logging.Logger.getLogger;
import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import static javax.swing.UIManager.*;
import static org.fruit.monkey.dialog.ToolTipTexts.*;


/**
 * This class takes care of the SettingsDialogue of TESTAR (the TESTAR GUI).
 */

public class SettingsDialog extends JFrame implements Observer {
    private static final long serialVersionUID = 5156320008281200950L;

    static final String TESTAR_VERSION = "2.2.12 (26-Nov-2020)";


    private String settingsFile;
    private Settings settings;
    //TODO: what is this ret variable. Cant you just return settings in the run method?
    private Settings ret;

    private JButton btnGenerate;
    private JButton btnSpy;
    private JButton btnReplay;
    private JButton btnView;
    private JButton btnRecord;

    private static final int GENERAL_TAB_INDEX = 1;
    private Map<Integer, Pair<String, SettingsPanel>> settingPanels = new HashMap<>();

    /**
     * Starts the settings Dialog.
     * @throws IOException when Icons cannot be found.
     */
    SettingsDialog() throws IOException {
        getContentPane().setBackground(Color.WHITE);
        try {
            for (LookAndFeelInfo info : getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            getLogger(SettingsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        this.setIconImage(loadIcon("/icons/logos/TESTAR.jpg"));

        ToolTipManager.sharedInstance().setDismissDelay(25000);
        ToolTipManager.sharedInstance().setInitialDelay(100);
        initComponents();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveCurrentSettings();
            }
        });
    }

    /**
     *
     * @param settings
     * @param settingsFile
     * @return
     */
    public Settings run(Settings settings, String settingsFile) {
        this.settings = settings;
        this.settingsFile = settingsFile;
        this.ret = null;
        this.setVisible(true);
        populateInformation(settings);

        while (this.isShowing())
            Util.pause(0.1);

        return ret;
    }

    public static Image loadIcon(String path) throws IOException {
        return ImageIO.read(SettingsDialog.class.getResourceAsStream(path));
    }

    /**
     * This is the method that is called when you click on one of the big mode buttons in TESTAR dialog
     * @param mode indicates the MODE button that was clicked.
     */
    private void start(RuntimeControlsProtocol.Modes mode) {
        try {
            extractInformation(settings);
            checkSettings(settings);
            settings.set(ConfigTags.Mode, mode);
            saveCurrentSettings();
            ret = settings;
            this.dispose();
        } catch (IllegalStateException ise) {
            JOptionPane.showMessageDialog(this, ise.getMessage(), "Invalid Settings!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkSettings(Settings settings) throws IllegalStateException {
        String userInputPattern = settings.get(ConfigTags.ProcessesToKillDuringTest);
        try {
            Pattern.compile(userInputPattern);
        } catch (PatternSyntaxException exception) {
            throw new IllegalStateException("Your ProcessFilter is not a valid regular expression!");
        }

        userInputPattern = settings.get(ConfigTags.ClickFilter);
        try {
            Pattern.compile(userInputPattern);
        } catch (PatternSyntaxException exception) {
            throw new IllegalStateException("Your ClickFilter is not a valid regular expression!");
        }

        userInputPattern = settings.get(ConfigTags.SuspiciousTitles);
        try {
            Pattern.compile(userInputPattern);
        } catch (PatternSyntaxException exception) {
            throw new IllegalStateException("Your Oracle is not a valid regular expression!");
        }

        if (!new File(settings.get(ConfigTags.OutputDir)).exists()) {
            throw new IllegalStateException("Output Directory does not exist!");
        }
        if (!new File(settings.get(ConfigTags.TempDir)).exists()) {
            throw new IllegalStateException("Temp Directory does not exist!");
        }

        settingPanels.forEach((k, v) -> v.getValue().checkSettings());
    }

    private void saveCurrentSettings() {
        extractInformation(settings);
        try {
            Util.saveToFile(settings.toFileString(), settingsFile);
            Settings.setSettingsPath(settingsFile.substring(0, settingsFile.indexOf("test.settings") - 1));
            System.out.println("Saved current settings to <" + settingsFile + ">");
        } catch (IOException e1) {
            LogSerialiser.log("Unable to save current settings to <" + settingsFile + ">: " + e1.toString() + "\n");
        }
    }

    /**
     * This replaces the original test.settings file with a complete settings file
     * @param sutSettings
     */
    private void switchSettings(String sutSettings) {
        String previousSSE = Main.getSSE()[0];
        String sse = sutSettings + Main.SUT_SETTINGS_EXT;
        if (previousSSE.equals(sse)) {
            return;
        }
        saveCurrentSettings();
        String settingsDir = Main.settingsDir;
        new File(settingsDir + previousSSE).renameTo(new File(settingsDir + sse));
        try {
            settingsFile = settingsDir + sutSettings + File.separator + Main.SETTINGS_FILE;
            settings = Main.loadSettings(new String[0], settingsFile);
            populateInformation(settings);
            System.out.println("Switched to <" + settingsFile + ">");
            Main.SSE_ACTIVATED = sutSettings;

        } catch (ConfigException cfe) {
            LogSerialiser.log("Unable to switch to <" + sutSettings + "> settings!\n");
        }
    }

    private void populateInformation(Settings settings) {
        settingPanels.forEach((k, v) -> v.getValue().populateFrom(settings));
    }

    private void extractInformation(Settings settings) {
        settingPanels.forEach((k, v) -> v.getValue().extractInformation(settings));
    }

    private void initComponents() throws IOException {
        // Init start buttons
        btnGenerate = getBtnGenerate();
        btnSpy = getBtnSpy();
        btnReplay = getBtnReplay();
        btnView = getBtnView();
        btnRecord = getBtnRecord();

        JTabbedPane jTabsPane = new JTabbedPane();
        jTabsPane.addTab("About", new AboutPanel());
        settingPanels.put(GENERAL_TAB_INDEX, new Pair("General Settings", new GeneralPanel(this)));
        settingPanels.put(settingPanels.size() + 1, new Pair("Filters", new FilterPanel()));
        settingPanels.put(settingPanels.size() + 1, new Pair("Oracles", new OraclePanel()));
        settingPanels.put(settingPanels.size() + 1, new Pair("Time Settings", new TimingPanel()));
        settingPanels.put(settingPanels.size() + 1, new Pair("Misc", new MiscPanel()));
        settingPanels.put(settingPanels.size() + 1, new Pair("State Model", StateModelPanel.createStateModelPanel()));
        settingPanels.put(settingPanels.size() + 1, new Pair("Spy mode", new SpyModePanel()));

        settingPanels.forEach((k, v) -> jTabsPane.add(v.getKey(), v.getValue()));

        setLayout(jTabsPane);
        pack();
        setCentreScreen();
    }

    /**
     * Make the window appear centre screen
     */
    private void setCentreScreen() {
        Dimension scrDim = Toolkit.getDefaultToolkit().getScreenSize();

        Rectangle bounds = new Rectangle();
        bounds.x = (int) scrDim.getWidth() / 2 - getWidth() / 2;
        bounds.y = (int) scrDim.getHeight() / 2 - getHeight() / 2;
        bounds.width = getWidth();
        bounds.height = getHeight();

        setBounds(bounds);
    }

    private void setLayout(JTabbedPane jTabsPane) {
        jTabsPane.setSelectedIndex(GENERAL_TAB_INDEX);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("TESTAR " + TESTAR_VERSION);
        setLocationByPlatform(true);
        setName("TESTAR Settings"); // NOI18N
        setResizable(false);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                                        .addComponent(jTabsPane, PREFERRED_SIZE, 620, PREFERRED_SIZE)
                                        .addGroup(getStartGroup(layout)))
                                .addContainerGap(DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(btnGenerate, PREFERRED_SIZE, 129, PREFERRED_SIZE)
                                        .addComponent(btnSpy, PREFERRED_SIZE, 129, PREFERRED_SIZE)
                                        .addComponent(btnReplay, PREFERRED_SIZE, 129, PREFERRED_SIZE)
                                        .addComponent(btnView, PREFERRED_SIZE, 129, PREFERRED_SIZE)
                                        .addComponent(btnRecord, PREFERRED_SIZE, 129, PREFERRED_SIZE)
                                )
                                .addPreferredGap(RELATED)
                                .addComponent(jTabsPane, PREFERRED_SIZE, 400, PREFERRED_SIZE)
                                .addContainerGap())
        );
    }

    private GroupLayout.SequentialGroup getStartGroup(GroupLayout layout) {
        GroupLayout.SequentialGroup group = layout.createSequentialGroup();

        group.addComponent(btnSpy, 120, 120, 120);
        group.addGap(2, 2, 2);
        group.addComponent(btnGenerate, 120, 120, 120);
        group.addGap(2, 2, 2);
        group.addComponent(btnRecord, 120, 120, 120);
        group.addGap(2, 2, 2);
        group.addComponent(btnReplay, 120, 120, 120);
        group.addGap(2, 2, 2);
        group.addComponent(btnView, 120, 120, 120);

        return group;
    }

    private JButton getBtnGenerate() throws IOException {
        JButton btn = new JButton();
        btn.setBackground(new Color(255, 255, 255));
        btn.setIcon(new ImageIcon(loadIcon("/icons/button_generate.png")));
        btn.setToolTipText(btnGenerateTTT);
        btn.setFocusPainted(false);
        btn.addActionListener(this::btnGenerateActionPerformed);
        return btn;
    }

    private void btnGenerateActionPerformed(ActionEvent evt) {
        start(RuntimeControlsProtocol.Modes.Generate);
    }

    private JButton getBtnSpy() throws IOException {
        JButton btn = new JButton();
        btn.setBackground(new Color(255, 255, 255));
        btn.setIcon(new ImageIcon(loadIcon("/icons/button_spy.png")));
        btn.setToolTipText(btnSpyTTT);
        btn.setFocusPainted(false);
        btn.addActionListener(this::btnSpyActionPerformed);
        return btn;
    }

    private void btnSpyActionPerformed(ActionEvent evt) {
        start(RuntimeControlsProtocol.Modes.Spy);
    }

    private JButton getBtnReplay() throws IOException {
        JButton btn = new JButton();
        btn.setBackground(new Color(255, 255, 255));
        btn.setIcon(new ImageIcon(loadIcon("/icons/button_replay.png")));
        btn.setToolTipText(btnReplayTTT);
        btn.setFocusPainted(false);
        btn.addActionListener(this::btnReplayActionPerformed);
        return btn;
    }

    private void btnReplayActionPerformed(ActionEvent evt) {
        JFileChooser fd = new JFileChooser();
        fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fd.setCurrentDirectory(new File(settings.get(ConfigTags.PathToReplaySequence)).getParentFile());

        if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String file = fd.getSelectedFile().getAbsolutePath();
            settings.set(ConfigTags.PathToReplaySequence, file);
            start(RuntimeControlsProtocol.Modes.Replay);
        }
    }

    private JButton getBtnView() throws IOException {
        JButton btn = new JButton();
        btn.setBackground(new Color(255, 255, 255));
        btn.setIcon(new ImageIcon(loadIcon("/icons/button_view.png")));
        btn.setToolTipText(btnViewTTT);
        btn.setFocusPainted(false);
        btn.addActionListener(this::btnViewActionPerformed);
        return btn;
    }

    private void btnViewActionPerformed(ActionEvent evt) {
        JFileChooser fd = new JFileChooser();
        fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fd.setCurrentDirectory(new File(settings.get(ConfigTags.PathToReplaySequence)).getParentFile());

        if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String file = fd.getSelectedFile().getAbsolutePath();
            settings.set(ConfigTags.PathToReplaySequence, file);
            start(RuntimeControlsProtocol.Modes.View);
        }
    }

    private JButton getBtnRecord() throws IOException {
        JButton btn = new JButton();
        btn.setBackground(new Color(255, 255, 255));
        btn.setIcon(new ImageIcon(loadIcon("/icons/button_record.png")));
        btn.setToolTipText(btnRecordTTT);
        btn.setFocusPainted(false);
        btn.addActionListener(this::btnRecordActionPerformed);
        return btn;
    }

    private void btnRecordActionPerformed(ActionEvent evt) {
        start(RuntimeControlsProtocol.Modes.Record);
    }

    @Override
    public void update(Observable o, Object arg) {
        switchSettings((String) arg);
    }
}
