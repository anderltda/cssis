package br.com.cssis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.cssis.foundation.util.StringHelper;

public class Teste {

	public static void main(String[] args) throws Exception {

		String logradouro = "RUA BARCO AVENIDA DO RIO BRANCO R., 3361";
		String numero = logradouro.substring(logradouro.indexOf(","), logradouro.length());
		String logradouro_ = StringHelper.subString("AVENIDA", logradouro.length());
		
		

/*		System.out.println(logradouro.substring(0, logradouro.indexOf(",")));
		System.out.println(StringHelper.revert(logradouro));
		System.out.println(StringHelper.removeStartingCharacter(logradouro, 'A'));
		System.out.println(StringHelper.removeCharacters(numero));

		String text    =
				"John writes about this, and John Doe writes about that," +
						" and John Wayne writes about everything."
						;

		String patternString1 = "((John)) ";

		Pattern      pattern      = Pattern.compile(patternString1);
		Matcher      matcher      = pattern.matcher(text);
		StringBuffer stringBuffer = new StringBuffer();

		while(matcher.find()){
			matcher.appendReplacement(stringBuffer, "");
			System.out.println(stringBuffer.toString());
		}
		matcher.appendTail(stringBuffer);

		System.out.println(stringBuffer.toString());*/
		
		
		String text = "John went for a walk, and John fell down, and John hurt his knee.";
			
		System.out.println(logradouro);
		System.out.println(logradouro.replaceAll("[[x?AVENIDA]]", "*"));
		System.out.println(logradouro.toUpperCase().replaceAll("x?AVENIDA\\s|x?RUA\\s", ""));

	}

}
