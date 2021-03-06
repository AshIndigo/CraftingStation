package com.ashindigo.craftingstation.widgets;

import com.ashindigo.craftingstation.CraftingStationContainer;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.Axis;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import java.util.List;

public class WItemListPanel extends WPanel {

    private final WBetterScrollbar scrollBar = new WBetterScrollbar(Axis.VERTICAL);
    private int lastScroll = -1;
    private final List<WListItemSlot> list;
    private final CraftingStationContainer gui;

    public WItemListPanel(List<WListItemSlot> data, CraftingStationContainer gui) {
        this.list = data;
        this.gui = gui;
        scrollBar.setMaxValue(data.size());
    }

    @Override
    public void paintBackground(int x, int y, int mouseX, int mouseY) {
        if (getBackgroundPainter() != null) {
            getBackgroundPainter().paintBackground(x, y, this);
        } else {
            ScreenDrawing.drawBeveledPanel(x, y, width, height);
        }

        if (scrollBar.getValue() != lastScroll) {
            gui.clearSlots();
            gui.triggerValidatation();
            lastScroll = scrollBar.getValue();
        }

        for (WWidget child : children) {
            if (child.getY() >= 0 && child.getY() <= 162) {
                child.paintBackground(x + child.getX(), y + child.getY(), mouseX - child.getX(), mouseY - child.getY());
            }
        }
    }

    @Override
    public void layout() {
        this.children.clear();
        this.children.add(scrollBar);
        int margin = 4;
        this.width = 54 + scrollBar.getWidth() + margin;
        this.height = 180;
        scrollBar.setLocation(this.width - scrollBar.getWidth(), 0);
        scrollBar.setSize(8, this.height);
        scrollBar.setMaxValue(list.size());
        int layoutHeight = this.getHeight() - (margin * 2);
        int cellHeight = 18;
        int cellsHigh = layoutHeight / cellHeight;
        scrollBar.setWindow(cellsHigh);
        int scrollOffset = scrollBar.getValue();
        for (int i = 0; i < list.size(); i++) {
            WListItemSlot slot = list.get(i);
            slot.setLocation(margin, margin + (cellHeight * (i - scrollOffset)));
            slot.setSize(this.width - (margin * 2) - scrollBar.getWidth(), cellHeight);
            children.add(slot);
        }
    }

}
