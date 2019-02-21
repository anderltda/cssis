package br.com.cssis.quartz.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import br.com.cssis.dominio.cadastro.entity.FarmConvEndereco;
import br.com.cssis.foundation.query.impl.FilterCondition;
import br.com.cssis.foundation.query.impl.Operator;
import br.com.cssis.foundation.service.ServiceException;
import br.com.cssis.foundation.util.MapLinkHelper;
import br.com.cssis.foundation.util.SetHelper;
import br.com.cssis.maplink2.webservice.Point;

public class ProcessarTmpEnderecoQuartzJobBean extends BaseQuartzJobBean {

	private static final Log LOG = LogFactory.getLog(ProcessarTmpEnderecoQuartzJobBean.class);

	@Override
	public void executeIssoSeLiberado(JobExecutionContext context) throws ServiceException {

		List<FilterCondition> criteriaList = new ArrayList<FilterCondition>();

		try {

			criteriaList.add(new FilterCondition(Operator.NULL_REPRESENTATION, "latitude", null));
			criteriaList.add(new FilterCondition(Operator.NULL_REPRESENTATION, "longitude", null));

			List<FarmConvEndereco> list = getCommonsService().findByProperties(FarmConvEndereco.class, criteriaList, null);

			if(SetHelper.nonEmpty(list)) {

				Point point = null;

				for (FarmConvEndereco farmConvEndereco : list) {

					try {

						System.out.println("ID: " + farmConvEndereco.getId());
						System.out.println("Logradouro: " + farmConvEndereco.getLogradouro());
						
						point = MapLinkHelper.getAddress(farmConvEndereco);
						
						if(point != null) {		
							
							farmConvEndereco.setLatitude(point.getY());
							farmConvEndereco.setLongitude(point.getX());							
							System.out.println("Endereço: " + farmConvEndereco.getLogradouro() + " Nº: " + farmConvEndereco.getNumero());
							System.out.println("Complemento: " + farmConvEndereco.getComplemento());
							System.out.println("Latitude: " + farmConvEndereco.getLatitude());
							System.out.println("Longitude: " + farmConvEndereco.getLongitude());
							
							getCommonsService().save(farmConvEndereco);
							
						} else {
							System.out.println("NÃO ENCONTRADO !!!");
						}
						
						System.out.println("*********************************************************");
						
					} catch (Exception e) {}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Log getLog() {
		return LOG;
	}


}
