package com.piston.mc.extracellsrenderfix.init;

import com.piston.mc.extracellsrenderfix.ModMain;

public class InitServer implements Init {

	@Override
	public void init() {
		ModMain.log.info("This mod does nothing on the server! Please remove me!");
	}

}
