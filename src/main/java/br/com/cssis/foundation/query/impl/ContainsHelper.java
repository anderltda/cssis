package br.com.cssis.foundation.query.impl;

import br.com.cssis.foundation.Constants;
import br.com.cssis.foundation.util.StringHelper;

public class ContainsHelper {

	// retorna true se for identificado que dever� ser utilizado o contains
	public static final boolean tratarBuscaNomeContains(String source, StringBuilder returnValue) throws Exception {
		if (StringHelper.isEmptyTrim(source))
			return false;
		// faz os split por espa�os. M�ltipos * e % s�o tratados como apenas 1 . Adiciona um * no final de qq forma
		String sourceTratada = source + "*"; 
		sourceTratada = sourceTratada.replaceAll("\\*", "%").replaceAll("([\\%])\\1{0,}", "%").replaceAll("([\\s])\\1{0,}", " "); 

		// busca a primeira posi��o
		int idxCoringa = sourceTratada.indexOf("%");
		boolean utilizarContains = idxCoringa >= 0 && idxCoringa <= Constants.BUSCA_CONTAINS_TAMANHO_MIM_TOKEN;

		if (utilizarContains) {
			String tokens[] = StringHelper.splitTrim(sourceTratada);
			// se a quantidade tokens for apenas 1, dever� ter tamanho pelo menos 5"
			if (tokens.length == 1) {
				idxCoringa = tokens[0].indexOf("%");
				tokens[0] = tokens[0].replaceAll("\\%", "");
				int tamanho = tokens[0].length();
				if (tamanho < Constants.BUSCA_CONTAINS_TAMANHO_MIM_TOKEN) {
					throw new Exception("Nome para pesquisa muito pequeno. Para buscas por nomes simples o tamanho m�nimo(exclu�do espa�os e coringa) �: " + Constants.BUSCA_CONTAINS_TAMANHO_MIM_TOKEN);
				}
			} else {
				// nome composto, verificar se o existe % sozinho em alguma palavra, caso exista, dar erro. O Contains n�o permite tokens muito
				// pequenos para o %, aproveitar para verificar se o tamanho total dos tokens
				int tamanhoTotal = 0, tamanhoLocalToken = 0;
				for (int i = 0; i < tokens.length; i++) {
					tamanhoLocalToken = tokens[i].replaceAll("\\%", "").length();
					if (tokens[i].indexOf("%") >= 0 && tamanhoLocalToken < Constants.BUSCA_CONTAINS_TAMANHO_MIM_TOKEN_CORINGA) {
						throw new Exception("Uso incorreto do coringa *. Em buscas com v�rios nomes o coringa n�o pode vir sozinho e o tamanho m�mino para a palavra que contem o coringa �: " + Constants.BUSCA_CONTAINS_TAMANHO_MIM_TOKEN_CORINGA + ". Exemplo: * Jos� Da Silva n�o uma busca v�lida. ");
					}
					tamanhoTotal += tamanhoLocalToken;
				}
				// verifica se o tamanho m�nimo foi atingido
				if (tamanhoTotal < Constants.BUSCA_CONTAINS_TAMANHO_MIM_TOKEN) {
					throw new Exception("Nome para pesquisa muito pequeno. Para buscas por nomes compostos, a quantidade m�mina de caracteres v�lidos (excl�ido o coringa * e espa�os) �: " + Constants.BUSCA_CONTAINS_TAMANHO_MIM_TOKEN);
				}
			}

			// o contains ser� montado e quebrado pelo query ou pq qq um que v� usar, vai retornar apenas
			// a busca sem acentos e upper case (como se fosse o like
			StringBuilder tmpBuffer = new StringBuilder();
			for (int i = 0; i < tokens.length; i++) {
				String tmp = StringHelper.removeSpecialChar(tokens[i]);
				tmpBuffer.append(StringHelper.getNonAccentString(tmp)).append(" ");
			}
			StringHelper.removeLast(tmpBuffer);
			// caso exista algum * no final, juntar a palavra anterior, e devolver com %
			returnValue.append(tmpBuffer.toString().replaceAll("\\s\\%\\z", "%"));
		} else {
			// n�o utiliza contains, ai n�o importa os tokens
			int tamanhoSemCorginga = StringHelper.removeSpecialChar(sourceTratada).length();
			if (tamanhoSemCorginga < Constants.BUSCA_CONTAINS_TAMANHO_MIM_TOKEN) {
				throw new Exception("Nome para pesquisa muito pequeno. Para buscas por nomes simples o tamanho m�nimo(exclu�do espa�os e coringa) �: " + Constants.BUSCA_CONTAINS_TAMANHO_MIM_TOKEN);
			}
			returnValue.append(StringHelper.getNonAccentString(sourceTratada.toUpperCase()));
			if (sourceTratada.indexOf("%") < 0) {
				returnValue.append("%");
			} 
		}
		return utilizarContains;
	}

	public static final String[] quebrarTokensContains(String source) {
		if (StringHelper.isEmpty(source))
			return new String[0];
		return source.replaceAll("\\*", "%").replaceAll("([\\%])\\1{0,}", "%").replaceAll("([\\s])\\1{0,}", "%").split("\\%{1,}");
	}

	public static final StringBuilder tratarBuscaCodigoSeguradoContains(String source) throws Exception {
		if (StringHelper.isEmptyTrim(source))
			return null;
		// faz os split por espa�os. M�ltipos * e % s�o tratados como apenas 1
		StringBuilder sourceTratada = new StringBuilder();
		sourceTratada.append("%").append(StringHelper.removeSpecialChar(source)).append("%");
		return sourceTratada;
	}

}
