package Conditions;

import soot.SootClass;
import soot.Unit;
import soot.Value;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.Stmt;

public class SootConditionChecker {
	public SootConditionChecker() {
	}

	public boolean CheckIfClassExists(SootClass Class, String SootValue) {
		if (Class.getName().contains(SootValue) && String.valueOf(Class).contains(".ads")) {
			return true;
		}
		return false;
	}

	public boolean LastKnownUnitIsAStatement(Unit LastKnownUnit) {
		if (LastKnownUnit instanceof Stmt) {
			return true;
		}
		return false;
	}

	public boolean ValueIsAVirtualInvokeExpr(Value SootValue) {
		if (SootValue instanceof VirtualInvokeExpr) {
			return true;
		}
		return false;
	}

	public boolean ValueIsASpecialInvokeExpr(Value SootValue) {
		if (SootValue instanceof SpecialInvokeExpr) {
			return true;
		}
		return false;
	}

	public boolean ValueToLowerContainsString(Value SootValue, String WordToCheckFor) {
		if (SootValue.toString().toLowerCase().contains(WordToCheckFor)) {
			return true;
		}
		return false;
	}

	public boolean CheckifSpecialInvokeExprMethodContains(SpecialInvokeExpr SpecialInvokeExpression, Value SootValue,
			String WordToCheckFor) {
		SpecialInvokeExpression = (SpecialInvokeExpr) SootValue;
		String VirtualInvokeExpressionMethodName = SpecialInvokeExpression.getMethod().getName().toLowerCase()
				.toString();
		if (VirtualInvokeExpressionMethodName.contains(WordToCheckFor)) {
			return true;
		}
		return false;
	}

	public boolean CheckifVirtualInvokeExprMethodContains(VirtualInvokeExpr VirtualInvokeExpression, Value SootValue,
			String WordToCheckFor) {
		VirtualInvokeExpression = (VirtualInvokeExpr) SootValue;
		String VirtualInvokeExpressionMethodName = VirtualInvokeExpression.getMethod().getName().toLowerCase()
				.toString();
		if (VirtualInvokeExpressionMethodName.contains(WordToCheckFor)) {
			return true;
		}
		return false;
	}

	public boolean CheckIfDeclaringClassHasAdLibrary(String declaringclass) {
		declaringclass = declaringclass.toLowerCase();
		if (declaringclass.contains("google") || declaringclass.contains("amazon")
				|| declaringclass.contains("facebook")) {
			return true;
		}
		return false;
	}
}
