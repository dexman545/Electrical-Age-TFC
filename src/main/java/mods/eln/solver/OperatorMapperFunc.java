package mods.eln.solver;

import java.util.List;

public class OperatorMapperFunc implements IOperatorMapper {
    
	private Class operator;
	private String key;
	private int argCount;
    
	public OperatorMapperFunc(String key, int argCount, Class operator) {
		this.operator = operator;
		this.key = key;
		this.argCount = argCount;
	}
	
	@Override
	public IOperator newOperator(String key, int depthDelta, java.util.List<Object> arg, int argOffset){
		if (depthDelta != -1) return null;
		if (!this.key.equals(key)) return null;
		if (!isFuncReady(arg, argOffset)) return null;
	
		IOperator o;
        
		try {
			o = (IOperator) operator.newInstance();
			IValue[] operatorArg = new IValue[argCount];
			for (int i = 0; i < argCount; i++) {
				operatorArg[i] = (IValue) arg.get(argOffset + 2 * (i + 1));
			}
			o.setOperator(operatorArg);
			arg.set(argOffset, o);
			removeFunc(arg, argOffset);
			return o;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private boolean isFuncReady(List<Object> list, int argOffset) {
		int counter = 0;
		
		argOffset++;
        
		for (int end = argOffset + 2 + argCount * 2 - 1; argOffset < end; argOffset++) {
			if (argOffset >= list.size()) return false;
			Object o = list.get(argOffset);
			String str = null;
			if (o instanceof String) str = (String) o;
			if (counter == 0) {
				if (str == null || !str.equals("(")) return false;
			} else if (argOffset == end-1) {
				if (str == null || !str.equals(")")) return false;
			} else if ((counter % 2) == 1) {
				if (!(o instanceof IValue)) return false;
			} else {
				if(str == null || !str.equals(",")) return false;
			}
			counter++;
		}
		return true;
	}
    
	private void removeFunc(List<Object> list, int offset) {
		for(int idx = 0; idx< 2 + argCount * 2 - 1; idx++){
			list.remove(offset + 1);
		}
	}
}
