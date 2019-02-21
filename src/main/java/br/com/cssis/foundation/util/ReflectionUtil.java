package br.com.cssis.foundation.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.proxy.HibernateProxy;

import br.com.cssis.foundation.BasicEntityObject;
import br.com.cssis.foundation.CompositeId;
import br.com.cssis.foundation.query.impl.FilterCondition;
import br.com.cssis.foundation.query.impl.Operator;

@SuppressWarnings("unchecked")
public class ReflectionUtil {

	public static Class getClassByParameterizedType(Class clazz, int index) {
		if (clazz == null) {
			return null;
		}
		Type gsc = clazz.getGenericSuperclass();
		if (gsc instanceof ParameterizedType) {
			Object tmp = ((ParameterizedType) gsc).getActualTypeArguments()[index];
			return tmp instanceof Class ? (Class) tmp : null;
		}
		return getClassByParameterizedType(clazz.getSuperclass(), index);
	}

	public static <T> T newInstance(Class<T> clazz, Object... params) {
		if (clazz != null) {
			ArrayList<Class> classes = new ArrayList<Class>();
			for (int i = 0; i < params.length; i++) {
				if (params[i] != null && !params[i].toString().equals("")) {
					classes.add(params[i].getClass());
				}
			}
			Constructor<T> cons = null;
			try {
				if (classes.size() > 0) {
					Class[] classArray = new Class[classes.size()];
					cons = clazz.getDeclaredConstructor(classes.toArray(classArray));
					return cons != null ? cons.newInstance(params) : null;
				} else {
					return clazz.newInstance();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static <T> Method getGetterMethod(Class<T> clazz, String fieldName) {
		return getMethod(clazz, "get", fieldName, new Class[] {});
	}

	public static Object executeGetterMethod(Object object, String fieldName) {
		return executeMethod("get", object, fieldName, new Class[] {}, new Object[] {});
	}

	public static <T> Method getSetterMethod(Class<T> clazz, String fieldName, Class... params) {
		return getMethod(clazz, "set", fieldName, params);
	}

	public static <T> Object executeSetterMethod(Object object, String fieldName, T value) {
		Class<T> valueClass = getFieldType(object, fieldName);
		if (valueClass != null && List.class.isAssignableFrom(valueClass)) {
			valueClass = (Class<T>) List.class;
		}

		return executeMethod("set", object, fieldName, new Class[] { valueClass }, new Object[] { value });
	}

	public static Object executeStringSetterMethod(Object object, String fieldName, String value) {
		Class valueClass = getFieldType(object, fieldName);
		if (valueClass != null && List.class.isAssignableFrom(valueClass)) {
			valueClass = List.class;
		}
		Object v = newInstance(valueClass, value);
		return executeMethod("set", object, fieldName, new Class[] { valueClass }, new Object[] { v });
	}

	public static Object executeMethod(String prefix, Object object, String fieldName, Class[] valueClass, Object[] value) {
		if (object == null) {
			return null;
		}

		Object ret = null;

		object = getLastObject(object, fieldName);
		if (object != null) {
			Class objectClass = (Class) getConcreteClass(object);

			if (fieldName.indexOf('.') > 0) {
				fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
			}

			Method method = getMethod(objectClass, prefix, fieldName, valueClass);
			if (method != null) {
				try {
					ret = method.invoke(object, value);
				} catch (Exception e) {
					System.out.println("Erro");
				}
			}
		}

		return ret;
	}

	public static Object executeMethod(Object object, String methodName, Class[] valueClass, Object[] value) {
		return executeMethod(null, object, methodName, valueClass, value);
	}
	
	public static <T> Method getMethod(Class<T> clazz, String prefix, String fieldName, Class... params) {
		String methodName = prefix != null ? prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1) : fieldName;
		Method m = null;
		try {
			m = clazz.getDeclaredMethod(methodName, params);
		} catch (Exception e) {
			try {
				m = clazz.getMethod(methodName, params);
			} catch (Exception e1) {
				Method[] ms = clazz.getMethods();
				for (Method mtmp : ms) {
					if (mtmp.getName().equals(methodName)) {
						m = mtmp;
						break;
					}
				}
			}
		}
		return m;
	}

	public static <T> Method getMethod(Class<T> clazz, String methodName, Class... params) {
		return getMethod(clazz, null, methodName, params);
	}
	
	public static Class getFieldType(Object object, String fieldName) {
		object = getLastObject(object, fieldName);
		int idx = fieldName.lastIndexOf('.');
		if (idx > 0) {
			fieldName = fieldName.substring(idx + 1);
		}
		return getFieldType(getConcreteClass(object), fieldName);
	}

	public static Class getFieldType(Class objectClass, String fieldName) {
		Class ret = null;
		try {
			Field f = objectClass.getDeclaredField(fieldName);
			ret = (Class) f.getType();
		} catch (Exception e) {
			if (objectClass != null && !objectClass.isInterface() && !Object.class.equals(objectClass)) {
				return getFieldType(objectClass.getSuperclass(), fieldName);
			}
		}

		return ret;
	}

	public static Class getGenericCollectionType(Field field) {
		return (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
	}

	public static boolean isId(Object object, String fieldName) {
		object = getLastObject(object, fieldName);
		int idx = fieldName.lastIndexOf('.');
		if (idx > 0) {
			fieldName = fieldName.substring(idx + 1);
		}
		Id a = (Id) getAnnotation(Id.class, getConcreteClass(object), getField(object, fieldName));
		if (a != null) {
			return true;
		}
		return false;
	}

	public static boolean isId(Class<?> clazz, Field field) {
		Id a = (Id) getAnnotation(Id.class, clazz, field);
		if (a != null) {
			return true;
		}
		return false;
	}

	public static ArrayList<Class> loadClasses(String matchPack) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> urls = null;
		Enumeration<URL> urls2 = null;
		try {
			urls = cl.getResources("META-INF/ou-persistence.xml");
			urls2 = cl.getResources("META-INF/persistence.xml");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			classes.addAll(loadClasses(url, matchPack));
		}
		while (urls2.hasMoreElements()) {
			URL url = urls2.nextElement();
			classes.addAll(loadClasses(url, matchPack));
		}
		return classes;
	}

	public static ArrayList<Class<?>> loadClasses(URL url, String matchPack) {
		ArrayList<String> listaClass = new ArrayList<String>();
		String path = url.getPath();
		if (path.indexOf(".jar") > 0) {
			try {
				populateClassFiles(new JarFile(path.substring(6, path.indexOf("!"))), listaClass);
			} catch (IOException e) {
				try {
					populateClassFiles(new JarFile(path.substring(5, path.indexOf("!"))), listaClass);
				} catch (IOException e1) {
				}
			}
		} else {
			if (path.indexOf("META-INF/persistence.xml") >= 0) {
				path = path.substring(0, path.indexOf("META-INF/persistence.xml"));
			}
			populateClassFiles(new File(path), listaClass);
		}

		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		for (String className : listaClass) {
			String name = pathToPackage(className);
			if (matchPack == null || name.matches(translatePackageER(matchPack))) {
				Class clazz = null;
				try {
					clazz = Class.forName(name);
				} catch (ClassNotFoundException e) {
				}
				if (clazz != null) {
					classes.add(clazz);
				}
			}
		}

		return classes;
	}

	public static Field getField(Object object, String fieldName) {
		object = getLastObject(object, fieldName);
		int idx = fieldName.lastIndexOf('.');
		if (idx > 0) {
			fieldName = fieldName.substring(idx + 1);
		}
		return getField(getConcreteClass(object), fieldName);
	}

	public static Field getField(Class objectCalss, String fieldName) {
		Field f = null;
		try {
			int strIdx = fieldName.indexOf("Str");
			fieldName = strIdx > 0 ? fieldName.substring(0, strIdx) : fieldName;
			f = objectCalss.getDeclaredField(fieldName);
		} catch (Exception e) {
			if (objectCalss != null && !objectCalss.getName().equals("br.com.tempopar.foundation.web.actions.BaseAction")) {
				return getField(objectCalss.getSuperclass(), fieldName);
			}
		}

		return f;
	}

	private static void populateClassFiles(File dir, ArrayList<String> lista) {
		if (dir.isDirectory()) {
			File[] arqs = dir.listFiles();
			for (File arq : arqs) {
				populateClassFiles(arq, lista);
			}
		} else {
			String name = dir.getAbsolutePath();
			name = name.substring(name.indexOf("classes" + File.separatorChar) + 8).replaceAll("\\" + File.separatorChar, ".");
			if (name.endsWith(".class")) {
				lista.add(name);
			}
		}
	}

	private static void populateClassFiles(JarFile jar, ArrayList<String> lista) {
		Enumeration<JarEntry> je = jar.entries();
		while (je.hasMoreElements()) {
			String name = je.nextElement().getName();
			if (name.endsWith(".class")) {
				lista.add(name);
			}
		}
	}

	private static String pathToPackage(String p) {
		int classIdx = p.indexOf(".class");
		if (classIdx >= 0) {
			p = p.substring(0, classIdx);
		}
		return p.replaceAll("\\/", ".");
	}

	public static String translatePackageER(String pack) {
		return pack.replaceAll("\\.", "\\\\\\.").replaceAll("\\*", ".*?");
	}

	public static final String getBasicFieldsAsString(Object o) {
		StringBuilder returnValue = new StringBuilder();
		Field instanceList[] = getConcreteClass(o).getDeclaredFields();
		ArrayList<Field> fieldList = new ArrayList<Field>();
		Class parentClass = getConcreteClass(o).getSuperclass();
		while (true) {
			Field parentFieldList[] = parentClass.getDeclaredFields();
			if (parentFieldList.length > 0) {
				CollectionUtils.addAll(fieldList, parentFieldList);
				parentClass = parentClass.getSuperclass();
			} else {
				break;
			}
		}

		for (int i = 0; i < instanceList.length; i++) {
			Field fld = instanceList[i];
			if (!fld.getName().equals("serialVersionUID") && isPrintableType(fld.getType())) {
				String tmp;
				try {
					tmp = BeanUtils.getProperty(o, fld.getName());
					returnValue.append("[").append(fld.getName()).append("=").append(tmp).append("]");
				} catch (Exception e) {
				}
			}
		}

		for (Field fld : fieldList) {
			if (!fld.getName().equals("serialVersionUID") && isPrintableType(fld.getType())) {
				String tmp;
				try {
					tmp = BeanUtils.getProperty(o, fld.getName());
					returnValue.append("[").append(fld.getName()).append("=").append(tmp).append("]");
				} catch (Exception e) {
				}
			}
		}
		return returnValue.toString();
	}

	public static Object getLastObject(Object obj, String fieldName) {
		if (fieldName.indexOf('.') > 0) {
			String fieldNameTmp = fieldName.substring(fieldName.indexOf('.') + 1);
			fieldName = fieldName.substring(0, fieldName.indexOf('.'));

			Object tmp = executeGetterMethod(obj, fieldName);
			if (tmp != null && Collection.class.isAssignableFrom(getConcreteClass(tmp))) {
				Field field = getField(obj, fieldName);
				Class clazz = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
				try {
					tmp = clazz.newInstance();
				} catch (Exception e) {
				}
			} else if (tmp == null) {
				tmp = newInstance(getFieldType(getConcreteClass(obj), fieldName));
				executeSetterMethod(obj, fieldName, tmp);
			}
			obj = tmp;
			return getLastObject(obj, fieldNameTmp);
		}

		return obj;
	}

	public static Class<?> getConcreteClass(Object obj) {
		Class objClass = null;
		if (obj != null) {
			if (obj instanceof HibernateProxy) {
				HibernateProxy proxy = (HibernateProxy) obj;
				objClass = proxy.getHibernateLazyInitializer().getPersistentClass();
			} else {
				objClass = obj.getClass();
			}
		}

		return objClass;
	}

	private static final Set<String> basicClasses;
	private static final Set<Object> basicInterfaces;
	static {
		basicClasses = new HashSet<String>();
		basicClasses.add(Integer.class.getSimpleName());
		basicClasses.add(Long.class.getSimpleName());
		basicClasses.add(String.class.getSimpleName());
		basicClasses.add(Boolean.class.getSimpleName());
		basicClasses.add(Byte.class.getSimpleName());
		basicClasses.add(Short.class.getSimpleName());
		basicClasses.add(Float.class.getSimpleName());
		basicClasses.add(Double.class.getSimpleName());
		basicClasses.add(BigDecimal.class.getSimpleName());
		basicClasses.add(BigInteger.class.getSimpleName());
		basicClasses.add(Date.class.getSimpleName());
		basicClasses.add(XMLGregorianCalendar.class.getSimpleName());
		
		basicInterfaces = new HashSet<Object>();
		/*
		 * basicInterfaces.add(Collection.class);
		 * basicInterfaces.add(List.class); basicInterfaces.add(Set.class);
		 * basicInterfaces.add(Map.class);
		 */
	}

	private static boolean isPrintableType(Class c) {
		if (c == null)
			return false;
		if (c.isPrimitive())
			return true;
		if (basicClasses.contains(c.getSimpleName()))
			return true;
		if (basicInterfaces.contains(c))
			return true;
		return false;
	}

	public static boolean isBasicClasses(Class c) {
		return basicClasses.contains(c.getSimpleName());
	}

	public static final ArrayList<String> getFieldNameList(Class clazz) {
		return getFieldNameList(clazz, null);
	}

	public static final ArrayList<String> getFieldNameList(Class clazz, Class clazzLimite) {
		if (clazzLimite == null) {
			clazzLimite = clazz.getSuperclass();
		}
		Class parentClass = clazz;
		ArrayList<String> fieldList = new ArrayList<String>();
		while (true) {
			if (!clazzLimite.equals(parentClass)) {
				Field parentFieldList[] = parentClass.getDeclaredFields();
				if (parentFieldList.length > 0) {
					for (Field f : parentFieldList) {
						fieldList.add(f.getName());
					}
				}
			} else {
				break;
			}
			parentClass = parentClass.getSuperclass();
		}
		return fieldList;
	}

	public static final ArrayList<Field> getFieldList(Class clazz, Class clazzLimite) {
		if (clazzLimite == null || !clazzLimite.isAssignableFrom(clazz)) {
			clazzLimite = clazz.getSuperclass();
		}
		Class parentClass = clazz;
		ArrayList<Field> fieldList = new ArrayList<Field>();
		while (true) {
			if (!clazzLimite.equals(parentClass)) {
				Field parentFieldList[] = parentClass.getDeclaredFields();
				if (parentFieldList.length > 0) {
					CollectionUtils.addAll(fieldList, parentFieldList);
				}
			} else {
				break;
			}
			parentClass = parentClass.getSuperclass();
		}
		return fieldList;
	}

	public static final String getFieldListAndValues(Object o) {
		StringBuilder returnValue = new StringBuilder();
		try {
			Field instanceList[] = o.getClass().getDeclaredFields();
			ArrayList<Field> fieldList = new ArrayList<Field>();
			Class parentClass = o.getClass().getSuperclass();
			while (true) {
				Field parentFieldList[] = parentClass.getDeclaredFields();
				if (parentFieldList.length > 0) {
					CollectionUtils.addAll(fieldList, parentFieldList);
					parentClass = parentClass.getSuperclass();
				} else {
					break;
				}
			}

			for (int i = 0; i < instanceList.length; i++) {
				Field fld = instanceList[i];
				if (!fld.getName().equals("serialVersionUID")) {
					returnValue.append("\t").append(fld.getName()).append("=").append(BeanUtils.getProperty(o, fld.getName())).append("\n");
				}
			}

			for (Field fld : fieldList) {
				if (!fld.getName().equals("serialVersionUID")) {
					returnValue.append("\t").append(fld.getName()).append("=").append(BeanUtils.getProperty(o, fld.getName())).append("\n");
				}
			}
		} catch (Throwable e) {
		}
		return returnValue.toString();
	}

	public static Column getColumn(Class clazz, Field field) {
		return (Column) getAnnotation(Column.class, clazz, field);
	}

	public static <T extends Annotation> Annotation getAnnotation(Class<T> annotation, Class clazz, String fieldName) {
		return getAnnotation(annotation, clazz, getField(clazz, fieldName));
	}
	
	public static <T extends Annotation> Annotation getAnnotation(Class<T> annotation, Class clazz, Field field) {
		Annotation a = field != null ? field.getAnnotation(annotation) : null;
		if (a == null) {
			try {
				Method method = ReflectionUtil.getGetterMethod(clazz, field.getName());
				if (method != null) {
					a = method.getAnnotation(annotation);
				}
			} catch (Throwable e) {
				// TODO: handle exception
			}
		}
		return a;
	}

	public static List<Column> getColumnList(Class clazz) {
		Field[] fieldList = clazz.getDeclaredFields();
		List<Column> returnValue = new ArrayList<Column>();
		for (int i = 0; i < fieldList.length; i++) {
			Column col = fieldList[i].getAnnotation(Column.class);
			if (col == null) {
				Method method = ReflectionUtil.getGetterMethod(clazz, fieldList[i].getName());
				if (method != null) {
					col = method.getAnnotation(Column.class);
				}
			}
			if (col != null)
				returnValue.add(col);
		}
		return returnValue;
	}

	public static List<FilterCondition> extractCreteriaFromObject(Object o) {
		List<FilterCondition> criteriaList = new ArrayList<FilterCondition>();
		return extractCreteriaFromObject(criteriaList, "", o);
	}

	public static List<FilterCondition> extractCreteriaFromObject(List<FilterCondition> input, String parentName, Object o) {
		Field instanceList[] = o.getClass().getDeclaredFields();
		for (Field fld : instanceList) {
			if (!fld.getName().equals("serialVersionUID")) {
				try {
					Object val = executeGetterMethod(o, fld.getName());
					if (val != null) {
						String propertyName;
						if (StringHelper.isEmpty(parentName)) {
							propertyName = fld.getName();
						} else {
							StringBuilder b = new StringBuilder();
							b.append(parentName).append(".").append(fld.getName());
							propertyName = b.toString();
						}

						if (val instanceof BasicEntityObject) {
							extractCreteriaFromObject(input, propertyName, val);
						} else {
							Operator.OP operator = null;
							if (val instanceof Collection || val instanceof Object[]) {
								operator = Operator.IN;
							} else {
								operator = Operator.EQUAL;
							}
							FilterCondition cond = new FilterCondition(operator, propertyName, val);
							input.add(cond);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return input;
	}

	public static void setCompositeIdValue(CompositeId compositeId, String idValue) {
		Class idType = compositeId.getClass();
		String[] idsComposite = idValue.toString().split("_");
		int i = 0;
		for (Field idf : idType.getDeclaredFields()) {
			if (!idf.getType().isPrimitive() && !Modifier.isFinal(idf.getModifiers())) {
				ReflectionUtil.executeSetterMethod(compositeId, idf.getName(), ReflectionUtil.newInstance(idf.getType(), idsComposite[i]));
				i++;
			}
		}
	}

	public static void setEmptyStringToNull(Object obj) {
		if (obj != null) {
			ArrayList<Field> fields = getFieldList(obj.getClass(), Object.class);
			for (Field f : fields) {
				if (String.class.isAssignableFrom(f.getType())) {
					String value = (String) executeGetterMethod(obj, f.getName());
					if (value != null && value.equals("")) {
						executeSetterMethod(obj, f.getName(), null);
					}
				}
			}
		}
	}

	public static String formatObject(Object value) {
		if (value == null || value.toString().equals("")) {
			return null;
		}

		Format fmt = getFormat(value.getClass());

		return fmt != null ? fmt.format(value) : value.toString();
	}

	public static <T> T formatObject(String value, Class<T> toClass) {
		if (value == null || value.equals("")) {
			return null;
		}

		Object obj = null;
		if (Number.class.isAssignableFrom(toClass)) {
			obj = ReflectionUtil.newInstance(toClass, value.replaceAll("\\.", "").replace(',', '.'));
		} else {
			Format fmt = getFormat(toClass);
			try {
				obj = (fmt != null ? fmt.parseObject(value) : null);
			} catch (ParseException e) {
			}
		}
		return (T) obj;

	}

	private static Format getFormat(Class valueClass) {
		Format fmt = null;
		if (Date.class.isAssignableFrom(valueClass)) {
			fmt = SimpleDateFormat.getInstance();
			((SimpleDateFormat) fmt).applyPattern("dd/MM/yyyy");
		} else if (Integer.class.isAssignableFrom(valueClass) || Long.class.isAssignableFrom(valueClass)) {
			fmt = DecimalFormat.getIntegerInstance();
			((DecimalFormat) fmt).applyPattern("#,##0");
		} else if (Float.class.isAssignableFrom(valueClass) || Double.class.isAssignableFrom(valueClass)) {
			fmt = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
			((DecimalFormat) fmt).setParseIntegerOnly(false);
			((DecimalFormat) fmt).setDecimalSeparatorAlwaysShown(true);
		}
		return fmt;
	}
	
	public static Object getInitializedObjectInstance(Class clazz) {
		try {
			Object object = clazz.newInstance();

			Field[] fiels = object.getClass().getDeclaredFields();

			for (Field field : fiels) {
				if (!field.getType().isPrimitive()
						&& !field.getType().isEnum()
						&& !field.getType().isAssignableFrom(String.class)
						&& !field.getType().isAssignableFrom(XMLGregorianCalendar.class)
						&& !field.getType().isAssignableFrom(BigInteger.class)
						&& !field.getType().isAssignableFrom(BigDecimal.class)
						&& !field.getType().isArray()) {
					Object property = null;
					if(field.getType().isAssignableFrom(List.class)){
						property = ArrayList.class.newInstance();
					}else{
						property = getInitializedObjectInstance(field.getType());
					}
					ReflectionUtil.executeSetterMethod(object, field.getName(), property);
				}
			}
			return object;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T> T newInstanceConstructor(Class<T> clazz, Object... params) {
		if (clazz != null) {
			ArrayList<Class> classes = new ArrayList<Class>();
			for (int i = 0; i < params.length; i++) {
				if (params[i] != null && !params[i].toString().equals("")) {
					classes.add(params[i].getClass());
				} else {
					classes.add(String.class);
				}
			}
			Constructor<T> cons = null;
			try {
				if (classes.size() > 0) {
					Class[] classArray = new Class[classes.size()];
					cons = clazz.getDeclaredConstructor(classes.toArray(classArray));
					return cons != null ? cons.newInstance(params) : null;
				} else {
					return clazz.newInstance();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String temporizador(long start) {
		return (((((System.currentTimeMillis() - start) / 1000) / 60) / 60)
				+ " hora(s) "
				+ ((((System.currentTimeMillis() - start) / 1000) / 60) % 60)
				+ " minuto(s) e "
				+ (((System.currentTimeMillis() - start) / 1000) % 60)
				+ " segundo(s)");
	}
	
	public static String totalizadorTemporizador(long currentTimeMillis) {
		return (((((currentTimeMillis) / 1000) / 60) / 60)
				+ " hora(s) "
				+ ((((currentTimeMillis) / 1000) / 60) % 60)
				+ " minuto(s) e "
				+ (((currentTimeMillis) / 1000) % 60)
				+ " segundo(s)");
	}
}