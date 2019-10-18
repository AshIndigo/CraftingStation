package com.ashindigo.craftingstation.widgets;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.Axis;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import java.util.List;

public class WItemListPanel extends WPanel {
    private int cellHeight = 18;

    private int margin = 4;

    private WBetterScrollbar scrollBar = new WBetterScrollbar(Axis.VERTICAL);
    private int lastScroll = -1;
    private List<WItemSlot> list;
    private GuiDescription gui;

    public WItemListPanel(List<WItemSlot> data, GuiDescription gui) {
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
            layout();
            lastScroll = scrollBar.getValue();
        }

        for (WWidget child : children) {
            child.paintBackground(x + child.getX(), y + child.getY(), mouseX - child.getX(), mouseY - child.getY());
        }
    }

    @Override
    public void layout() {
        this.children.clear();
        this.children.add(scrollBar);
        this.width = 54 + scrollBar.getWidth() + margin;
        this.height = 180;
        scrollBar.setLocation(this.width - scrollBar.getWidth(), 0);
        scrollBar.setSize(8, this.height);
        scrollBar.setMaxValue(list.size());
        int layoutHeight = this.getHeight() - (margin * 2);
        int cellsHigh = layoutHeight / cellHeight;
        scrollBar.setWindow(cellsHigh);
        int scrollOffset = scrollBar.getValue();
        int presentCells = Math.min(list.size() - scrollOffset, cellsHigh);
        if (presentCells > 0) {
            for (int i = 0; i < presentCells; i++) {
                int index = i + scrollOffset;
                if (index >= list.size()) break;
                if (index < 0) continue;
                WItemSlot w = list.get(index);
                if (w.canResize()) {
                    w.setSize(this.width - (margin * 2) - scrollBar.getWidth(), cellHeight);
                }
                w.setLocation(margin, margin + (cellHeight * i));
                this.children.add(w);
            }
        }
    }

}
