package br.com.cssis.foundation.util;

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.query.impl.ConnectionFactory;
import br.com.cssis.foundation.query.impl.GenericDBTransporter;
import br.com.cssis.foundation.query.impl.JDBCQueryRunner;

public class ColumnMessageUtil extends BasicObject {

	private static final long serialVersionUID = 1L;

	private static ColumnMessageUtil instance = new ColumnMessageUtil();

	private Map<String, EntidadeEntry> mapaEntidade = new HashMap<String, EntidadeEntry>();

	private ColumnMessageUtil() {
		restart();
	}

	@SuppressWarnings("unchecked")
	private void restart() {
		try {
			if (log().isDebugEnabled()) { 
				log().debug("Carregando mensagens customizadas para entidades.");
			}
			
			Connection conn = ConnectionFactory.getInstance().getConnection();
			List<GenericDBTransporter> lista = JDBCQueryRunner.getInstance().executeQuery("Foundation.MensagemErroColuna", null, null, 1, -1, conn);
			for (GenericDBTransporter t : lista) {
				addColuna(t.getStringValue("CLASSE"), t.getStringValue("MENSAGEM_ENTIDADE"), t.getStringValue("PROPRIEDADE"), t.getStringValue("MENSAGEM_COLUNA"), t.getStringValue("NAO_VALIDAR_ENTIDADE"), t.getStringValue("NAO_VALIDAR"));
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ColumnMessageUtil getInstance() {
		return instance;
	}

	private void addColuna(String entidade, String mensagemEntidade, String coluna, String mensagemColuna, String naoValidarEntidade, String naoValidarColuna) {
		EntidadeEntry entidadeEntry = mapaEntidade.get(StringHelper.toUpperCase(entidade));
		if (entidadeEntry == null) {
			entidadeEntry = new EntidadeEntry(entidade, mensagemEntidade, BooleanHelper.booleanValue(naoValidarEntidade));
			mapaEntidade.put(StringHelper.toUpperCase(entidade), entidadeEntry);
		}
		entidadeEntry.addColuna(coluna, mensagemColuna, BooleanHelper.getBoolean(naoValidarColuna));
		if (log().isDebugEnabled()) {
			log().debug("Entidade:" + entidade + " Coluna:" + coluna + " Mensagem:" + mensagemColuna + " Não Validar:" + naoValidarColuna);
		}
	}

	public String getMensagem(String entidade, String coluna) {
		EntidadeEntry entidadeEntry = mapaEntidade.get(StringHelper.toUpperCase(entidade));
		if (entidadeEntry == null)
			return null;
		EntidadeDetail colunaDetail = entidadeEntry.mapaColunas.get(StringHelper.toUpperCase(coluna));
		if (colunaDetail != null)
			return colunaDetail.getMensagem();
		else
			return null;
	}

	public boolean naoValidar(String entidade, String coluna) {
		EntidadeEntry entidadeEntry = mapaEntidade.get(StringHelper.toUpperCase(entidade));
		if (entidadeEntry == null)
			return false;
		EntidadeDetail colunaDetail = entidadeEntry.mapaColunas.get(StringHelper.toUpperCase(coluna));
		if (colunaDetail != null)
			return colunaDetail.isNaoValidar();
		else
			return false;
	}

	public boolean naoValidar(String entidade) {
		EntidadeEntry entidadeEntry = mapaEntidade.get(StringHelper.toUpperCase(entidade));
		if (entidadeEntry == null)
			return false;
		return entidadeEntry.naoValidar;
	}

	public static void main(String[] args) {
		System.out.println(ColumnMessageUtil.getInstance().naoValidar("br.com.tempopar.odonto.cadastrogeral.entity.Bairro", "nomePesquisa"));
	}
}

class EntidadeEntry implements Serializable {
	private static final long serialVersionUID = 1L;
	public String entidade;
	public String mensagem;
	public boolean naoValidar;
	public Map<String, EntidadeDetail> mapaColunas = new HashMap<String, EntidadeDetail>();

	public EntidadeEntry(String entidade, String mensagem, boolean naoValidar) {
		super();

		this.entidade = StringHelper.toUpperCase(entidade);
		this.mensagem = mensagem;
		this.naoValidar = naoValidar;
	}

	public void addColuna(String coluna, String mensagem, boolean naoValidar) {
		EntidadeDetail colunaDetail = new EntidadeDetail(coluna, mensagem, naoValidar);
		mapaColunas.put(StringHelper.toUpperCase(coluna), colunaDetail);
	}
}

class EntidadeDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	public String coluna;
	public String mensagem;
	public boolean naoValidar = false;

	public EntidadeDetail(String coluna, String mensagem, boolean naoValidar) {
		super();
		this.coluna = coluna;
		this.mensagem = mensagem;
		this.naoValidar = naoValidar;
	}

	public String getColuna() {
		return coluna;
	}

	public void setColuna(String coluna) {
		this.coluna = coluna;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public boolean isNaoValidar() {
		return naoValidar;
	}

	public void setNaoValidar(boolean naoValidar) {
		this.naoValidar = naoValidar;
	}

}
