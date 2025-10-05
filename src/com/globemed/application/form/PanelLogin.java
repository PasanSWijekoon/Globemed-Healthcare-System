package com.globemed.application.form;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Pasan
 */
public class PanelLogin extends JPanel {

    public PanelLogin() {
        setLayout(new MigLayout("fillx,wrap,insets 30 40 50 40, width 320", "[fill]", "[]20[][]10[]"));
        putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Login.background;"
                + "arc:20;");
    }

}
