package br.com.cssis.foundation.util;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.Constants;
import br.com.cssis.foundation.ExceptionConstants;
import br.com.cssis.foundation.ExceptionHandler;
import br.com.cssis.foundation.config.FrameworkConfiguration;
import br.com.cssis.foundation.service.ServiceException;

@SuppressWarnings("unchecked")
public class StringHelper extends BasicObject {
	private static final long serialVersionUID = 1L;

	private static String K_SPLITTER_REGEX = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_PROPERTY_NAME_SPLITTER);
	private static Pattern patSplitter = Pattern.compile(K_SPLITTER_REGEX);
	private static String K_PROPERTY_SET = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_PROPERTY_SET);
	private static String K_PROPERTY_GET = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_PROPERTY_GET);

	private static final String nonAccentTransactionDest = "aeiouAEIOUaeiouAEIOUaonAONaeiouAEIOUaeiouAEIOUcCaoo";
	private static final Map<Character, Character> accentConversionMap;
	public static final String accentTranslationSource = "·ÈÌÛ˙¡…Õ”⁄‚ÍÓÙ˚¬ Œ‘€„ıÒ√’—‡ËÏÚ˘¿»Ã“Ÿ‰ÎÔˆ¸ƒÀœ÷‹Á«™∫∞";
	public static final String lineBreak = "\r\n";

	static {
		accentConversionMap = new HashMap<Character, Character>(accentTranslationSource.length());
		for (int i = 0; i < accentTranslationSource.length(); i++) {
			accentConversionMap.put(accentTranslationSource.charAt(i), nonAccentTransactionDest.charAt(i));
		}
	}

	public static final boolean isEmpty(String value) {
		return (value == null || value.length() == 0) ? true : false;
	}

	public static final boolean nonEmpty(String value) {
		return !isEmpty(value);
	}

	public static final boolean isEmptyTrim(String value) {
		if (value == null || value.length() == 0) return true;
		return (value.trim().length() == 0);
	}

	public static final boolean nonEmptyTrim(String value) {
		return !isEmptyTrim(value);
	}

	public static final String mapAsString(Map map) {
		if (map == null) return "";
		StringBuilder builder = new StringBuilder();
		for (Object key : map.keySet()) {
			builder.append(key.toString()).append(" =>");
			builder.append(map.get(key) == null ? "NULL" : map.get(key).toString()).append("");
			builder.append("\n");
		}
		return builder.toString();
	}

	public static final String collectionAsString(Collection col) {
		if (col == null) return "";
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for (Object object : col) {
			builder.append("[").append(i+1);
			builder.append("] =>").append(object == null ? " null" : object.toString());
			builder.append("\n");
			i++;
		}
		return builder.toString();
	}

	public static void removeLast(StringBuilder builder) {
		if (builder == null || builder.length() == 0) return;
		builder.setLength(builder.length() - 1);
	}

	public static String removeLast(String str) {
		if (isEmpty(str)) return str;
		else return str.substring(0, str.length() - 1);
	}

	public static String subString(String str, int lastPosition) {
		if(str == null)
			return null;
		if (str.length() < lastPosition) return str;
		else return str.substring(0, lastPosition);
	}

	public static String getPropertyName(String name) {
		if (isEmpty(name)) return name;
		String[] items = patSplitter.split(name);
		StringBuilder returnValue = new StringBuilder();
		for (int i = 0; i < items.length; i++) {
			returnValue.append(firstUpperRestLower(items[i]));
		}
		return firstLower(returnValue.toString());
	}

	public static String firstUpperRestLower(String name) {
		if (isEmpty(name)) return name;
		String returnValue = name.substring(0, 1).toUpperCase();
		if (name.length() > 1) returnValue += name.substring(1).toLowerCase();
		return returnValue;
	}

	public static String firstUpper(String name) {
		if (isEmpty(name)) return name;
		String returnValue = name.substring(0, 1).toUpperCase();
		if (name.length() > 1) returnValue += name.substring(1);
		return returnValue;
	}

	public static String firstLower(String name) {
		if (isEmpty(name)) return name;
		String returnValue = name.substring(0, 1).toLowerCase();
		if (name.length() > 1) returnValue += name.substring(1);
		return returnValue;
	}

	public static String getSetterName(String name) {
		return K_PROPERTY_SET + firstUpper(getPropertyName(name));
	}

	public static String getGetterName(String name) {
		return K_PROPERTY_GET + firstUpper(getPropertyName(name));
	}

	public static String getNonAccentString(String source) {
		if (isEmpty(source)) return source;
		else {
			char[] modified = source.toUpperCase().toCharArray();
			for (int i = 0; i < modified.length; i++) {
				Character replacementCh = accentConversionMap.get(modified[i]);
				if (replacementCh != null) {
					modified[i] = replacementCh.charValue();
				}
			}
			return String.valueOf(modified);

		}
	}
	
	public static String getNonAccentStringCaseSensitive(String source) {
		if (isEmpty(source)) return source;
		else {
			char[] modified = source.toCharArray();
			for (int i = 0; i < modified.length; i++) {
				Character replacementCh = accentConversionMap.get(modified[i]);
				if (replacementCh != null) {
					modified[i] = replacementCh.charValue();
				}
			}
			return String.valueOf(modified);

		}
	}

	public static String lpad(String str, int size, char c) {
		int currentSize = str == null ? 0 : str.length();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size - currentSize; i++) {
			builder.append(c);
		}
		builder.append(str);
		return builder.substring(0, size).toString();
	}

	public static String rpad(String str, int size, char c) {
		int currentSize = str == null ? 0 : str.length();
		StringBuilder builder = new StringBuilder(str);
		for (int i = 0; i < size - currentSize; i++) {
			builder.append(c);
		}
		return builder.substring(0, size).toString();
	}

	public static char lastChar(String value) {
		if (isEmpty(value)) return '\0';
		else return value.charAt(value.length() - 1);
	}

	public static String removeSpecialChar(String value) {
		if (nonEmpty(value)) return value.replaceAll("[^a-z^A-Z^0-9]", "");
		else return value;
	}
	
	public static String replaceSpecialChar(String value, Character caracter) {
		if (nonEmpty(value)) return value.replaceAll("[^a-z^A-Z^0-9]", caracter.toString());
		else return value;
	}
	
	public static String removeCharacter(String value) {	
		if(nonEmpty(value)) {
			String regex = "[-)\\\\/,?.!%$:#(+=^*]*";
			return value.replaceAll(regex, "");
		}
		return value;
	}
	
	public static String removeCharacters(String value) {	
		if(nonEmpty(value)) {
			String regex = "[-)\\\\/,?.!%$®@&>:#(_+=^*]*";
			return getNonAccentStringCaseSensitive(fullBlankTrim(toUpperCase(value.replaceAll(regex, "").replace("*", ""))));
		}
		return value;
	}
	
	public static String getFirstName(String value) {	
		if(nonEmpty(value)) {
			return removeCharacters(value.substring(0, value.indexOf(" ")));			
		}
		return value;
	}	
	
	public static String removeExtensao(String value) {	
		if(nonEmpty(value)) {
			value = value.substring(0, value.indexOf("."));
		}
		return value;
	}
	

	public static boolean containsOnlyDigit(String value) {
		if (nonEmpty(value)) return value.matches("[\\d]+");
		else return false;
	}

	public static boolean containsOnlyAlphaNumeric(String value) {
		if (nonEmpty(value)) return value.matches("[\\d|\\w]+");
		else return false;
	}

	public static String removeWildCards(String value) {
		if (nonEmpty(value)) return value.replaceAll("[*%]", "");
		else return value;
	}

	public static String removeControlChar(String value) {
		if (nonEmpty(value)) return value.replaceAll("[\\n|\\r]", "").replaceAll("'", "\\\\'");
		else return value;
	}

	public static String replaceControlCharToSpace(String value) {
		if (nonEmpty(value)) return fullTrim(value.replaceAll("[\\n|\\r]", " ").replaceAll("'", "\\\\'").replaceAll("\\s+"," "));
		else return value;
	}

	public static String getSearchString(String value) {
		if (nonEmpty(value)) return getNonAccentString(value.toUpperCase());
		else return value;

	}

	public final static String getWildCard() {
		return "%";
	}

	public static final String formataCnpjCpf(String src) {
		if (src == null) return null;

		String s = removeNonNumbers(src);

		if (s.length() == 11) {
			String formated = s.substring(0, 3) + "." + s.substring(3, 6) + "." + s.substring(6, 9) + "-" + s.substring(9);
			int nbr = formated.indexOf('#');
			if (nbr >= 0) {
				formated = formated.substring(0, nbr);
			}
			return formated;
		} else if (s.length() == 14) {
			String formated = s.substring(0, 2) + "." + s.substring(2, 5) + "." + s.substring(5, 8) + "/" + s.substring(8, 12) + "-" + s.substring(12);
			int nbr = formated.indexOf('#');
			if (nbr >= 0) {
				formated = formated.substring(0, nbr);
			}
			return formated;
		} else if (s.length() < 11) {
			while (s.length() != 11) {
				s = "0" + s;
			}
			return formataCnpjCpf(s);

		} else if (s.length() < 14) {
			while (s.length() != 14) {
				s = "0" + s;
			}
			return formataCnpjCpf(s);
		}
		return null;
	}
	public static final String formatarRg(String src) {
		if (src == null) return null;
		
		String s = removeNonNumbers(src);
		
		if (s.length() == 9) {
			String formated = s.substring(0, 2) + "." + s.substring(2, 5) + "." + s.substring(5, 8) + "-" + s.substring(8);
			int nbr = formated.indexOf('#');
			if (nbr >= 0) {
				formated = formated.substring(0, nbr);
			}
			return formated;
		} else if (s.length() < 9) {
			while (s.length() != 9) {
				s = "0" + s;
			}
			return formatarRg(s);
			
		}
		return null;
	}

	public static final String formataCnpjCei(String s) {
		if (s == null) return null;

		if (s.length() == 12) {
			String formated = s.substring(0, 2) + "." + s.substring(2, 5) + "." + s.substring(5, 10) + "/" + s.substring(10);
			int nbr = formated.indexOf('#');
			if (nbr >= 0) {
				formated = formated.substring(0, nbr);
			}
			return formated;

		} else return formataCnpjCpf(s);
	}

	public static List<String> getTokens(String input, String mask) {
		Pattern pattern = Pattern.compile(mask);
		Matcher matcher = pattern.matcher(input);
		List<String> returnValue = new ArrayList<String>();
		if (matcher.groupCount() > 0) {
			while (matcher.find()) {
				returnValue.add(matcher.group().replaceAll("\"", ""));
			}
		}
		return returnValue;
	}

	public static String getHumanNameFromJavaName(String javaName) {
		if (isEmpty(javaName)) return javaName;
		String mask = "([A-Z]+)";
		Pattern pattern = Pattern.compile(mask);
		Matcher matcher = pattern.matcher(javaName);
		StringBuilder builder = new StringBuilder();
		int start = 0;
		if (matcher.groupCount() > 0) {
			while (matcher.find()) {
				if (start != matcher.start()) {
					builder.append(StringHelper.firstUpper(javaName.substring(start, matcher.start())));
					start = matcher.start();
					builder.append(" ");
				}
			}
			builder.append(StringHelper.firstUpper(javaName.substring(start)));
		}
		return builder.toString();
	}

	public static String getHumanNameFromDBName(String name) {
		if (isEmpty(name)) return name;
		String[] items = patSplitter.split(name);
		StringBuilder returnValue = new StringBuilder();
		for (int i = 0; i < items.length; i++) {
			returnValue.append(firstUpperRestLower(items[i])).append(" ");
		}
		removeLast(returnValue);
		return returnValue.toString();
	}

	public static final String toUpperCase(String source) {
		if (nonEmpty(source)) return source.toUpperCase();
		else return source;
	}

	public static final String toLowerCase(String source) {
		if (nonEmpty(source)) return source.toLowerCase();
		else return source;
	}


	public static String formatName(String name) {
		StringBuilder newName = new StringBuilder();
		Matcher m = Pattern.compile("((\\w)(\\w*))").matcher(name);
		while (m.find()) {
			String tempName = (m.group(2).toUpperCase() + m.group(3).toLowerCase());
			Matcher toLowerCase = Pattern.compile("((^D(as|os|a|e)$)|(^(E|O)$))").matcher(tempName);
			Matcher toUpperCase = Pattern.compile("(^([IiVvXxCcDdMm]+)$)").matcher(tempName);
			if (toLowerCase.find()) {
				tempName = toLowerCase.group(1).toLowerCase();
			} else if (toUpperCase.find()) {
				tempName = toUpperCase.group(1).toUpperCase();
			}

			newName.append(tempName);
			if (!m.hitEnd()) {
				newName.append(" ");
			}
		}

		return newName.toString();
	}

	public static String removeNonNumbers(String value) {
		return value != null ? value.replaceAll("\\D", "") : null;
	}

	public static String getSizedString(String value, int size) {
		if (size < 0) return value;
		if (value == null) return null;
		if (size == 0) return "";
		if (value.length() > size) return value.substring(0, size);
		else return value;
	}

	public static boolean isDifferent(String source, String target) {
		if (source == null) {
			return target == null;
		} else {
			if (target == null) return false;
			return !source.equals(target);
		}
	}

	public static boolean isEqual(String source, String target) {
		return !isDifferent(source, target);
	}

	public static String revert(String source) {
		if (isEmptyTrim(source)) return source;

		int tamanho = source.length();
		char[] chSource = source.toCharArray();
		char[] tmpChar = new char[source.length()];
		for (int i = 0; i < tamanho; i++) {
			tmpChar[i] = chSource[tamanho - 1 - i];
		}
		return new String(tmpChar);
	}

	public static final String[] splitTrim(String source) {
		if (isEmptyTrim(source)) return new String[0];
		String[] tokens = source.split("\\s{1,}");
		return tokens;
	}

	public static final String formatAsString(Object value) {
		if(value==null)
			return "";
		
		Format fmt = null;
		Class valueClass = value.getClass();

		if (Date.class.isAssignableFrom(valueClass)) {
			if (value != null && !value.toString().equals("")) {
				fmt = SimpleDateFormat.getInstance();
				((SimpleDateFormat) fmt).applyPattern("dd/MM/yyyy");
			}
		} else if (Integer.class.isAssignableFrom(valueClass) || Long.class.isAssignableFrom(valueClass)) {
			if (value != null && !value.toString().equals("")) {
				fmt = DecimalFormat.getIntegerInstance();
				((DecimalFormat) fmt).applyPattern("#,###,##0");
			}
		} else if (Float.class.isAssignableFrom(valueClass) || Double.class.isAssignableFrom(valueClass)) {
			if (value != null && !value.toString().equals("")) {
				fmt = DecimalFormat.getInstance();
				((DecimalFormat) fmt).applyPattern("#,###,##0.00");
			}

		}else if (BigDecimal.class.isAssignableFrom(valueClass)) {
			if (value != null && !value.toString().equals("")) {
				fmt = DecimalFormat.getInstance();
				((DecimalFormat) fmt).applyPattern("#,###,##0.000000");
			}

		}

		if (fmt != null) {
			value = fmt.format(value);
		}

		return value.toString();
	}

	public static final String fullTrim(String source) {
		if (source == null || source.length() == 0) return source;
		else return revert(revert(source.trim()).trim());
	}
	
	public static final String fullBlankTrim(String source) {
		if (source == null || source.length() == 0) return "";
		else return revert(revert(source.trim()).trim());
	}

	public static boolean validEmail(String email) {
		if (email == null) {
			return true;
		}
		return email.matches("\\b(^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-])+((\\.com)|(\\.pr)|(\\.net)|(\\.org)|(\\.info)|(\\.edu)|(\\.mil)|(\\.gov)|(\\.biz)|(\\.ws)|(\\.us)|(\\.tv)|(\\.cc)|(\\.aero)|(\\.arpa)|(\\.coop)|(\\.int)|(\\.jobs)|(\\.museum)|(\\.name)|(\\.pro)|(\\.travel)|(\\.nato)|(\\.sebrae.com.br)|(\\..{2,3})|(\\..{2,3}\\..{2,3})|(\\..{2,3}\\..{2,3}\\..{2,3})|(\\..{2,20}\\..{2,3}))$)\\b");
	}

	public static final int length(String str) {
		if (str != null) return str.length();
		else return 0;
	}
	
	public static final String removeHtmlTags(String source) {
		if (StringHelper.isEmptyTrim(source)) return "";
		String returnValue = "";
	    String scriptregex = "<(script|style)[^>]*>[^<]*</(script|style)>";
	    Pattern p1 = Pattern.compile(scriptregex,Pattern.CASE_INSENSITIVE);
	    Matcher m1 = p1.matcher(source);
	    int count = 0;
	    while (m1.find()) {
	      count++;
	    }
	    returnValue = m1.replaceAll("");

	    String tagregex = "<[^>]*>";
	    Pattern p2 = Pattern.compile(tagregex);
	    Matcher m2 = p2.matcher(returnValue);
	    count = 0;
	    while (m2.find()) {
	      count++;
	    }
	    // Replace any matches with nothing
	    returnValue = m2.replaceAll("");

	    String multiplenewlines = "(\\n{1,2})(\\s*\\n)+"; 
	    return returnValue.replaceAll(multiplenewlines,"$1");
	}

	public static String[] splitTextBySize(String text, int size) {
		if (text.length() > size) {
			List<String> textInLines = new ArrayList<String>();
			while (nonEmpty(text)) {
				int index = (text.length() < size ? text.length() : size);
				textInLines.add(text.substring(0, index));
				text = text.substring(index);
			}
			return textInLines.toArray(new String[textInLines.size()]);
		}
		return new String[] {text};
	}
	
	/**
	 * Converte uma String em um Map
	 * String Ex. "idTipoGrupoCobranca=1;estipulante.subestipulante.id=1234;dataCompetencia=19/08/2010;dataFechamento=19/08/2010"
	 * @param mapAsString
	 * @return
	 */
	public static Map<String, String> parseStringToMap(String mapAsString) {
		//Pattern keyValueFinder = Pattern.compile("[\\p{L}[.]]*=[[:][0-9[/]][\\p{L}]]*");
		Pattern keyValueFinder = Pattern.compile("[[:][0-9[/]][\\p{L}]]*=[[:][0-9[/]][\\p{L}]]*");
		Matcher keyValueMatcher = keyValueFinder.matcher(mapAsString);		
		
		Map<String, String> params = new HashMap<String, String>();

		while (keyValueMatcher.find()) {
			String mapValue = keyValueMatcher.group();

			StringTokenizer mapString = new StringTokenizer(mapValue, "=");
			params.put(mapString.nextElement().toString(), mapString.nextElement().toString());
		}

		return params;
	}
	
	public static String getValueAsString(Object value) throws ServiceException {
		String valueAsString = "";
		
		try {
			
			Class valueClazz = value.getClass();
			
			if(String.class.isAssignableFrom(valueClazz)){
				valueAsString = value.toString();
			}else if(Long.class.isAssignableFrom(valueClazz)){
				valueAsString = value.toString();
			}else if(Date.class.isAssignableFrom(valueClazz)){
				valueAsString = DateTimeHelper.formatDate((Date)value);
			}else if(ArrayList.class.isAssignableFrom(valueClazz)){
				List valuesList = (List)value;
				for(int i = 0 ; i < valuesList.size() ; i++){
					if(i == 1)
						valueAsString += ":";

					valueAsString += getValueAsString(valuesList.get(i));
					
				}
			}			
		} catch (ParseException e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, e);
		}
		
		return valueAsString;
	}
	
	public static String encodeHTML(String s)
	{
	    StringBuffer out = new StringBuffer();
	    for(int i=0; i<s.length(); i++)
	    {
	        char c = s.charAt(i);
	        if(c > 127 || c=='"' || c=='<' || c=='>')
	        {
	           out.append("&#"+(int)c+";");
	        }
	        else
	        {
	            out.append(c);
	        }
	    }
	    return out.toString();
	}
	
	public static String getErrorMensage(String mensagem) {
		if (mensagem == null) {
			return null;
		}
		
		String result = null;

		if (mensagem.indexOf(Constants.K_FORMAT_ERROR_LI_START) >= 0 && mensagem.indexOf(Constants.K_FORMAT_ERROR_LI_END) >= 0) {
			result = mensagem.substring(mensagem.indexOf(Constants.K_FORMAT_ERROR_LI_START) + Constants.K_FORMAT_ERROR_LI_START.length(), mensagem.indexOf(Constants.K_FORMAT_ERROR_LI_END)).toString();
		} else {
			result = mensagem;
		}

		return result;
	}
	
	public static String addCharacter(String string, Character character, Integer stringSize, Character fillWith, Integer ...index){
		StringBuilder newString = new StringBuilder();
		
		if(string != null){
			char[] stringChar;
			if(stringSize != null && fillWith != null){
				stringChar = StringHelper.lpad(string, stringSize, fillWith).toCharArray();
			}else{
				stringChar = string.toCharArray();
			}
			
			for (int i = 0; i < stringChar.length ; i++) {
				if(Arrays.asList(index).contains(i+1)){
					newString.append(character);
				}
				newString.append(stringChar[i]);
			}
		}
		
		return newString.toString();
	}

	/**
	 * Formata Cep.<br>
	 * Se cep for null, retorna null.<br>
	 * Se cep tiver tamanho menor que 7, retorna o original<br>
	 * @param cep
	 * @return
	 */
	public static String formatarCep(String cep) {
		if (cep == null) {
			return null;
		}
		if (cep.length() < 7) {
			return cep;
		}
		cep = removeNonNumbers(cep);
		return cep.replaceFirst("(\\d{4,5})(\\d{3})", "$1-$2");
	}
	
	
	public static String formatarTelefone(String telefone) {
		if (telefone == null) {
			return null;
		}
		telefone = removeNonNumbers(telefone);
		return telefone.replaceFirst("(\\d{3,5})(\\d{4})", "$1-$2");
	}
	
	/**
	 * Para usar bem, ver documentaÁ„o do MessageFormat
	 * Exemplo:
	 * 
	 * int planet = 7;
	 * String event = "a disturbance in the Force";
	 * 
	 * String result = MessageFormat.format(
		     "At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.",
		     planet, new Date(), event);
		 
		 System.out.println(result);
		 
	 * @param text
	 * @param variables
	 * @return
	 */
	public static String textFormat(String text, Object... variables){
		return MessageFormat.format(text , variables);
	}
	
	public static String capitalize(String palavra){
		return WordUtils.capitalize(palavra.toLowerCase());
	}
	
	public static void main(String[] args) {
		System.out.println(formatarRg("123456789"));
		
	}
	
	public static final String removeStartingCharacter(String value, Character character){
		return value.substring(0, value.indexOf(character.toString()));
	}
	
	public static final String completaString(final String campo, final int tamanho, final String caracter){
		String retorno = campo.trim();
		while(retorno.length() <= tamanho){
			retorno += caracter;
		}
		return retorno;
	}
	
	public static String clobToString(Clob clb) {
		try {
			if (clb == null)
				return null;

			StringBuffer str = new StringBuffer();
			String strng;

			BufferedReader bufferRead = new BufferedReader(clb.getCharacterStream());

			while ((strng = bufferRead.readLine()) != null){
				str.append(strng);
			}

			return str.toString();
		} catch (Exception e) {
			return null;
		}

	}
	
	public static Map<String, String> getMapValues(String valor, String sepradorElemento) {
		Map<String, String> valores = new HashMap<String, String>();
		for (String elemento : StringUtils.split(valor, "|")) {
			String[] chaveValor = elemento.split(sepradorElemento);
			if (chaveValor.length < 2) {
				continue;
			}
			valores.put(chaveValor[0], chaveValor[1]);
		}
		return valores;
	}
	
	public static final String converteListLongEmString(List<String> ids){
		String s = "";
		String sss = "";		
		
		for (String o : ids) {
			s += new String(o.toString() + "-");
		}
		
		String[] ss = s.split("-");	
		
		Integer tamanhoList = ss.length;
		
		for (int i = 0; i < tamanhoList; i++) {
			
			if ( i == 0 ) {
				sss += ss[i] + ", ";
			}
			
			if ( i > 0 && i < tamanhoList ) {
				sss += ss[i] + ", ";
			}
			
			if ( i == (tamanhoList - 1) ) {
				sss += ss[i];
			}	
		}
		
		return sss;		
	}
	
	public static final String formataRegistoAns(String registoAns){
		String a = "";
		a = removeSpecialChar(registoAns);
		
		if(a.length() == 9){
			return a.substring(0, 3) + "." + a.substring(3, 6) + "/" + a.substring(6, 8) + "-" + a.substring(8);
		}
		
		return registoAns;
	}
	
	public static final String verificaVazio(String string) {
		return string == null ? "" : string;
	}

}