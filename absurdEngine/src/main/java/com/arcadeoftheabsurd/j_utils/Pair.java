/*
 * AbsurdEngine (https://bitbucket.org/smpsnr/absurdengine/) 
 * (c) by Sam Posner (http://www.arcadeoftheabsurd.com/)
 *
 * AbsurdEngine is licensed under a
 * Creative Commons Attribution 4.0 International License
 *
 * You should have received a copy of the license along with this
 * work. If not, see http://creativecommons.org/licenses/by/4.0/ 
 */

package com.arcadeoftheabsurd.j_utils;

/**
 * Generic 2-tuple
 * @author sam
 */

public class Pair<A, B> 
{
	public final A a;
	public final B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair)) {
			return false;
		}
		Pair test = (Pair) o;

		return (a.equals(test.a)) && (b.equals(test.b));
	}
	
	@Override
	public int hashCode() {
		return a.hashCode() ^ b.hashCode();
	}
}
