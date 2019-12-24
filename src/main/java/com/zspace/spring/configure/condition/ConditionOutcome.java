package com.zspace.spring.configure.condition;

/**
 * 对条件的匹配结果进行记录
 * @author liuwenqing02
 *
 */
public class ConditionOutcome {

	private final boolean match;

	public ConditionOutcome(boolean match, String message) {
		this(match);
	}

	public ConditionOutcome(boolean match) {
		this.match = match;
	}

	public static ConditionOutcome match() {
		return new ConditionOutcome(true);
	}

	public static ConditionOutcome noMatch() {
		return new ConditionOutcome(false);
	}

	public boolean isMatch() {
		return this.match;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() == obj.getClass()) {
			ConditionOutcome other = (ConditionOutcome) obj;
			return (this.match == other.match);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Boolean.hashCode(this.match) * 31 ;
	}

    	public static ConditionOutcome inverse(ConditionOutcome outcome) {
		return new ConditionOutcome(!outcome.isMatch());
	}

}
