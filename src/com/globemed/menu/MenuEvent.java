package com.globemed.menu;

/**
 *
 * @author Pasan
 */
public interface MenuEvent {

    public void menuSelected(String menuName, int subIndex, MenuAction action);
}
