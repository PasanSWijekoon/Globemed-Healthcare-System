
package com.globemed.application.form.other;

/**
 *
 * @author pasan
 */
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class Card extends JPanel {

    public Card(String title, String value) {
        setLayout(new MigLayout("wrap, fillx", "[fill]", "[]unrelated[]"));
        putClientProperty(FlatClientProperties.STYLE, "arc: 20");

        JLabel lblTitle = new JLabel(title);
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +1");
        add(lblTitle);

        JLabel lblValue = new JLabel(value);
        lblValue.putClientProperty(FlatClientProperties.STYLE, "font:bold +5");
        add(lblValue);
    }
}
