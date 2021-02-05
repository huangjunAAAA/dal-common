package com.boring.dal.config.sqllinker;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.stat.TableStat;

import java.util.ArrayList;
import java.util.List;

public class TableConditionExt extends TableStat.Condition {
	private List<SQLObject> exprs=new ArrayList<>();
	public TableConditionExt(TableStat.Column column, String operator) {
		super(column, operator);
	}

	public List<SQLObject> getExprs() {
		return exprs;
	}

	public void addExpr(SQLObject expr) {
		this.exprs.add(expr);
	}
}
