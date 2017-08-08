package net.pobbay.service;

import net.pobbay.util.ColorPickerDialog.OnColorChangedListener;
import net.pobbay.view.Panel2;

public class ColorListener implements OnColorChangedListener {
	public int color = -16777216;
	public Panel2 panel2;

	public ColorListener(Panel2 p) {
		this.panel2 = p;
	}

	@Override
	public void colorChanged(int color) {

		this.color = color;
		panel2.setColor(color);
	}

}
