package org.fruit.monkey.dialog;

import org.fruit.monkey.Settings;
import org.fruit.monkey.SettingsDialog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static javax.swing.GroupLayout.PREFERRED_SIZE;

public class MethodologyPanel extends JPanel {
  private JLabel testarLogo = getLogo("/icons/info/methodology.jpg");

  public MethodologyPanel () throws IOException {
    setBackground(Color.WHITE);

    GroupLayout aboutPanelLayout = new GroupLayout(this);
    setLayout(aboutPanelLayout);
    aboutPanelLayout.setHorizontalGroup(
        aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(aboutPanelLayout.createSequentialGroup()
                 .addContainerGap()
            	 .addComponent(testarLogo, PREFERRED_SIZE, 500, PREFERRED_SIZE)
                 .addContainerGap()
            )
    );
    aboutPanelLayout.setVerticalGroup(
        aboutPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
            .addGroup(aboutPanelLayout.createSequentialGroup()
            	.addContainerGap()	
                .addComponent(testarLogo, PREFERRED_SIZE,500, PREFERRED_SIZE)
                .addContainerGap()               
                )
    );
  }

  private JLabel getLogo (String iconPath) throws IOException{
    return new JLabel(new ImageIcon(SettingsDialog.loadIcon(iconPath)));
  }

  public void populateFrom(Settings settings) {
  }
}
