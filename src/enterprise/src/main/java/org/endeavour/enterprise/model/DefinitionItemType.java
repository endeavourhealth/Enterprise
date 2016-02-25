package org.endeavour.enterprise.model;

public enum DefinitionItemType {
	Folder(0),
	Report(1),
	Query(2),
	Test(3),
	Datasource(4),
	CodeSet(5),
	ListOutput(6); //2016-02-25 DL - added

	private int value;

	DefinitionItemType(final int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	//2016-02-25 DL - not used
	/*private static Map<Integer, DefinitionItemType> map = new HashMap();

	static {
		for (DefinitionItemType definitionItemType : DefinitionItemType.values())
			map.put(definitionItemType.get(), definitionItemType);
	}

	public static DefinitionItemType valueOf(int definitionItemType) {
		return map.get(definitionItemType);
	}*/

	public static DefinitionItemType get(int value) {
		for(DefinitionItemType e: DefinitionItemType.values()) {
			if(e.value == value) {
				return e;
			}
		}
		return null; // not found
	}
}
