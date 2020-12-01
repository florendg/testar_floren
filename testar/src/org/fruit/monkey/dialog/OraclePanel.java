/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018 Universitat Politecnica de Valencia - www.upv.es
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
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import static org.fruit.monkey.dialog.ToolTipTexts.suspiciousTitlesTTT;

public class OraclePanel extends SettingsPanel {

	private static final long serialVersionUID = -8633257917450402330L;

	private JTextArea txtSuspTitles;
	private JCheckBox processCheckBox;
	private JTextArea txtProcTitles;
	private JSpinner spnFreezeTime;

	public OraclePanel() {
		txtSuspTitles = new JTextArea();
		txtSuspTitles.setLineWrap(true);
		txtProcTitles = new JTextArea();
		txtProcTitles.setLineWrap(true);

		processCheckBox = new JCheckBox("Enable Process Listener");
		processCheckBox.setBounds(10, 128, 192, 20);
		add(processCheckBox);


		spnFreezeTime = new JSpinner();
		spnFreezeTime.setModel(new SpinnerNumberModel(1.0d, 1.0d, null, 1.0d));

		JScrollPane suspiciousTitlePane = new JScrollPane();
		suspiciousTitlePane.setViewportView(txtSuspTitles);
		JScrollPane suspiciousProcessPane = new JScrollPane();
		suspiciousProcessPane.setViewportView(txtProcTitles);


		JLabel suspiciousTitleLabel = new JLabel("Suspicious Titles:");
		suspiciousTitleLabel.setToolTipText(suspiciousTitlesTTT);
		JLabel suspiciousProcessLabel = new JLabel("Suspicious Process Output:");
		JLabel freezeTimeLabel = new JLabel("Freeze Time:");
		JLabel secondsLabel = new JLabel("seconds");

		GroupLayout gl_jPanelOracles = new GroupLayout(this);
		this.setLayout(gl_jPanelOracles);
		gl_jPanelOracles.setHorizontalGroup(
				gl_jPanelOracles.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(gl_jPanelOracles.createSequentialGroup()
						.addGap(10)
						.addGroup(gl_jPanelOracles.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addGroup(gl_jPanelOracles.createSequentialGroup()
										.addGroup(gl_jPanelOracles.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addGroup(gl_jPanelOracles.createSequentialGroup()
														.addComponent(freezeTimeLabel, PREFERRED_SIZE, 92, PREFERRED_SIZE)
														.addGap(10)
														.addComponent(spnFreezeTime, PREFERRED_SIZE, 95, PREFERRED_SIZE)
														.addGap(10)
														.addComponent(secondsLabel)
														.addGap(121))
												.addGroup(GroupLayout.Alignment.LEADING, gl_jPanelOracles.createSequentialGroup()
														.addComponent(suspiciousTitlePane, PREFERRED_SIZE, 600, PREFERRED_SIZE)
														.addGap(121))
												.addGroup(GroupLayout.Alignment.LEADING, gl_jPanelOracles.createSequentialGroup()
														.addComponent(suspiciousProcessLabel, PREFERRED_SIZE, 200, PREFERRED_SIZE)
														.addGap(121))
												.addGroup(GroupLayout.Alignment.LEADING, gl_jPanelOracles.createSequentialGroup()
														.addComponent(suspiciousProcessPane, PREFERRED_SIZE, 600, PREFERRED_SIZE)
														.addGap(121)))
										.addGap(23))
								.addGroup(gl_jPanelOracles.createSequentialGroup()
										.addPreferredGap(RELATED)
										.addComponent(suspiciousTitleLabel, PREFERRED_SIZE, 142, PREFERRED_SIZE)
										.addContainerGap(348, Short.MAX_VALUE))))
				);
		gl_jPanelOracles.setVerticalGroup(
				gl_jPanelOracles.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(gl_jPanelOracles.createSequentialGroup()
						.addGap(18)
						.addComponent(suspiciousTitleLabel)
						.addGap(240)
						.addGroup(gl_jPanelOracles.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(spnFreezeTime, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
								.addComponent(secondsLabel)
								.addComponent(freezeTimeLabel))
						.addContainerGap(120, Short.MAX_VALUE))
				.addGroup(gl_jPanelOracles.createSequentialGroup()
						.addGap(160)
						.addComponent(suspiciousProcessLabel)
						.addGap(10)
						.addComponent(suspiciousProcessPane, PREFERRED_SIZE, 80, PREFERRED_SIZE)
						.addContainerGap(156, Short.MAX_VALUE))
				.addGroup(gl_jPanelOracles.createSequentialGroup()
						.addGap(45)
						.addComponent(suspiciousTitlePane, PREFERRED_SIZE, 80, PREFERRED_SIZE)
						.addContainerGap(156, Short.MAX_VALUE))
				);
	}

	/**
	 * Populate Oracle Fields from Settings structure.
	 * @param settings The settings to load.
	 */
	public void populateFrom(final Settings settings) {
		txtSuspTitles.setText(settings.get(ConfigTags.SuspiciousTitles));
		processCheckBox.setSelected(settings.get(ConfigTags.ProcessListenerEnabled));
		txtProcTitles.setText(settings.get(ConfigTags.SuspiciousProcessOutput));
		spnFreezeTime.setValue(settings.get(ConfigTags.TimeToFreeze));
	}

	/**
	 * Retrieve information from the Oracle GUI.
	 * @param settings reference to the object where the settings will be stored.
	 */
	public void extractInformation(final Settings settings) {
		settings.set(ConfigTags.SuspiciousTitles, txtSuspTitles.getText());
		settings.set(ConfigTags.ProcessListenerEnabled, processCheckBox.isSelected());
		settings.set(ConfigTags.SuspiciousProcessOutput, txtProcTitles.getText());
		settings.set(ConfigTags.TimeToFreeze, (Double) spnFreezeTime.getValue());
	}
}
