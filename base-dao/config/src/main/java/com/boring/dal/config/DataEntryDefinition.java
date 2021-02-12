package com.boring.dal.config;

import java.util.ArrayList;
import java.util.List;

public abstract class DataEntryDefinition extends DataEntry {

	private List<String> classNames=new ArrayList<>();

	public List<String> getClassNames() {
		return classNames;
	}

	public void setClassNames(List<String> classNames) {
		this.classNames = classNames;
	}

	protected void addClass(String name){
		classNames.add(name);
	}

	public abstract void init();
}
