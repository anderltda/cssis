package br.com.cssis.webservice.result;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

public abstract class BaseDTO implements Serializable {

	private static final long serialVersionUID = -5961122593692573963L;

	public StringBuilder descreverBean(Class<?> clazz) {
		return descreverBean(null, clazz);
	}

	public StringBuilder descreverBean(String[] naoDescrever) {
		return descreverBean(naoDescrever, this.getClass());
	}

	public StringBuilder descreverBean() {
		return descreverBean(null, this.getClass());
	}

	public StringBuilder descreverBean(String[] naoDescrever, Class<?> clazz) {
		StringBuilder s = new StringBuilder();
		for (Method metodo : clazz.getMethods()) {
			if ((metodo.getName().indexOf("get") != 0 && metodo.getName().indexOf("is") != 0) || ArrayUtils.contains(naoDescrever, metodo.getName())) {
				continue;
			}
			String prop = null;
			if (metodo.getName().indexOf("get") == 0) {
				prop = WordUtils.uncapitalize(metodo.getName().replaceFirst("get", ""));
			} else {
				prop = WordUtils.uncapitalize(metodo.getName().replaceFirst("is", ""));
			}
			try {
				Object valor = metodo.invoke(this, new Object[] {});
				s.append(appendPropriedade(prop, valor));
			} catch (Exception e) {
				s.append(appendPropriedade(prop, "?"));
			}
		}
		return s;
	}

	public static StringBuilder appendPropriedade(String nome, Object valor) {
		StringBuilder s = new StringBuilder();
		s.append("- ").append(StringUtils.rightPad(nome, 30, ".")).append(":").append(valor).append(System.getProperty("line.separator"));
		return s;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(descreverBean());
		return s.toString();
	}
}
