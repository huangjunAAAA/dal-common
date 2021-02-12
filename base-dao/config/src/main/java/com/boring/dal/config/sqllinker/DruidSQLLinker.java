package com.boring.dal.config.sqllinker;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.boring.dal.config.Constants;
import com.boring.dal.config.DataEntry;
import com.boring.dal.config.ObjectUtil;
import com.boring.dal.config.SQLVarInfo;
import com.boring.dal.config.aap.AnnotationAssist;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class DruidSQLLinker implements FieldSQLConnector,SQLTreePrune {

	@Autowired
	private AnnotationAssist annotationAssist;

    private static Logger logger = LogManager.getLogger("DAO");

    private DbType sqlType= DbType.mysql;

    private Map<String, Map<String, Method>> clsmap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        String sql = "select s4id,s1.id,tid,s2.sid,max(s1.id4), max(id28), max(s1.id2)+1, min(s2.id3-0.1/s1.pp2)*2,now() n1, now(),count(*) from savior_claim_data s1, sss2table s2 where policy_type = ? and status in (:p0) and type in (:p1) and create_at between ? and ? " +
                " and s1.pa=? and sss2table.pp1 =? and s2.pa=? and s1.param=s2.param and (:p2) > now() and ? > s2.pa6+convert(now())  order by s1.create_at,update_at desc";
        SQLSelectStatement stmt = null;
        try {
            stmt = (SQLSelectStatement) SQLUtils.parseStatements(sql, JdbcConstants.ORACLE).get(0);
        } catch (ParserException e) {
            logger.error(e.getMessage(), e);
        }

		MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) stmt.getSelect().getQuery();

        List<String> cols = new DruidSQLLinker().getConditionedColumnList(stmt);
        System.out.println(cols);

        List<String> vcols = new DruidSQLLinker().getValueColumnList(stmt);
        System.out.println(vcols);

        List<String> ocols = new DruidSQLLinker().getOrderBy(stmt);
        System.out.println(ocols);
    }

    public DruidSQLLinker(){

    }
    public DruidSQLLinker(@NonNull DbType dbType){
        this.sqlType=dbType;
    }
    @Override
    public void linkDataEntry(DataEntry de, List<Class> clazz) {
        SQLSelectStatement stmt = null;
        String sqlitem = de.getSql();
        try {
            stmt = (SQLSelectStatement) SQLUtils.parseStatements(sqlitem, sqlType).get(0);
        } catch (ParserException e) {
            logger.fatal("unable to parse sql:" + sqlitem);
            return;
        }
		de.setStmt(stmt);

        initMethodMap(clazz);

        LinkedList<SQLVarInfo> km = new LinkedList<>();
        List<String> cols = getConditionedColumnList(stmt);
        for (Iterator<String> iterator = cols.iterator(); iterator.hasNext(); ) {
            String col = iterator.next();
            Method m = getFieldGetter(col);
            boolean isCollection = col.indexOf("[]") >= 0;
            km.add(new SQLVarInfo(m, isCollection));
        }
        de.setKeyProperties(km);

		List<Integer> varDist = getConditionVarDistribution(stmt);
		de.setSqlVarDist(varDist);

        LinkedHashMap<String, Class> vhm = new LinkedHashMap<>();
        List<String> vcols = getValueColumnList(stmt);
        for (Iterator<String> iterator = vcols.iterator(); iterator.hasNext(); ) {
            String col = iterator.next();
            String[] names = col.split(Constants.NAME_DELIMITER);
            Class c = getFieldClass(names[0]);
            vhm.put(names[1], c);
        }
        de.setValueProperties(vhm);

        LinkedList<Method> obm = new LinkedList<>();
        List<String> olst = getOrderBy(stmt);
        for (Iterator<String> iterator = olst.iterator(); iterator.hasNext(); ) {
            String col = iterator.next();
            Method m = getFieldGetter(col);
            obm.add(m);
        }
        de.setOrderByProperties(obm);

        List<String> tlst = getTables(stmt);
        Map<String, Class> cmap = new HashMap<String, Class>();
        for (Iterator<Class> iterator = clazz.iterator(); iterator.hasNext(); ) {
            Class c = iterator.next();
			String tname = annotationAssist.getDbTableName(c);
            cmap.put(tname, c);
        }

        LinkedList<Class> tablecls = new LinkedList<>();
        for (Iterator<String> iterator = tlst.iterator(); iterator.hasNext(); ) {
            String t = iterator.next();
            Class c = cmap.get(t);
            tablecls.add(c);
        }

        de.setRelatedClass(tablecls);
    }

    private Class getFieldClass(String col) {
        if (col.startsWith(Constants.TYPE_INTEGER))
            return Integer.class;
        if (col.startsWith(Constants.TYPE_FLOAT))
            return Float.class;
        if (col.startsWith(Constants.TYPE_DOUBLE))
            return Double.class;
        if (col.startsWith(Constants.TYPE_STRING))
            return String.class;
        Method m = getFieldGetter(col);
        if (m == null) {
            return String.class;
        }
        return m.getReturnType();
    }

    private Method getFieldGetter(String col) {
        if (col.startsWith(Constants.ANY_FUNC))
            return null;
        String[] mps = col.split("\\" + Constants.TYPE_DELIMITER);
        String simpleCol = mps[1].replace("[]", "");
        String[] possibleTables = mps[0].split("\\" + Constants.TABLE_DELIMITER);
        for (int i = 0; i < possibleTables.length; i++) {
            String table = possibleTables[i];
            Map<String, Method> methodmap = clsmap.get(table);
            if (methodmap == null)
                return null;
            Method m = methodmap.get(simpleCol);
            if (m != null)
                return m;
        }
        return null;
    }

    private void initMethodMap(List<Class> clazz) {
        for (Iterator<Class> iterator = clazz.iterator(); iterator.hasNext(); ) {
            Class cls = iterator.next();
			String tname = annotationAssist.getDbTableName(cls);
            if (tname == null)
                continue;
            HashMap<String, Method> getters = new HashMap<>();
            clsmap.put(tname, getters);
            Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
				String fname = annotationAssist.getDbFieldName(f);
                if (fname == null)
                    continue;
                try {
                    Method m = cls.getMethod(ObjectUtil.getGetterName(f.getName()));
                    if (StringUtils.isNotEmpty(fname))
                        getters.put(fname, m);
                    else
                        getters.put(f.getName(), m);
                } catch (NoSuchMethodException e) {
                    logger.warn(e, e);
                }
            }
        }
    }



	public ArrayList<String> getColumnFromCondition(TableConditionExt c) {
		ArrayList<String> cols = new ArrayList<>();
		if (c.getOperator().equals(SQLBinaryOperator.Is.name) || c.getOperator().equals(SQLBinaryOperator.IsNot.name))
			return cols;
		boolean inSupport = c.getOperator().equalsIgnoreCase("IN") || c.getOperator().equalsIgnoreCase("NOT IN");
		List<SQLVariantRefExpr> vars = findAllVariants(c.getExpr());
		for (int i = 0; i < vars.size(); i++) {
			cols.add(c.getColumn().getTable() + "." + c.getColumn().getName() + (inSupport ? "[]" : ""));
		}
		return cols;
	}

	private List<SQLVariantRefExpr> findAllVariants(SQLObject so){
		List<SQLVariantRefExpr> vars=new ArrayList<>();
		if(so instanceof SQLVariantRefExpr){
			vars.add((SQLVariantRefExpr) so);
			return vars;
		}
		else if(so instanceof SQLBinaryOpExpr){
			SQLBinaryOpExpr binOp= (SQLBinaryOpExpr) so;
			vars.addAll(findAllVariants(binOp.getRight()));
			vars.addAll(findAllVariants(binOp.getLeft()));
			return vars;
		}else if(so instanceof SQLBetweenExpr){
			SQLBetweenExpr betweenExpr= (SQLBetweenExpr) so;
			vars.addAll(findAllVariants(betweenExpr.getBeginExpr()));
			vars.addAll(findAllVariants(betweenExpr.getEndExpr()));
			return vars;
		}else if(so instanceof SQLInListExpr){
			SQLInListExpr sqlInListExpr= (SQLInListExpr) so;
			for (Iterator<SQLExpr> iterator = sqlInListExpr.getTargetList().iterator(); iterator.hasNext(); ) {
				SQLExpr expr = iterator.next();
				vars.addAll(findAllVariants(expr));
			}
			return vars;
		}
		return vars;
	}

    private List<String> getValueColumnList(SQLSelectStatement stmt, String anyTable) {
        ArrayList<String> ret = new ArrayList<>();
        List<SQLSelectItem> vlst = stmt.getSelect().getQueryBlock().getSelectList();
        for (Iterator<SQLSelectItem> iterator = vlst.iterator(); iterator.hasNext(); ) {
            SQLSelectItem ve = iterator.next();
            SQLExpr expr = ve.getExpr();
            String type = getSelectItemExpr(expr);
            type = type.replace(Constants.UNKNOWN_TABLE, anyTable);
            if (ve.getAlias() != null)
                ret.add(type + Constants.NAME_DELIMITER + ve.getAlias());
            else {
                ret.add(type + Constants.NAME_DELIMITER + ve.toString());
            }
        }
        return ret;
    }

    public List<String> getTables(SQLSelectStatement stmt) {
        SchemaStatVisitor v = SQLUtils.createSchemaStatVisitor(sqlType);
        stmt.accept(v);
        List<String> ret = new ArrayList<>();
        Map<TableStat.Name, TableStat> tables = v.getTables();
        for (Iterator<TableStat.Name> iterator = tables.keySet().iterator(); iterator.hasNext(); ) {
            TableStat.Name name = iterator.next();
            ret.add(name.toString());
        }
        return ret;
    }

    private String getTablesCombo(SQLSelectStatement stmt) {
        SchemaStatVisitor v = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(v);
        Map<TableStat.Name, TableStat> tables = v.getTables();
        StringBuilder at = new StringBuilder();
        for (Iterator<TableStat.Name> iterator = tables.keySet().iterator(); iterator.hasNext(); ) {
            TableStat.Name name = iterator.next();
            at.append(name);
            if (iterator.hasNext())
                at.append(Constants.TABLE_DELIMITER);
        }
        return at.toString();
    }

    private String getSelectItemExpr(SQLObject se) {
        if (se instanceof SQLIdentifierExpr) {
            return Constants.UNKNOWN_TABLE + Constants.TYPE_DELIMITER + ((SQLIdentifierExpr) se).getName();
        }
        if (se instanceof SQLPropertyExpr) {
            SQLPropertyExpr s1 = (SQLPropertyExpr) se;
            SQLObject from = s1.getResolvedOwnerObject();
            if (from != null) {
                return (s1.getResolvedOwnerObject() + "." + s1.getName());
            } else {
                return Constants.UNKNOWN_TABLE + Constants.TYPE_DELIMITER + s1.getName();
            }
        }

        if (se instanceof SQLAggregateExpr) {
            SQLAggregateExpr aggr = (SQLAggregateExpr) se;
            if (aggr.getMethodName().equals("count")) {
                return Constants.TYPE_INTEGER;
            }

            for (Iterator<SQLExpr> iterator = aggr.getArguments().iterator(); iterator.hasNext(); ) {
                SQLExpr arg = iterator.next();
                String r = getSelectItemExpr(arg);
                if (r != null)
                    return r;
            }
        }

        if (se instanceof SQLNumericLiteralExpr) {
            return Constants.TYPE_DOUBLE;
        }

        if (se instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr be = (SQLBinaryOpExpr) se;
            String lr = getSelectItemExpr(be.getLeft());
            if (lr != null)
                return lr;
            String rr = getSelectItemExpr(be.getRight());
            if (rr != null)
                return rr;
        }

        return Constants.TYPE_STRING;
    }

    public List<String> getConditionedColumnList(SQLSelectStatement stmt) {
        String anyt = getTablesCombo(stmt);
        return getConditionedColumnList(stmt, anyt);
    }

    public List<String> getValueColumnList(SQLSelectStatement stmt) {
        String anyt = getTablesCombo(stmt);
        return getValueColumnList(stmt, anyt);
    }

    public List<String> getOrderBy(SQLSelectStatement stmt) {
        SchemaStatVisitor v = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(v);
        List<TableStat.Column> orderby = v.getOrderByColumns();
        String anyt = getTablesCombo(stmt);
        List<String> ret = new ArrayList<>();
        for (Iterator<TableStat.Column> iterator = orderby.iterator(); iterator.hasNext(); ) {
            TableStat.Column c = iterator.next();
            String lw = c.toString();
            lw = lw.replace(Constants.UNKNOWN_TABLE, anyt);
            ret.add(lw);
        }
        return ret;
    }

    public List<String> getConditionedColumnList(SQLSelectStatement stmt, String anyTable) {
        SchemaStatVisitorExt v = SchemaStatVisitorExt.createSchemaStatVisitorExt(sqlType);
        stmt.accept(v);
        ArrayList<String> ret = new ArrayList<>();
        List<TableConditionExt> cs = v.getConditions();
        for (Iterator<TableConditionExt> iterator = cs.iterator(); iterator.hasNext(); ) {
            TableConditionExt c = iterator.next();
            ArrayList<String> l = getColumnFromCondition(c);
            for (Iterator<String> stringIterator = l.iterator(); stringIterator.hasNext(); ) {
                String lw = stringIterator.next();
                lw = lw.replace(Constants.UNKNOWN_TABLE, anyTable);
                ret.add(lw);
            }
        }
        return ret;
    }

	public List<Integer> getConditionVarDistribution(SQLSelectStatement stmt) {
		SchemaStatVisitorExt v = SchemaStatVisitorExt.createSchemaStatVisitorExt(sqlType);
		stmt.accept(v);
		ArrayList<Integer> ret = new ArrayList<>();
		List<TableConditionExt> cs = v.getConditions();
		for (Iterator<TableConditionExt> iterator = cs.iterator(); iterator.hasNext(); ) {
			TableConditionExt c = iterator.next();
			ArrayList<String> l = getColumnFromCondition(c);
			ret.add(l.size());
		}
		return ret;
	}

	public String removeSQLParameters(SQLSelectStatement stmt, Integer... idxToRemoved){
		SchemaStatVisitorExt v = SchemaStatVisitorExt.createSchemaStatVisitorExt(DbType.mysql);
		stmt.accept(v);

		List<TableConditionExt> condiLst = v.getConditions();
		for (Iterator<TableConditionExt> iterator = condiLst.iterator(); iterator.hasNext(); ) {
			TableConditionExt next = iterator.next();
			if(getColumnFromCondition(next).size()==0)
				iterator.remove();
		}
		for (int i = 0; i < idxToRemoved.length; i++) {
			int idxd = idxToRemoved[i];
			TableConditionExt toDel = condiLst.get(idxd);
			removeConditionExpr(toDel.getExpr());
		}

		return stmt.toString();
	}

	private void removeConditionExpr(SQLObject wc){
		SQLObject parent=wc.getParent();
		if(parent instanceof SQLSelectQueryBlock){
			SQLSelectQueryBlock query= (SQLSelectQueryBlock) parent;
			query.setWhere(null);
		}else if(parent instanceof SQLBinaryOpExpr){
			SQLBinaryOpExpr bOp= (SQLBinaryOpExpr) parent;
			SQLExpr sibling = bOp.getLeft() == wc ? bOp.getRight() : bOp.getLeft();
			SQLObject grandParent = parent.getParent();
			if(grandParent instanceof SQLSelectQueryBlock){
				SQLSelectQueryBlock query= (SQLSelectQueryBlock) grandParent;
				query.setWhere(sibling);
			}else if (grandParent instanceof SQLBinaryOpExpr){
				// replace parent
				SQLBinaryOpExpr gbOp= (SQLBinaryOpExpr) grandParent;
				boolean left = gbOp.getLeft() == parent ? true : false;
				if(left){
					gbOp.setLeft(sibling);
				}else
					gbOp.setRight(sibling);
			}
			sibling.setParent(parent.getParent());
		}
	}

	@Override
	public PrunedResult treePrune(DataEntry de, Object[] params) {
		boolean allok=true;
		for (int i = 0; params!=null&&i < params.length; i++) {
			Object p = params[i];
			if(p==null)
				allok=false;
		}
		if(allok)
			return new PrunedResult(de,params);

		List<Integer> vdist = de.getSqlVarDist();
		List<Object> plst=new ArrayList<>();
		Iterator<Object> iter = Arrays.asList(params).iterator();
		List<Integer> prunedLst=new ArrayList<>();
		for (int i = 0; i < vdist.size(); i++) {
			Integer vlen = vdist.get(i);
			boolean nullExist=false;
			ArrayList<Object> tmp=new ArrayList<>();
			for (int j = 0; j < vlen; j++) {
				Object val = iter.next();
				tmp.add(val);
				if(val==null)
					nullExist=true;
			}
			if(nullExist){
				prunedLst.add(i);
			}else{
				plst.addAll(tmp);
			}
		}

		DataEntry nde=de.clone();
		String newsql = removeSQLParameters(nde.getStmt(), prunedLst.toArray(new Integer[0]));
		nde.setSql(newsql);
		Iterator<SQLVarInfo> iter2 = nde.getKeyProperties().iterator();
		for (int i = 0; i < vdist.size(); i++) {
			Integer vlen = vdist.get(i);
			boolean delete = prunedLst.contains(i);
			for (int j = 0; j < vlen; j++) {
				Object val = iter2.next();
				if(delete)
					iter2.remove();
			}
		}
		List<Integer> varDist = getConditionVarDistribution(nde.getStmt());
		nde.setSqlVarDist(varDist);
		return new PrunedResult(nde,plst.toArray());
	}
}
