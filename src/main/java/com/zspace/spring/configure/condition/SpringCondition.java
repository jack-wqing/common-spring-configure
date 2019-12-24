package com.zspace.spring.configure.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;

/**
 * 对 --spring condition-- 通用支持
 * @author liuwenqing02
 *
 */
public abstract class SpringCondition implements Condition {

	@Override
	public final boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String classOrMethodName = getClassOrMethodName(metadata);
		try {
			ConditionOutcome outcome = getMatchOutcome(context, metadata);
			return outcome.isMatch();
		}
		catch (NoClassDefFoundError ex) {
			throw new IllegalStateException("Could not evaluate condition on " + classOrMethodName + " due to "
					+ ex.getMessage() + " not " + "found. Make sure your own configuration does not rely on "
					+ "that class. This can also happen if you are "
					+ "@ComponentScanning a springframework package (e.g. if you "
					+ "put a @ComponentScan in the default package by mistake)", ex);
		}
		catch (RuntimeException ex) {
			throw new IllegalStateException("Error processing condition on " + getName(metadata), ex);
		}
	}

	private String getName(AnnotatedTypeMetadata metadata) {
		if (metadata instanceof AnnotationMetadata) {
			return ((AnnotationMetadata) metadata).getClassName();
		}
		if (metadata instanceof MethodMetadata) {
			MethodMetadata methodMetadata = (MethodMetadata) metadata;
			return methodMetadata.getDeclaringClassName() + "." + methodMetadata.getMethodName();
		}
		return metadata.toString();
	}

	private static String getClassOrMethodName(AnnotatedTypeMetadata metadata) {
		if (metadata instanceof ClassMetadata) {
			ClassMetadata classMetadata = (ClassMetadata) metadata;
			return classMetadata.getClassName();
		}
		MethodMetadata methodMetadata = (MethodMetadata) metadata;
		return methodMetadata.getDeclaringClassName() + "#" + methodMetadata.getMethodName();
	}

	/**
	 * 需要匹配的实现结果
	 * @param context
	 * @param metadata
	 * @return
	 */
	public abstract ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata);

	/**
	 * 执行匹配，如果指定的conditions 有匹配的
	 * @param context
	 * @param metadata
	 * @param conditions
	 * @return
	 */
	protected final boolean anyMatches(ConditionContext context, AnnotatedTypeMetadata metadata,
			Condition... conditions) {
		for (Condition condition : conditions) {
			if (matches(context, metadata, condition)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 采用适配模式来匹配 -- 指定的条件能够被满足
	 * @param context
	 * @param metadata
	 * @param condition
	 * @return
	 */
	protected final boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata, Condition condition) {
		if (condition instanceof SpringCondition) {
			return ((SpringCondition) condition).getMatchOutcome(context, metadata).isMatch();
		}
		return condition.matches(context, metadata);
	}

}
