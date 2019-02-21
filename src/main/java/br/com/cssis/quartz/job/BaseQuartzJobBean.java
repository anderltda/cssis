package br.com.cssis.quartz.job;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import br.com.cssis.commons.service.CommonsService;
import br.com.cssis.foundation.service.ServiceException;
import br.com.cssis.foundation.util.ClusterUtil;

public abstract class BaseQuartzJobBean extends QuartzJobBean {

	private CommonsService commonsService;
	private List<String> clustersUnitsIds;

	public abstract Log getLog();

	public abstract void executeIssoSeLiberado(JobExecutionContext context) throws ServiceException;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		
		try {
			
			if (!isPermitidoProcessamentoCluster()) {
				return;
			}
			
			executeIssoSeLiberado(context);

		} catch (Exception e) {
			getLog().error(e);
		} 
	}
	
	private boolean isPermitidoProcessamentoCluster() {
		
		if (CollectionUtils.isEmpty(clustersUnitsIds)) {
			return true;
		}
		
		String clusterUnitId = ClusterUtil.getClusterUnitId();
		
		if (StringUtils.isEmpty(clusterUnitId)) {
			getLog().debug("Permissão de processamento negada. Servidor sem configuração de clusterUnitId. Liberados:" + clustersUnitsIds);
			return false;
		}
		
		if (clustersUnitsIds.contains(clusterUnitId)) {
			return true;
		}
		
		getLog().debug("Permissão de processamento negada para o Cluster:" + clusterUnitId + ". Liberados:" + clustersUnitsIds);
		
		return false;
	}

	public String getMsgLogInicioJob() {
		return "Job Iniciado";
	}

	public String getMsgLogFinalJob() {
		return "Job Finalizado";
	}

	public CommonsService getCommonsService() {
		return commonsService;
	}

	public void setCommonsService(CommonsService commonsService) {
		this.commonsService = commonsService;
	}	
}
