package br.com.cssis.foundation.validator.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cssis.foundation.BaseEntity;
import br.com.cssis.foundation.ExceptionConstants;
import br.com.cssis.foundation.ExceptionHandler;
import br.com.cssis.foundation.query.impl.FilterCondition;
import br.com.cssis.foundation.query.impl.Operator;
import br.com.cssis.foundation.query.impl.SortingCondition;
import br.com.cssis.foundation.service.BasicServiceHandlerImpl;
import br.com.cssis.foundation.service.PersistenceService;
import br.com.cssis.foundation.service.ServiceException;
import br.com.cssis.foundation.util.DateTimeHelper;
import br.com.cssis.foundation.util.NumberHelper;
import br.com.cssis.foundation.util.ReflectionUtil;
import br.com.cssis.foundation.validator.ValidarDataInicioAwareable;
import br.com.cssis.foundation.validator.ValidatorService;

@Transactional(rollbackFor = Throwable.class, propagation= Propagation.REQUIRED)
public abstract class ValidatorServiceImpl extends BasicServiceHandlerImpl implements ValidatorService {
	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "entityManagerFactory")
	protected EntityManager em;

	@Resource(name = "oracleDataSource")
	protected DataSource defaultDS;

	public EntityManager getEm() {
		return em;
	}

	public DataSource getDefaultDS() {
		return defaultDS;
	}

	public abstract PersistenceService<BaseEntity> getService();

	@Override
	public <T extends BaseEntity> void validarDataInicioFim(Class<T> clazz, T value, Long[] referenceIds, String[] referencedIdsName, long constantError) throws ServiceException {
		validarDataInicioFim(clazz, value, "id", "dataInicio", "dataFim", referenceIds, referencedIdsName, constantError);
	}

	public <T extends BaseEntity> void validarDataInicioFim(Class<T> clazz, T value, String idFieldName, String dataInicioFieldName, String dataFimFieldName, Long[] referenceIds, String[] referencedIdsName, long constantError) throws ServiceException {
		try {
			// obter os valores id, dataInicio e dataFim
			Long id = (Long) ReflectionUtil.executeGetterMethod(value, idFieldName);
			Date dtInicio = (Date) ReflectionUtil.executeGetterMethod(value, dataInicioFieldName);
			Date dtFim = (Date) ReflectionUtil.executeGetterMethod(value, dataFimFieldName);

			// Não devem ser iguais e também a data inicial deve ser maior do
			// que a data fim (exceto nulo)
			long dateComp = DateTimeHelper.compareDates(dtInicio, dtFim);
			if (dateComp > 0 && dtFim != null) {
				throw ExceptionHandler.getInstance().generateException(ServiceException.class, constantError, "Os valores de DataInicio não deve ser maior do que a DataFim.");
			}

			List<FilterCondition> criteriaList = new ArrayList<FilterCondition>();
			// não deve exister perído com colisão de datas (intersecção de
			// datas com intervalo fechado)
			criteriaList.add(new FilterCondition(Operator.GREATHER_EQUAL_NULL, "dataFim", dtInicio));
			if (dtFim != null) {
				criteriaList.add(new FilterCondition(Operator.LESS_EQUAL, "dataInicio", dtFim));
			}

			// remove 'eu mesmo' (no caso de update)
			if (id != null) {
				criteriaList.add(new FilterCondition(Operator.NOT_EQUAL, "id", id));
			}
			
			// adiciona os campos que compoe 'unicidade' das datas
			for (int i = 0; i < referenceIds.length; i++) {
				criteriaList.add(new FilterCondition(referencedIdsName[i], referenceIds[i]));
			}
			// finalmente pega o count
			if (getService().getCountByProperties(clazz, criteriaList) > 0) {
				throw ExceptionHandler.getInstance().generateException(ServiceException.class, constantError, "Período de DataInicio e DataFim inválido, houve colisão de datas.");
			}
			
			// verificar se as configurações da DataInicio estão disponíveis para a entidade
			if(value instanceof ValidarDataInicioAwareable){
				FilterCondition.removeCondition(criteriaList, "dataInicio", "dataFim");
				
				if(!((ValidarDataInicioAwareable)value).equalDataInicio()){
					criteriaList.add(new FilterCondition(Operator.EQUAL, "dataInicio", dtInicio));
				}
				
				if (getService().getCountByProperties(clazz, criteriaList) > 0) {
					throw ExceptionHandler.getInstance().generateException(ServiceException.class, constantError, "Período de DataInicio e DataFim inválido, houve colisão de datas.");
				}
			}
		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	@Override
	public <T extends BaseEntity> void validarFaixaInicioFim(Class<T> clazz, T value, String idFieldName, String quantidadeInicioName, String quantidadeFimName, Long quantidadeInicioValue, Long quantidadeFimValue, Long[] referenceIds, String[] referencedIdsName, long constantError) throws ServiceException {
		try {
			Long id = (Long) ReflectionUtil.executeGetterMethod(value, idFieldName);

			if (NumberHelper.doubleValue(quantidadeFimValue) < NumberHelper.doubleValue(quantidadeInicioValue)) {
				throw ExceptionHandler.getInstance().generateException(ServiceException.class, constantError, "A quantidade final deve ser maior do que a quantidade inicial.");
			}

			List<FilterCondition> criteriaList = new ArrayList<FilterCondition>();

			criteriaList.add(new FilterCondition(Operator.GREATHER_EQUAL, quantidadeFimName, quantidadeInicioValue));
			criteriaList.add(new FilterCondition(Operator.LESS_EQUAL, quantidadeInicioName, quantidadeFimValue));

			// remove 'eu mesmo' (no caso de update)
			if (id != null) {
				criteriaList.add(new FilterCondition(Operator.NOT_EQUAL, "id", id));
			}
			// adiciona os campos que compoe 'unicidade' das datas
			for (int i = 0; i < referenceIds.length; i++) {
				criteriaList.add(new FilterCondition(referencedIdsName[i], referenceIds[i]));
			}
			// finalmente pega o count
			int count = getService().getCountByProperties(clazz, criteriaList);
			if (count > 0) {
				throw ExceptionHandler.getInstance().generateException(ServiceException.class, constantError, "Período de Quantidade Inicial e Final Inválido. Houve intersecção de Valores.");
			}
		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	@Override
	public <T extends BaseEntity> void validarIntervaloContiguo(Class<T> clazz, T value, String quantidadeInicioName, String quantidadeFimName, Long[] referenceIds, String[] referencedIdsName, long constantError) throws ServiceException {
		class Intervalo implements Comparable<Intervalo> {
			public long inicio;
			public long fim;

			public Intervalo(long inicio, long fim) {
				super();
				this.inicio = inicio;
				this.fim = fim;
			}

			@Override
			public int compareTo(Intervalo o) {
				if (o == null)
					return -1;

				if (o.inicio > this.inicio ) {
					return -1;
				} else if (o.inicio < this.inicio ) {
					return 1;
				} else
					return 0;
			}

			public String toString() {
				return inicio + "-" + fim;
			}
		}

		try {
			Long id = (Long) ReflectionUtil.executeGetterMethod(value, "id");

			List<FilterCondition> criteriaList = new ArrayList<FilterCondition>();
			// adiciona os campos que compoe 'unicidade' das datas
			for (int i = 0; i < referenceIds.length; i++) {
				criteriaList.add(new FilterCondition(referencedIdsName[i], referenceIds[i]));
			}
			if (!value.isIdNull()) {
				criteriaList.add(new FilterCondition(Operator.NOT_EQUAL, "id", id));
			}

			List<SortingCondition> sortingList = new ArrayList<SortingCondition>();
			sortingList.add(new SortingCondition(quantidadeInicioName));

			// finalmente pega o count
			List<T> listaValores = getService().findByProperties(clazz, criteriaList, sortingList);
			if (listaValores.size() == 0)
				return;

			SortedSet<Intervalo> listaIntervalo = new TreeSet<Intervalo>();
			for (int i = 0; i < listaValores.size(); i++) {
				long qtdeInicial = NumberHelper.longValue(ReflectionUtil.executeGetterMethod(listaValores.get(i), quantidadeInicioName));
				long qtdeFinal = NumberHelper.longValue(ReflectionUtil.executeGetterMethod(listaValores.get(i), quantidadeFimName));
				listaIntervalo.add(new Intervalo(qtdeInicial, qtdeFinal));
			}

			long qtdeInicial = NumberHelper.longValue(ReflectionUtil.executeGetterMethod(value, quantidadeInicioName));
			long qtdeFinal = NumberHelper.longValue(ReflectionUtil.executeGetterMethod(value, quantidadeFimName));
			listaIntervalo.add(new Intervalo(qtdeInicial, qtdeFinal));

			Intervalo[] listaValoresOrdenados = listaIntervalo.toArray(new Intervalo[0]);

			for (int i = 0; i < listaValoresOrdenados.length; i++) {
				if (i > 0) {
					if (listaValoresOrdenados[i].inicio != (listaValoresOrdenados[i - 1].fim + 1)) {
						StringBuilder b = new StringBuilder();
						b.append("Os intervalos informados não são contíguos.");
						throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, b.toString());
					}
				}
				if (i < (listaValoresOrdenados.length - 1)) {
					if (listaValoresOrdenados[i].fim != (listaValoresOrdenados[i + 1].inicio - 1)) {
						StringBuilder b = new StringBuilder();
						b.append("Os intervalos informados não são contíguos.");
						throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, b.toString());
					}
				}
			}

		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}

		// sql para validar inico e fim de uma tabela com inicio, fim e cobranca
		// caso volte algum registro...inválido. tem opção de validar o inicio =
		// 1 e fim
		// = 999 que pode ser parametrizado
		// SELECT * FROM
		// (SELECT T1.INICIO, T1.FIM ,
		// (SELECT MIN(T2.INICIO) FROM T1 T2 WHERE T2.INICIO > T1.INICIO AND
		// T2.COBRANCA
		// = T1.COBRANCA ) AS PROXIMO,
		// FIM+1 AS SUG_PROXIMO,
		// (SELECT MAX(T2.FIM) FROM T1 T2 WHERE T2.FIM < T1.FIM AND T2.COBRANCA
		// =
		// T1.COBRANCA) AS ANTERIOR,
		// INICIO - 1 AS SUG_ANTERIOR
		// FROM T1 WHERE T1.COBRANCA = 1) T
		// WHERE ( T.PROXIMO IS NOT NULL AND (T.FIM + 1) != T.PROXIMO )
		// OR ( T.ANTERIOR IS NOT NULL AND (T.INICIO-1) != T.ANTERIOR )
		// OR ( T.ANTERIOR IS NULL AND T.INICIO != 1 )
		// OR ( T.PROXIMO IS NULL AND T.FIM != 999 )
	}
	
	
	@Override
	public <T extends BaseEntity> void validarParcelaInicioFim(Class<T> clazz, T value, String idFieldName, String parInicioFieldName, String parFimFieldName, Object[] referenceIds, String[] referencedIdsName,String tpVitalicio, long constantError) throws ServiceException {
		try {
			// obter os valores id, valInicio e valFim
			Long id = (Long) ReflectionUtil.executeGetterMethod(value, idFieldName);
			Short parIniNovo = (Short) ReflectionUtil.executeGetterMethod(value, parInicioFieldName);
			Short parFimNovo = (Short) ReflectionUtil.executeGetterMethod(value, parFimFieldName);

			// Não devem ser iguais e também a data inicial deve ser maior do
			// que a data fim (exceto nulo)
			
			if (tpVitalicio != null && !tpVitalicio.trim().equals("S") && (parIniNovo > parFimNovo) && parFimNovo != null ) {
				throw ExceptionHandler.getInstance().generateException(ServiceException.class, constantError, "Os valores de Parcela Inicial não deve ser maior do que a Parcela Final.");
			}

			List<FilterCondition> criteriaList = new ArrayList<FilterCondition>();
			// não deve exister perído com colisão de datas (intersecção de
			// valores com intervalo fechado)
			FilterCondition parcelaFim = new FilterCondition(Operator.GREATHER_EQUAL, "parcelaFinal", parIniNovo);
			FilterCondition parcelaNulo = new FilterCondition("parcelaFinal", null);
			parcelaFim.addCondition(FilterCondition.OR, parcelaNulo);
			criteriaList.add(parcelaFim);
			if (parFimNovo != null) {
				criteriaList.add(new FilterCondition(Operator.LESS_EQUAL, "parcelaInicial", parFimNovo));
			}

			// remove 'eu mesmo' (no caso de update)
			if (id != null) {
				criteriaList.add(new FilterCondition(Operator.NOT_EQUAL, "id", id));
			}
			//adiciona os campos que compoe 'unicidade' da parcelas 
			for (int i = 0; i < referenceIds.length; i++) {
				criteriaList.add(new FilterCondition(referencedIdsName[i], referenceIds[i]));
			}
			
			// finalmente pega o count
			int count = getService().getCountByProperties(clazz, criteriaList);
			if (count > 0) {
				throw ExceptionHandler.getInstance().generateException(ServiceException.class, constantError, "Período de Parcela Inicial e Parcela Final inválido, houve colisão de parcelas.");
			}
		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}
	
	@Override
	public <T extends BaseEntity> void validarParcelaIntervalo(Class<T> clazz, T value, String parInicioName, String parFimName, Object[] referenceIds, String[] referencedIdsName, long constantError) throws ServiceException {
		class Intervalo implements Comparable<Intervalo> {
			public long inicio;
			public long fim;

			public Intervalo(long inicio, long fim) {
				super();
				this.inicio = inicio;
				this.fim = fim;
			}

			@Override
			public int compareTo(Intervalo o) {
				if (o == null)
					return -1;

				if (o.inicio > this.inicio ) {
					return -1;
				} else if (o.inicio < this.inicio ) {
					return 1;
				} else
					return 0;
			}

			public String toString() {
				return inicio + "-" + fim;
			}
		}

		try {
			Long id = (Long) ReflectionUtil.executeGetterMethod(value, "id");
			Short parIniNovo = (Short) ReflectionUtil.executeGetterMethod(value, parInicioName);
			List<FilterCondition> criteriaList = new ArrayList<FilterCondition>();
			// adiciona os campos que compoe 'unicidade' das datas
			for (int i = 0; i < referenceIds.length; i++) {
				criteriaList.add(new FilterCondition(referencedIdsName[i], referenceIds[i]));
			}
			
			/*
			if (!value.isIdNull()) {
				criteriaList.add(new FilterCondition(Operator.NOT_EQUAL, "id", id));
			}
			*/

			List<SortingCondition> sortingList = new ArrayList<SortingCondition>();
			sortingList.add(new SortingCondition(parInicioName));

			// finalmente pega o count
			List<T> listaValores = getService().findByProperties(clazz, criteriaList, sortingList);
			if (listaValores.size() == 0){
				if(parIniNovo != 1)
				    throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, "Obrigatoriamente a parcela inicio deve começar pela 1.");
				return;
			}
			
			SortedSet<Intervalo> listaIntervalo = new TreeSet<Intervalo>();
			for (int i = 0; i < listaValores.size(); i++) {
				long qtdeInicial = NumberHelper.longValue(ReflectionUtil.executeGetterMethod(listaValores.get(i), parInicioName));
				long qtdeFinal = NumberHelper.longValue(ReflectionUtil.executeGetterMethod(listaValores.get(i), parFimName));
				if(i==0)
					qtdeInicial=1;
				listaIntervalo.add(new Intervalo(qtdeInicial, qtdeFinal));
			}

			long qtdeInicial = NumberHelper.longValue(ReflectionUtil.executeGetterMethod(value, parInicioName));
			long qtdeFinal = NumberHelper.longValue(ReflectionUtil.executeGetterMethod(value, parFimName));
			listaIntervalo.add(new Intervalo(qtdeInicial, qtdeFinal));

			Intervalo[] listaValoresOrdenados = listaIntervalo.toArray(new Intervalo[0]);

			for (int i = 0; i < listaValoresOrdenados.length; i++) {
				if (i > 0) {
					if (listaValoresOrdenados[i].inicio != (listaValoresOrdenados[i - 1].fim + 1)) {
						StringBuilder b = new StringBuilder();
						b.append("Os intervalos informados não são contíguos.");
						throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, b.toString());
					}
				}
				if (i < (listaValoresOrdenados.length - 1)) {
					if (listaValoresOrdenados[i].fim != (listaValoresOrdenados[i + 1].inicio - 1)) {
						StringBuilder b = new StringBuilder();
						b.append("Os intervalos informados não são contíguos.");
						throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, b.toString());
					}
				}
			}

		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}
}
