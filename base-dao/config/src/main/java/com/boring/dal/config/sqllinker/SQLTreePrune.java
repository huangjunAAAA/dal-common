package com.boring.dal.config.sqllinker;

import com.boring.dal.config.DataEntry;

public interface SQLTreePrune {
	PrunedResult treePrune(DataEntry de, Object[] params);


	class PrunedResult{
		public DataEntry de;
		public Object[] params;

		public PrunedResult(DataEntry de,Object[] params){
			this.de=de;
			this.params=params;
		}
	}
}
