/*
 * This file is part of pattern recognition network (prNet), a program to find patterns in data-structures
 * Copyright (C) 2022  Elija Giesbrecht
 * Published under GPLv3-or-later license
 */
package prNet;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * An interface used to compare 2 elements.<p>
 * This interface basically works like a {@link BiPredicate} but it is Serializable.
 * 
 * @author Elija Giesbrecht
 *
 * @param <T1> The first element-type
 * @param <T2> The second element-type
 */
public interface Comparison<T1, T2> extends Serializable {
	
	/**
	 * Compares the given elements.
	 * 
	 * @param t1 The first element
	 * @param t2 The second element
	 * @return If the elements are equal or not
	 */
	public boolean compare(T1 t1, T2 t2);
	
	/**
	 * Negates the interface.
	 * 
	 * @return The opposite result
	 */
	default Comparison<T1, T2> negate() {
		return (t1, t2)->!compare(t1, t2);
	}
	
	/**
	 * Logical and.
	 * 
	 * @param other The second comparison
	 * @return A comparison that represents a logical and of this and the given comparison
	 */
	default Comparison<T1, T2> and(Comparison<T1, T2> other) {
        Objects.requireNonNull(other);
        return (T1 t, T2 u)->compare(t, u) && other.compare(t, u);
    }
}
