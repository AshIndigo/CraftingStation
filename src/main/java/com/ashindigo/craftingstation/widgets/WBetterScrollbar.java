package com.ashindigo.craftingstation.widgets;

import io.github.cottonmc.cotton.gui.widget.Axis;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;

/**
 * Just a setter for window
 */
@SuppressWarnings("WeakerAccess")
public class WBetterScrollbar extends WScrollBar {

    public WBetterScrollbar(Axis vertical) {
        super(vertical);
    }

    public void setWindow(int wind) {
        window = wind;
    }
}
