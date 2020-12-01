/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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

import static org.fruit.monkey.dialog.ToolTipTexts.*;
import static org.fruit.monkey.dialog.ToolTipTexts.maxTestTimeTTT;
import static org.fruit.monkey.dialog.ToolTipTexts.useRecordedTTT;

public class TimingPanel extends SettingsPanel {

  private static final long serialVersionUID = -5878966626046293031L;

  private JSpinner spnActionDuration;
  private JSpinner spnActionWaitTime;
  private JSpinner spnSutStartupTime;
  private JSpinner spnMaxTime;
  private JCheckBox checkUseRecordedTimes;

  public TimingPanel() {
    setLayout(null);

    addTimingControls();
    addTimingLabels();
  }

  private void addTimingControls() {
    spnActionDuration = new JSpinner();
    spnActionDuration.setBounds(143, 14, 100, 27);
    spnActionDuration.setModel(new SpinnerNumberModel(0.0d, 0.0d, null, 0.1d));
    spnActionDuration.setToolTipText(actionDurationTTT);
    add(spnActionDuration);

    spnActionWaitTime = new JSpinner();
    spnActionWaitTime.setBounds(143, 52, 100, 27);
    spnActionWaitTime.setModel(new SpinnerNumberModel(0.0d, 0.0d, null, 0.1d));
    spnActionWaitTime.setToolTipText(actionWaitTimeTTT);
    add(spnActionWaitTime);

    spnSutStartupTime = new JSpinner();
    spnSutStartupTime.setBounds(143, 90, 100, 27);
    spnSutStartupTime.setModel(new SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));
    spnSutStartupTime.setToolTipText(sutStartupTimeTTT);
    add(spnSutStartupTime);

    spnMaxTime = new JSpinner();
    spnMaxTime.setBounds(143, 128, 100, 31);
    spnMaxTime.setModel(new SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));
    spnMaxTime.setToolTipText(maxTestTimeTTT);
    add(spnMaxTime);

    checkUseRecordedTimes = new JCheckBox();
    checkUseRecordedTimes.setBounds(271, 177, 21, 21);
    checkUseRecordedTimes.setToolTipText(useRecordedTTT);
    add(checkUseRecordedTimes);
  }

  private void addTimingLabels() {
    JLabel jLabel2 = new JLabel("Action Duration:");
    jLabel2.setBounds(10, 14, 130, 14);
    jLabel2.setToolTipText(actionDurationTTT);
    add(jLabel2);

    JLabel jLabel3 = new JLabel("seconds");
    jLabel3.setBounds(256, 17, 52, 14);
    jLabel3.setToolTipText(actionDurationTTT);
    add(jLabel3);


    JLabel jLabel4 = new JLabel();
    jLabel4.setBounds(10, 52, 130, 14);
    jLabel4.setText("Action Wait Time:");
    jLabel4.setToolTipText(actionWaitTimeTTT);
    add(jLabel4);

    JLabel jLabel5 = new JLabel("seconds");
    jLabel5.setBounds(256, 55, 52, 14);
    jLabel5.setToolTipText(actionWaitTimeTTT);
    add(jLabel5);


    JLabel jLabel7 = new JLabel("seconds");
    jLabel7.setBounds(256, 93, 52, 14);
    jLabel7.setToolTipText(sutStartupTimeTTT);
    add(jLabel7);

    JLabel jLabel6 = new JLabel("SUT Startup Time:");
    jLabel6.setBounds(10, 90, 130, 14);
    jLabel6.setToolTipText(sutStartupTimeTTT);
    add(jLabel6);


    JLabel jLabel22 = new JLabel("Max. Test Time:");
    jLabel22.setBounds(10, 128, 130, 14);
    jLabel22.setToolTipText(maxTestTimeTTT);
    add(jLabel22);

    JLabel jLabel23 = new JLabel("seconds");
    jLabel23.setBounds(256, 131, 52, 14);
    jLabel23.setToolTipText(maxTestTimeTTT);
    add(jLabel23);

    JLabel jLabel24 = new JLabel("Use Recorded Action Timing during Replay:");
    jLabel24.setBounds(10, 177, 255, 14);
    jLabel24.setToolTipText(useRecordedTTT);
    add(jLabel24);
  }

  /**
   * Populate Timing Fields from Settings structure.
   *
   * @param settings The settings to load.
   */
  public void populateFrom(final Settings settings) {
    checkUseRecordedTimes.setSelected(settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay));
    spnActionWaitTime.setValue(settings.get(ConfigTags.TimeToWaitAfterAction));
    spnActionDuration.setValue(settings.get(ConfigTags.ActionDuration));
    spnSutStartupTime.setValue(settings.get(ConfigTags.StartupTime));
    spnMaxTime.setValue(settings.get(ConfigTags.MaxTime));
  }

  /**
   * Retrieve information from the Timing GUI.
   *
   * @param settings reference to the object where the settings will be stored.
   */
  public void extractInformation(final Settings settings) {
    settings.set(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay, checkUseRecordedTimes.isSelected());
    settings.set(ConfigTags.ActionDuration, (Double) spnActionDuration.getValue());
    settings.set(ConfigTags.TimeToWaitAfterAction, (Double) spnActionWaitTime.getValue());
    settings.set(ConfigTags.StartupTime, (Double) spnSutStartupTime.getValue());
    settings.set(ConfigTags.MaxTime, (Double) spnMaxTime.getValue());
  }
}
