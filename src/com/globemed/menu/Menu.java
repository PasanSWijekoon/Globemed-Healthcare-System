package com.globemed.menu;

import com.globemed.menu.mode.LightDarkMode;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import com.globemed.menu.mode.ToolBarAccentColor;
import com.globemed.model.Role;
import com.globemed.model.User;
import net.miginfocom.swing.MigLayout;
import com.globemed.auth.permissions.Permission;

/**
 *
 * @author Pasan
 */
public class Menu extends JPanel {

    private final String menuItems[][] = {
        {"~MAIN~"},
        {"Dashboard"},
        {"~PATIENTS~"},
        {"Manage Patients"},
        {"~Manage~"},
        {"Appointments"},
        {"Billing"},
        {"Staff"},
        {"~COMPONENT~"},
        {"Reports"},
        {"Security"},
        {"Logout"}
    };

    public boolean isMenuFull() {
        return menuFull;
    }

    public void setMenuFull(boolean menuFull) {
        this.menuFull = menuFull;
        if (menuFull) {
            header.setText(headerName);
            lbUsername.setVisible(true);
            lbUserType.setVisible(true);
        } else {
            header.setText("");
            header.setHorizontalAlignment(JLabel.CENTER);
            lbUsername.setVisible(false);
            lbUserType.setVisible(false);
        }
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                ((MenuItem) com).setFull(menuFull);
            }
        }
        lightDarkMode.setMenuFull(menuFull);
        toolBarAccentColor.setMenuFull(menuFull);

        revalidate();
    }

    private final List<MenuEvent> events = new ArrayList<>();
    private boolean menuFull = true;
    private JPanel panelHeader;
    private final String headerName = "GlobeMed Health";
    private JLabel lbUsername;
    private JLabel lbUserType;
    protected final boolean hideMenuTitleOnMinimum = true;
    protected final int menuTitleLeftInset = 5;
    protected final int menuTitleVgap = 5;
    protected final int menuMaxWidth = 250;
    protected final int menuMinWidth = 60;
    protected final int headerFullHgap = 5;
    private User currentUser;

    public Menu() {
        init();
    }

    private void init() {
        setLayout(new MenuLayout());
        putClientProperty(FlatClientProperties.STYLE, ""
                + "border:20,2,2,2;"
                + "background:$Menu.background;"
                + "arc:10");

        panelHeader = new JPanel(new MigLayout("wrap, fillx, insets 0, gapy 0, align center", "[center]"));
        panelHeader.setOpaque(false);

        header = new JLabel(headerName);
        header.setIcon(new ImageIcon(getClass().getResource("/com/globemed/icon/png/logo.png")));
        header.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$Menu.header.font;"
                + "foreground:$Menu.foreground");

        lbUsername = new JLabel();
        lbUserType = new JLabel();
        lbUsername.putClientProperty(FlatClientProperties.STYLE, "font:-2; foreground:$Menu.foreground");
        lbUserType.putClientProperty(FlatClientProperties.STYLE, "font:-2; foreground:$Menu.foreground");

        panelHeader.add(header);
        panelHeader.add(lbUsername, "gaptop 10");
        panelHeader.add(lbUserType);

        //  Menu
        scroll = new JScrollPane();
        panelMenu = new JPanel(new MenuItemLayout(this));
        panelMenu.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:5,5,5,5;"
                + "background:$Menu.background");

        scroll.setViewportView(panelMenu);
        scroll.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:null");
        JScrollBar vscroll = scroll.getVerticalScrollBar();
        vscroll.setUnitIncrement(10);
        vscroll.putClientProperty(FlatClientProperties.STYLE, ""
                + "width:$Menu.scroll.width;"
                + "trackInsets:$Menu.scroll.trackInsets;"
                + "thumbInsets:$Menu.scroll.thumbInsets;"
                + "background:$Menu.ScrollBar.background;"
                + "thumb:$Menu.ScrollBar.thumb");

        lightDarkMode = new LightDarkMode();
        toolBarAccentColor = new ToolBarAccentColor(this);
        toolBarAccentColor.setVisible(FlatUIUtils.getUIBoolean("AccentControl.show", false));
        add(panelHeader);
        add(scroll);
        add(lightDarkMode);
        add(toolBarAccentColor);
    }

    public void setUserInfo(User user) {
        lbUsername.setText(user.getEmail());
        lbUserType.setText("User Type : " + user.getRole().toString());
        this.currentUser = user;
        createMenu();
    }

    private void createMenu() {
        panelMenu.removeAll();
        int index = 0;
        for (String[] menuItemData : menuItems) {
            String menuName = menuItemData[0];
            if (menuName.startsWith("~") && menuName.endsWith("~")) {
                panelMenu.add(createTitle(menuName));
            } else {
                if (shouldShowMenuItem(menuName)) {
                    MenuItem menuItem = new MenuItem(this, menuItemData, index++, events);
                    panelMenu.add(menuItem);
                }
            }
        }
        revalidate();
        repaint();
    }

    private boolean shouldShowMenuItem(String menuName) {
        if (currentUser == null) {
            return false;
        }
        switch (menuName) {
            case "Dashboard":
                return currentUser.hasPermission(Permission.VIEW_DASHBOARD);
            case "Manage Patients":
                return currentUser.hasPermission(Permission.MANAGE_PATIENTS);
            case "Appointments":
                return currentUser.hasPermission(Permission.MANAGE_APPOINTMENTS);
            case "Billing":
                return currentUser.hasPermission(Permission.ACCESS_BILLING);
            case "Staff":
                return currentUser.hasPermission(Permission.MANAGE_STAFF);
            case "Reports":
                return currentUser.hasPermission(Permission.VIEW_REPORTS);
            case "Security":
                return currentUser.hasPermission(Permission.MANAGE_SECURITY);
            case "Logout":
                return currentUser.hasPermission(Permission.LOGOUT);
            default:
                return false;
        }
    }

    private JLabel createTitle(String title) {
        String menuName = title.substring(1, title.length() - 1);
        JLabel lbTitle = new JLabel(menuName);
        lbTitle.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$Menu.label.font;"
                + "foreground:$Menu.title.foreground");
        return lbTitle;
    }

    public void setSelectedMenu(String menuName, int subIndex) {
        runEvent(menuName, subIndex);
    }

    protected void setSelected(String menuName, int subIndex) {
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                MenuItem item = (MenuItem) com;
                if (item.getMenus()[0].equals(menuName)) {
                    item.setSelectedIndex(subIndex);
                } else {
                    item.setSelectedIndex(-1);
                }
            }
        }
    }

    protected void runEvent(String menuName, int subIndex) {
        MenuAction menuAction = new MenuAction();
        for (MenuEvent event : events) {
            event.menuSelected(menuName, subIndex, menuAction);
        }
        if (!menuAction.isCancel()) {
            setSelected(menuName, subIndex);
        }
    }

    public void addMenuEvent(MenuEvent event) {
        events.add(event);
    }

    public void hideMenuItem() {
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                ((MenuItem) com).hideMenuItem();
            }
        }
        revalidate();
    }

    public boolean isHideMenuTitleOnMinimum() {
        return hideMenuTitleOnMinimum;
    }

    public int getMenuTitleLeftInset() {
        return menuTitleLeftInset;
    }

    public int getMenuTitleVgap() {
        return menuTitleVgap;
    }

    public int getMenuMaxWidth() {
        return menuMaxWidth;
    }

    public int getMenuMinWidth() {
        return menuMinWidth;
    }

    private JLabel header;
    private JScrollPane scroll;
    private JPanel panelMenu;
    private LightDarkMode lightDarkMode;
    private ToolBarAccentColor toolBarAccentColor;

    private class MenuLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(5, 5);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int x = insets.left;
                int y = insets.top;
                int gap = UIScale.scale(5);
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);

                Component panelHeader = null;
                for (Component comp : parent.getComponents()) {
                    if (comp instanceof JPanel && ((JPanel) comp).getLayout() instanceof MigLayout) {
                        panelHeader = comp;
                        break;
                    }
                }

                int headerHeight = 0;
                if (panelHeader != null) {
                    headerHeight = panelHeader.getPreferredSize().height;
                    panelHeader.setBounds(x, y, width, headerHeight);
                }

                int accentColorHeight = 0;
                if (toolBarAccentColor.isVisible()) {
                    accentColorHeight = toolBarAccentColor.getPreferredSize().height + gap;
                }

                int ldgap = UIScale.scale(10);
                int ldWidth = width - ldgap * 2;
                int ldHeight = lightDarkMode.getPreferredSize().height;
                int ldx = x + ldgap;
                int ldy = y + height - ldHeight - ldgap - accentColorHeight;

                int menux = x;
                int menuy = y + headerHeight;
                int menuWidth = width;
                int menuHeight = height - (headerHeight + (menuFull ? gap : 0)) - (ldHeight + ldgap * 2) - (accentColorHeight);
                scroll.setBounds(menux, menuy, menuWidth, menuHeight);

                lightDarkMode.setBounds(ldx, ldy, ldWidth, ldHeight);

                if (toolBarAccentColor.isVisible()) {
                    int tbheight = toolBarAccentColor.getPreferredSize().height;
                    int tbwidth = Math.min(toolBarAccentColor.getPreferredSize().width, ldWidth);
                    int tby = y + height - tbheight - ldgap;
                    int tbx = ldx + ((ldWidth - tbwidth) / 2);
                    toolBarAccentColor.setBounds(tbx, tby, tbwidth, tbheight);
                }
            }
        }
    }
}
