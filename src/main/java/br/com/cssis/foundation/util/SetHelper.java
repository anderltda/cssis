package br.com.cssis.foundation.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.cssis.foundation.BasicObject;

@SuppressWarnings("unchecked")
public class SetHelper extends BasicObject {

	private static final long serialVersionUID = 1L;

	public static boolean isEmpty(Collection set) {
		if (set == null)
			return true;
		else
			return set.size() == 0;
	}

	public static boolean nonEmpty(Collection set) {
		return !isEmpty(set);
	}

	public static boolean isEmpty(Map map) {
		if (map == null)
			return true;
		else
			return map.isEmpty();
	}

	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}

	public static <T> boolean nonEmpty(T[] array) {
		return !isEmpty(array);
	}

	public static boolean nonEmpty(Map map) {
		return !isEmpty(map);
	}

	public static int getSize(Collection col) {
		if (col == null)
			return 0;
		else
			return col.size();
	}

	public static boolean matchAny(boolean[] booleanValues, boolean match) {
		for (int i = 0; i < booleanValues.length; i++) {
			if (booleanValues[i] == match)
				return true;
		}
		return false;
	}

	public static <T> java.util.Set<T> getNonNullSetFromList(Collection<T> source) {
		Set<T> set = new HashSet<T>();
		if (source == null)
			return set;
		for (T t : source) {
			set.add(t);
		}
		return set;
	}

	public static final boolean isEmpty(Object object) {
		if (object == null)
			return true;
		if (object instanceof Map) {
			Map map = (Map) object;
			return map.isEmpty();
		} else if (object instanceof List) {
			return ((List) object).isEmpty();
		} else if (object instanceof Object[]) {
			return ((Object[]) object).length == 0;
		} else if (object instanceof Iterator) {
			Iterator it = (Iterator) object;
			return !it.hasNext();
		} else if (object instanceof Collection) {
			return ((Collection) object).isEmpty();
		} else if (object instanceof Enumeration) {
			Enumeration it = (Enumeration) object;
			return !it.hasMoreElements();
		} else if (object.getClass().isArray()) {
			return Array.getLength(object) > 0;
		} else {
			throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
		}
	}

	public static final boolean nonEmpty(Object object) {
		return !isEmpty(object);
	}

	public static final String setAsString(Collection<?> source) {
		String msg = "";
		for (Object object : source) {
			msg += object + "\n";
		}
		return msg;
	}

}
