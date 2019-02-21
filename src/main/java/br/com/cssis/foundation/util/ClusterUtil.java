package br.com.cssis.foundation.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClusterUtil {
	public static final Log LOG = LogFactory.getLog(ClusterUtil.class);

	private static String clusterUnitId = null;

	private static final String NOME_VAR_CLUSTER_UNIT = "clusterUnitId";

	public static final String getClusterUnitId() {
		if (clusterUnitId == null) {
			try {
				Context initCtx = new InitialContext();
				Context envCtx = (Context) initCtx.lookup("java:comp/env");
				clusterUnitId = (String) envCtx.lookup(NOME_VAR_CLUSTER_UNIT);
			} catch (NameNotFoundException e) {
				LOG.warn("[" + NOME_VAR_CLUSTER_UNIT + "] não foi configurado no Context.");
			} catch (NamingException e) {
				LOG.error("Erro ao tentar fazer lookup de [" + NOME_VAR_CLUSTER_UNIT + "]", e);
			}
		}
		return clusterUnitId;
	}
}
