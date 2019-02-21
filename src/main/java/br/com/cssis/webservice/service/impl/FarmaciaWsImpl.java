package br.com.cssis.webservice.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cssis.commons.service.CommonsService;
import br.com.cssis.dominio.cadastro.dto.AddressLocationDTO;
import br.com.cssis.foundation.ExceptionHandler;
import br.com.cssis.foundation.util.LatLong;
import br.com.cssis.foundation.util.MapLinkHelper;
import br.com.cssis.foundation.util.NumberHelper;
import br.com.cssis.foundation.util.SetHelper;
import br.com.cssis.foundation.util.StringHelper;
import br.com.cssis.maplink2.webservice.Address;
import br.com.cssis.maplink2.webservice.AddressLocation;
import br.com.cssis.maplink2.webservice.City;
import br.com.cssis.maplink2.webservice.Point;
import br.com.cssis.webservice.exception.WSException;
import br.com.cssis.webservice.result.AddressLocationResult;
import br.com.cssis.webservice.result.StatusResult;
import br.com.cssis.webservice.result.StatusResult.Status;
import br.com.cssis.webservice.service.FarmaciaWs;

@Service(value = FarmaciaWs.SERVICE_NAME)
@WebService(portName = "FarmaciaPort", serviceName = "FarmaciaService", endpointInterface = "br.com.cssis.webservice.service.FarmaciaWs", targetNamespace = "webservice.cssis.com.br")
public class FarmaciaWsImpl implements FarmaciaWs {

	private static final Log LOG = LogFactory.getLog(FarmaciaWsImpl.class);

	@Autowired
	private CommonsService commonsService;	

	public CommonsService getCommonsService() {
		return commonsService;
	}

	public void setCommonsService(CommonsService commonsService) {
		this.commonsService = commonsService;
	}

	/**
	 * Método responsável por buscar as farmacias informando apenas latitude e longitude. 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	@Override
	public AddressLocationResult getLatLongAddress(Double latitude, Double longitude) {

		AddressLocationResult addressLocationResult = new AddressLocationResult();

		try {

			AddressLocation addressLocation = MapLinkHelper.getAddress(latitude, longitude);

			if(addressLocation == null) {
				throw new WSException(new StatusResult(Status.ALERT));
			}

			AddressLocationDTO addressLocationDTO = new AddressLocationDTO();
			addressLocationDTO.setCep(addressLocation.getAddress().getZip());
			addressLocationDTO.setLogradouro(addressLocation.getAddress().getStreet());
			addressLocationDTO.setBairro(StringHelper.getNonAccentString(StringHelper.toUpperCase(addressLocation.getAddress().getDistrict())));
			addressLocationDTO.setCidade(StringHelper.getNonAccentString(StringHelper.toUpperCase(addressLocation.getAddress().getCity().getName())));				
			addressLocationDTO.setUf(addressLocation.getAddress().getCity().getState());
			
			addressLocationResult.setAddressLocation(addressLocationDTO);

		} catch(WSException ex) {		

			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);
			
		} catch(Exception ex) {

			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);

		}

		return addressLocationResult;
	}

	/**
	 * Método responsável por buscar as farmacias dentro da cidade informado.
	 * @param city
	 * @param uf
	 * @return
	 */
	@WebMethod
	public AddressLocationResult getNomeUfAddress(String city, String uf) {		

		AddressLocationResult addressLocationResult = new AddressLocationResult();

		try {

			LatLong latLong = MapLinkHelper.getCity(city, uf);			

			if(latLong == null) {
				throw new WSException(new StatusResult(Status.ALERT));
			}

			AddressLocationDTO addressLocation = new AddressLocationDTO();
			addressLocation.setLatitude(latLong.getLatitude());
			addressLocation.setLongitude(latLong.getLongitude());
			
			addressLocationResult.setAddressLocation(addressLocation);

		} catch(WSException ex) {		

			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);
			
		} catch(Exception ex) {

			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);

		}

		return addressLocationResult;		
	}

	/**
	 * Método responsável por buscar as farmacias dentro do bairro informado
	 * @param cidade
	 * @param uf
	 * @param bairro
	 * @param usoInterno
	 * @param vinteQuatroHoras
	 * @return
	 */
	@Override
	public AddressLocationResult buscaFarmaciaCidadeEstadoBairro(String cidade, String uf, String bairro, Boolean vinteQuatroHoras) {

		AddressLocationResult addressLocationResult = new AddressLocationResult();

		try {
			
			Map<Object, Object> params = new HashMap<Object, Object>();
			params.put("BAIRRO", StringHelper.isEmpty(bairro) ? null : bairro);
			params.put("CIDADE", StringHelper.isEmpty(cidade) ? null : StringHelper.removeCharacters(cidade));
			params.put("UF", StringHelper.isEmpty(uf) ? null : uf);			
			params.put("USO_INTERNO", "X");
			params.put("VINTEQUATROHORAS", !vinteQuatroHoras ? null : vinteQuatroHoras);

			List<AddressLocationDTO> addressLocations = getCommonsService().executeQueryIbatis("Address.bucarPorBairroCidadeUf", params);
			
			if(SetHelper.isEmpty(addressLocations)) {
				throw new WSException(new StatusResult(Status.ALERT));
			}
						
			addressLocationResult.setAddressLocations(addressLocations);

		} catch(WSException ex) {		

			ex.printStackTrace();
			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);
			
		} catch(Exception ex) {

			ex.printStackTrace();
			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);

		}

		return addressLocationResult;	
	}


	/**
	 * Método responsável por buscar cidades onde contém farmacias
	 * @param uf
	 * @return
	 */
	@Override
	public AddressLocationResult getCidadesUf(String uf) {

		AddressLocationResult addressLocationResult = new AddressLocationResult();

		try {
			
			Map<Object, Object> params = new HashMap<Object, Object>();
			params.put("UF", StringHelper.isEmpty(uf) ? null : uf);	
			
			if(StringHelper.isEmpty(uf)) {
				throw new WSException(new StatusResult("UF é um parametro obrigatório", Status.ALERT));
			}

			List<AddressLocationDTO> addressLocations = getCommonsService().executeQueryIbatis("Address.getCidadesUf", params);
			
			if(SetHelper.isEmpty(addressLocations)) {
				throw new WSException(new StatusResult(Status.ALERT));
			}
						
			addressLocationResult.setAddressLocations(addressLocations);

		} catch(WSException ex) {		

			ex.printStackTrace();
			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);
			
		} catch(Exception ex) {

			ex.printStackTrace();
			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);

		}

		return addressLocationResult;	
	}

	/**
	 * Método responsável por buscar bairros onde contém farmacias
	 * @param cidade
	 * @param uf
	 * @return
	 */
	@Override
	public AddressLocationResult getBairros(String cidade, String uf) {

		AddressLocationResult addressLocationResult = new AddressLocationResult();

		try {
			
			Map<Object, Object> params = new HashMap<Object, Object>();
			params.put("CIDADE", cidade);
			params.put("UF", uf);
			
			if(StringHelper.isEmpty(cidade)) {
				throw new WSException(new StatusResult("Cidade é um parametro obrigatório", Status.ALERT));
			}
			
			if(StringHelper.isEmpty(uf)) {
				throw new WSException(new StatusResult("UF é um parametro obrigatório", Status.ALERT));
			}

			List<AddressLocationDTO> addressLocations = getCommonsService().executeQueryIbatis("Address.getBairros", params);
			
			if(SetHelper.isEmpty(addressLocations)) {
				throw new WSException(new StatusResult(Status.ALERT));
			}
						
			addressLocationResult.setAddressLocations(addressLocations);

		} catch(WSException ex) {		

			ex.printStackTrace();
			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);
			
		} catch(Exception ex) {

			ex.printStackTrace();
			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);

		}

		return addressLocationResult;	
	}

	
	/**
	 * Método responsável por buscar as farmacias na proximidade do endereço informando
	 * @param cidade
	 * @param uf
	 * @param bairro
	 * @param logradouro
	 * @param numero
	 * @param raio
	 * @param vinteQuatroHoras
	 * @return
	 */
	@Override
	public AddressLocationResult buscaFarmaciaProximaEndereco(String cidade, String uf, String bairro, String logradouro, String numero, Integer raio, Boolean vinteQuatroHoras) {
		
		AddressLocationResult addressLocationResult = new AddressLocationResult();

		try {

			if(StringHelper.isEmpty(numero)) {
				throw new WSException(new StatusResult("Numero é um parametro obrigatório", Status.ALERT));
			}
			
			if(StringHelper.isEmpty(logradouro)) {
				throw new WSException(new StatusResult("Logradouro é um parametro obrigatório", Status.ALERT));
			}
			
			if(StringHelper.isEmpty(bairro)) {
				throw new WSException(new StatusResult("Bairro é um parametro obrigatório", Status.ALERT));
			}
			
			if(StringHelper.isEmpty(cidade)) {
				throw new WSException(new StatusResult("Cidade é um parametro obrigatório", Status.ALERT));
			}
			
			if(StringHelper.isEmpty(uf)) {
				throw new WSException(new StatusResult("UF é um parametro obrigatório", Status.ALERT));
			}
			
			if(raio == null) {
				raio = 10;
			}
			
			City city = new City();
			city.setName(cidade.toUpperCase());
			city.setState(uf.toUpperCase());

			Address address = new Address();
			address.setCity(city);
			address.setStreet(logradouro.toUpperCase());
			address.setDistrict(bairro.toUpperCase());
			address.setHouseNumber(numero);

			Point point = MapLinkHelper.getAddress(address);
			
			addressLocationResult = buscaFarmaciaProxima(point.getY(), point.getX(), raio, vinteQuatroHoras);

		} catch(WSException ex) {		

			ex.printStackTrace();
			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);
			
		} catch(Exception ex) {

			ex.printStackTrace();
			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);

		}

		return addressLocationResult;
	}

	/**
	 * Método responsável por buscar as farmacias dentro de um raio informando
	 * @param latitude
	 * @param longitude
	 * @param raio
	 * @param vinteQuatroHoras
	 * @return
	 */
	@Override
	public AddressLocationResult buscaFarmaciaProxima(Double latitude, Double longitude, Integer raio, Boolean vinteQuatroHoras) {
		
		AddressLocationResult addressLocationResult = new AddressLocationResult();

		try {
			
			if(latitude == null) {
				throw new WSException(new StatusResult("Latitude é um parametro obrigatório", Status.ALERT));
			}
			
			if(longitude == null) {
				throw new WSException(new StatusResult("Longitude é um parametro obrigatório", Status.ALERT));
			}
			
			if(raio == null) {
				throw new WSException(new StatusResult("Raio é um parametro obrigatório", Status.ALERT));
			}

			LatLong centro = new LatLong(NumberHelper.getDouble(latitude), NumberHelper.getDouble(longitude));
			LatLong quadrado[] = centro.getQuadradoNormalizado((raio*1000));
			LatLong latLong1 = quadrado[0];
			LatLong latLong2 = quadrado[2];

			Map<Object, Object> params = new HashMap<Object, Object>();
			params.put("latitude2", latLong1.getLatitude());
			params.put("latitude1", latLong2.getLatitude());			
			params.put("longitude2", latLong1.getLongitude());
			params.put("longitude1", latLong2.getLongitude());	
			params.put("USO_INTERNO", "X");
			params.put("VINTEQUATROHORAS", !vinteQuatroHoras ? null : vinteQuatroHoras);		
			
			List<AddressLocationDTO> addressLocations = getCommonsService().executeQueryIbatis("Address.buscaFarmaciaProxima", params);
			
			if(SetHelper.isEmpty(addressLocations)) {
				throw new WSException(new StatusResult(Status.ALERT));
			}
						
			addressLocationResult.setAddressLocations(addressLocations);

		} catch(WSException ex) {		

			ex.printStackTrace();
			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);
			
		} catch(Exception ex) {

			ex.printStackTrace();
			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);

		}

		return addressLocationResult;
	}

	/**
	 * Método responsável por buscar farmacias dentro de um cep informado
	 * @param cep
	 * @param vinteQuatroHoras
	 * @return
	 */
	@Override
	public AddressLocationResult buscaFarmaciaCep(String cep, Boolean vinteQuatroHoras) {

		AddressLocationResult addressLocationResult = new AddressLocationResult();

		try {

			int raio = 1;
			
			LatLong latLong = MapLinkHelper.getCep(cep);
			
			if(latLong == null) {
				throw new WSException(new StatusResult(Status.ALERT));
			}	
			
			Double latitude = latLong.getLatitude(); 
			Double longitude = latLong.getLongitude();
						
			while (SetHelper.isEmpty(addressLocationResult.getAddressLocations()) && raio <= 10) {
				
				addressLocationResult = buscaFarmaciaProxima(latitude, longitude, raio, vinteQuatroHoras);
				
				raio *= 2;
			}
			
			if(SetHelper.isEmpty(addressLocationResult.getAddressLocations())) {
				throw new WSException(new StatusResult(Status.ALERT));
			}		
			
		} catch(WSException ex) {		

			ex.printStackTrace();
			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);
			
		} catch(Exception ex) {

			ex.printStackTrace();
			LOG.debug("****************************************************");
			LOG.debug(ex.getMessage());
			LOG.debug("****************************************************");		
			addressLocationResult = (AddressLocationResult) ExceptionHandler.getInstance().anexarErrosResult(AddressLocationResult.class, ex);

		}

		return addressLocationResult;	
	}		
}