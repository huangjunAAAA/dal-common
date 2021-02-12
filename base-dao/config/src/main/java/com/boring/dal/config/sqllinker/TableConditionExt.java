package com.boring.dal.config.sqllinker;


import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.stat.TableStat;


public class TableConditionExt extends TableStat.Condition {
	private SQLObject expr;

	public TableConditionExt(TableStat.Column column, String operator) {
		super(column, operator);
	}

	public SQLObject getExpr() {
		return expr;
	}

	public void setExpr(SQLObject expr) {
		this.expr = expr;
	}


}
