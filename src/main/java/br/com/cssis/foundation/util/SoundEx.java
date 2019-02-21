package br.com.cssis.foundation.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SoundEx {

	private static final String accentConversionString    = "·ÈÌÛ˙¡…Õ”⁄‚ÍÓÙ˚¬ Œ‘€„ıÒ√’—‡ËÏÚ˘¿»Ã“Ÿ‰ÎÔˆ¸ƒÀœ÷‹Á«yYqQnNxXzZ";
	private static final String nonAccentConversionString = "aeiouAEIOUaeiouAEIOUaonAONaeiouAEIOUaeiouAEIOUsSiIkKmMsSsS";
	private static final Map<Character, Character> accentConversionMap;
	private static final String NO_SOUNDEX = "NOSOUNDEX";
	private static final List<FoneticRule> foneticRuleList;
	
	static {
		accentConversionMap = new HashMap<Character, Character>(accentConversionString.length());
		for (int i = 0; i < accentConversionString.length(); i++) {
			accentConversionMap.put(accentConversionString.charAt(i), nonAccentConversionString.charAt(i));
		}
		
		foneticRuleList = new ArrayList<FoneticRule>();
		
		// composite rules, must not reaply on the same sequence inputed here
		
		foneticRuleList.add(new FoneticRule("MG", "G" , false, true));
		foneticRuleList.add(new FoneticRule("RG", "G" , false, true));
		foneticRuleList.add(new FoneticRule("CH", "S" , false, true));
		foneticRuleList.add(new FoneticRule("ST", "T", false, true));	
		foneticRuleList.add(new FoneticRule("RS","S", false, true));
		
		foneticRuleList.add(new FoneticRule("SHOW", "SHOU"));
		foneticRuleList.add(new FoneticRule("SCH", "X"));

		foneticRuleList.add(new FoneticRule("W","V", false, true));
		
		foneticRuleList.add(new FoneticRule("BR", "B"));
		foneticRuleList.add(new FoneticRule("BL", "B"));
		
		foneticRuleList.add(new FoneticRule("PH", "F"));
		
		foneticRuleList.add(new FoneticRule("GR", "G"));
		foneticRuleList.add(new FoneticRule("GL", "G"));

		foneticRuleList.add(new FoneticRule("NG", "G"));
		
		foneticRuleList.add(new FoneticRule("GE", "J"));
		foneticRuleList.add(new FoneticRule("GI", "J"));
		foneticRuleList.add(new FoneticRule("RJ", "J"));
		foneticRuleList.add(new FoneticRule("MJ", "J"));
		foneticRuleList.add(new FoneticRule("NJ", "J"));
		
		foneticRuleList.add(new FoneticRule("CA", "K"));
		foneticRuleList.add(new FoneticRule("CO", "K"));
		foneticRuleList.add(new FoneticRule("CU", "K"));
		foneticRuleList.add(new FoneticRule("CK", "K"));
		
		foneticRuleList.add(new FoneticRule("LH", "L"));
		
		//foneticRuleList.add(new FoneticRule("SM", "M"));
		
		foneticRuleList.add(new FoneticRule("RM", "SM"));
		
		foneticRuleList.add(new FoneticRule("NH", "N"));
		foneticRuleList.add(new FoneticRule("GM", "M"));
		foneticRuleList.add(new FoneticRule("MD", "M"));
		
		
		foneticRuleList.add(new FoneticRule("PR", "P"));

		foneticRuleList.add(new FoneticRule("CE", "S"));
		foneticRuleList.add(new FoneticRule("CI", "S"));
		foneticRuleList.add(new FoneticRule("CS", "S"));

		foneticRuleList.add(new FoneticRule("TS","S"));
		
		foneticRuleList.add(new FoneticRule("LT", "T"));
		foneticRuleList.add(new FoneticRule("TL", "T"));
		foneticRuleList.add(new FoneticRule("TR", "T"));
		foneticRuleList.add(new FoneticRule("CT", "T"));
		foneticRuleList.add(new FoneticRule("RT", "T"));
		foneticRuleList.add(new FoneticRule("CT", "T"));

		
		// final L by R
		foneticRuleList.add(new FoneticRule("L", "R"));
		
		// remove terminations
		foneticRuleList.add(new FoneticRule("S", "", true));
		foneticRuleList.add(new FoneticRule("Z", "", true));
		foneticRuleList.add(new FoneticRule("R", "", true));
		foneticRuleList.add(new FoneticRule("M", "", true));
		foneticRuleList.add(new FoneticRule("N", "", true));
		foneticRuleList.add(new FoneticRule("AO","", true));
		foneticRuleList.add(new FoneticRule("L", "", true));
		
	}

	public static String encode(String source) {
		if (StringHelper.isEmpty(source))
			return source;
		String nonAccent = applySimpleRules(source);
		Pattern patSplitter = Pattern.compile("[^a-z^A-Z^0-9]");
		String[] items = patSplitter.split(nonAccent);
		StringBuilder returnValue = new StringBuilder();
		for (int i = 0; i < items.length; i++) {
			if (StringHelper.nonEmpty(items[i])) {
				returnValue.append(applyFoneticRules(items[i]));
			}
		}
		if(StringHelper.isEmpty(returnValue.toString())){
			return NO_SOUNDEX;
		}else{
			return returnValue.toString();
		}
				
	}

	private static String applySimpleRules(String source) {
		char[] modified = source.toUpperCase().toCharArray();
		for (int i = 0; i < modified.length; i++) {
			Character replacementCh = accentConversionMap.get(modified[i]);
			if (replacementCh != null) {
				modified[i] = replacementCh.charValue();
			}
		}
		return String.valueOf(modified);
	}

	private static String applyFoneticRules(String source) {
		String foneticString = source;

		
		for (FoneticRule fr : FoneticRule.getListEndOfExpression(foneticRuleList, false)) {
			String mark = "##";
			if ( fr.allowRecursiveReplacement ) {
				mark = "";
			}
			foneticString = foneticString.replaceAll(fr.getRegEx(), mark + fr.getReplacement() + mark);
		}
		foneticString = foneticString.replaceAll("##", "");

		for (FoneticRule fr : FoneticRule.getListEndOfExpression(foneticRuleList, true)) {
			foneticString = foneticString.replaceAll(fr.getRegEx(), fr.getReplacement());
		}
		
		foneticString = removeConsecutiveDuplicateAndVowels(foneticString);
		return foneticString;
	}

	private static String removeConsecutiveDuplicateAndVowels(String source) {
		return source.replaceAll("(A|E|I|O|U|H)", "").replaceAll("([a-zA-Z])(?=\\1)","");
		
	}

	public static void main(String[] args) throws Exception {
		Map<String, String> testingValues = new HashMap<String, String>();
		
		testingValues.put("Broco", "BK");
		testingValues.put("Bloco", "BK");
		testingValues.put("Casa", "KS");
		testingValues.put("Kasa", "KS");
		testingValues.put("Cela", "SR");
		testingValues.put("Sela", "SR");
		testingValues.put("Circo", "SRK");
		testingValues.put("Sirco", "SRK");
		testingValues.put("Coroar", "KR");
		testingValues.put("Koroar", "KR");
		testingValues.put("Cuba", "KB");
		testingValues.put("Kuba", "KB");
		testingValues.put("RoÁa", "RS");
		testingValues.put("Rosa", "RS");
		testingValues.put("Ameixa", "MS");
		testingValues.put("Ameicha", "MS");
		testingValues.put("TORACS", "TR");
		testingValues.put("TORAX", "TR");
		testingValues.put("Compactar", "KMPT");
		testingValues.put("Compatar", "KMPT"); 
		testingValues.put("Gana", "GM");
		testingValues.put("Gene", "JM");		
		testingValues.put("Gana", "GM");
		testingValues.put("Gene", "JM");
		testingValues.put("Gibi", "JB");
		testingValues.put("Gostar", "GT");
		testingValues.put("Guabiru", "GBR");
		testingValues.put("Fleuma", "FRM");
		testingValues.put("Fleugma", "FRM");
		testingValues.put("Negro", "MG");
		testingValues.put("Nego", "MG");			
		testingValues.put("HierÛglifo", "RGF");
		testingValues.put("HierÛgrifo", "RGF");		
		testingValues.put("TORACS", "TR");
		testingValues.put("TORAX", "TR");
		testingValues.put("Luminar", "RM");
		testingValues.put("Ruminar", "RM");
		testingValues.put("Mudez", "MD");
		testingValues.put("Nudez", "MD");
		testingValues.put("Comendo", "KM");
		testingValues.put("Comeno", "KM");
		testingValues.put("Philipe", "FRP");
		testingValues.put("Felipe", "FRP");
		testingValues.put("Lagartixa", "RGTS");
		testingValues.put("Largatixa", "RGTS");
		testingValues.put("Queijo", "KJ");
		testingValues.put("Keijo", "KJ");
		testingValues.put("Lagarto", "RGT");
		testingValues.put("Largato", "RGT");
		testingValues.put("Perspectiva", "PSPTV");
		testingValues.put("Pespectiva", "PSPTV");
		testingValues.put("Bunginganga", "BJG");
		testingValues.put("Bugiganga", "BJG");
		testingValues.put("Mesmo", "MSM");
		testingValues.put("Mermo", "MSM");
		testingValues.put("Virgem", "VJ");
		testingValues.put("Vige", "VJ");
		testingValues.put("SuperstiÁ„o", "SPTS");
		testingValues.put("SupertiÁ„o", "SPTS");
		testingValues.put("Estupro", "TP");
		testingValues.put("Estrupo", "TP");
		testingValues.put("Contrato", "KMT");
		testingValues.put("Contlato", "KMT");
		testingValues.put("Kubixeque", "KBSK");
		testingValues.put("Kubitscheck", "KBSK");
		testingValues.put("Walter", "VT");
		testingValues.put("Valter", "VT");
		testingValues.put("Exceder", "SD");
		testingValues.put("Esceder", "SD");
		testingValues.put("Yara", "R");
		testingValues.put("Iara", "R");
		testingValues.put("Casa", "KS");
		testingValues.put("Caza", "KS");

		Iterator<String> itKeys = testingValues.keySet().iterator();
		while (itKeys.hasNext()) {
			String valueToEnconde = itKeys.next();
			String expectedValue =  testingValues.get(valueToEnconde);
			String encodedValue = SoundEx.encode(valueToEnconde);
			if (!encodedValue.equalsIgnoreCase(expectedValue)) {
				System.out.println("valueToEnconde=" + valueToEnconde + 
						" encodedValue=" + encodedValue +
						" expectedValue=" + expectedValue);				
				System.out.println("Erro No Enconde");
			}
		}
		
		String nome = "Marcus VinÌcius da Costa Soares";
		System.out.println(SoundEx.encode(nome));

		nome = "Show da Xuxa";
		System.out.println(SoundEx.encode(nome));
		
	}
}

class FoneticRule {
	String silab;
	String replacement;
	boolean endOfExpression = false;
	boolean allowRecursiveReplacement = false;

	public boolean isEndOfExpression() {
		return endOfExpression;
	}

	public void setEndOfExpression(boolean endOfExpression) {
		this.endOfExpression = endOfExpression;
	}

	public String getSilab() {
		return silab;
	}

	public void setSilab(String silab) {
		this.silab = silab;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	public FoneticRule(String silab, String replacement) {
		super();
		this.silab = silab;
		this.replacement = replacement;
	}

	public FoneticRule(String silab, String replacement, boolean endOfExpression) {
		this(silab, replacement);
		this.endOfExpression = endOfExpression;
	}

	public FoneticRule(String silab, String replacement, boolean endOfExpression, boolean allowRecursiveReplacement) {
		this(silab, replacement, endOfExpression);
		this.allowRecursiveReplacement = allowRecursiveReplacement; 
	}
	
	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (allowRecursiveReplacement ? 1231 : 1237);
		result = prime * result + (endOfExpression ? 1231 : 1237);
		result = prime * result + ((replacement == null) ? 0 : replacement.hashCode());
		result = prime * result + ((silab == null) ? 0 : silab.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FoneticRule other = (FoneticRule) obj;
		if (allowRecursiveReplacement != other.allowRecursiveReplacement)
			return false;
		if (endOfExpression != other.endOfExpression)
			return false;
		if (replacement == null) {
			if (other.replacement != null)
				return false;
		} else if (!replacement.equals(other.replacement))
			return false;
		if (silab == null) {
			if (other.silab != null)
				return false;
		} else if (!silab.equals(other.silab))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "silaba=" + silab + " rep=" + replacement + " final=" + endOfExpression;
	}

	public String getRegEx() {
		if (endOfExpression) {
			return getSilab() + "$";
		} else {
			return getSilab() + "";
		}
	}
	
	public static List<FoneticRule> getListEndOfExpression(List<FoneticRule> source, boolean isEnd) {
		List<FoneticRule> returnValue = new ArrayList<FoneticRule>();
		for (FoneticRule foneticRule : source) {
			if (foneticRule.isEndOfExpression() == isEnd) returnValue.add(foneticRule);
		}
		
		return returnValue;
	}
}