package org.endeavour.enterprise.model;

import java.util.HashMap;
import java.util.Map;

public enum Module {
	Library(0),
	Searches(1),
	Reports(2);

	private int value;
	Module(final int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	private static Map<Integer, Module> map = new HashMap();

	static {
		for (Module module : Module.values())
			map.put(module.getValue(), module);
	}

	public static Module valueOf(int module) {
		return map.get(module);
	}
}
