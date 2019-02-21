package br.com.cssis.webservice.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import br.com.cssis.webservice.result.AddressLocationResult;

@WebService
public interface FarmaciaWs {
	
	public static final String SERVICE_NAME = "farmaciaWs";
	
	/**
	 * M�todo respons�vel por buscar as farmacias informando apenas latitude e longitude. 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	@WebMethod
	public AddressLocationResult getLatLongAddress(
			@WebParam(name = "latitude") Double latitude, 
			@WebParam(name = "longitude") Double longitude);
	
	/**
	 * M�todo respons�vel por buscar as farmacias dentro da cidade informado.
	 * @param city
	 * @param uf
	 * @return
	 */
	@WebMethod
	public AddressLocationResult getNomeUfAddress(
			@WebParam(name = "city") String city, 
			@WebParam(name = "uf") String uf);

	/**
	 * M�todo respons�vel por buscar as farmacias dentro do bairro informado
	 * @param cidade
	 * @param uf
	 * @param bairro
	 * @param vinteQuatroHoras
	 * @return
	 */
	@WebMethod
	public AddressLocationResult buscaFarmaciaCidadeEstadoBairro(
			@WebParam(name = "cidade") String cidade,
			@WebParam(name = "uf") String uf, 
			@WebParam(name = "bairro") String bairro,
			@WebParam(name = "vinteQuatroHoras") Boolean vinteQuatroHoras);
		
	/**
	 * M�todo respons�vel por buscar as farmacias na proximidade do endere�o informando
	 * @param cidade
	 * @param uf
	 * @param bairro
	 * @param logradouro
	 * @param numero
	 * @param raio
	 * @param vinteQuatroHoras
	 * @return
	 */
	@WebMethod
	public AddressLocationResult buscaFarmaciaProximaEndereco(
			@WebParam(name = "cidade") String cidade,
			@WebParam(name = "uf") String uf, 
			@WebParam(name = "bairro") String bairro,
			@WebParam(name = "logradouro") String logradouro, 
			@WebParam(name = "numero") String numero,
			@WebParam(name = "raio") Integer raio, 
			@WebParam(name = "vinteQuatroHoras") Boolean vinteQuatroHoras);
	
	/**
	 * M�todo respons�vel por buscar as farmacias dentro de um raio informando
	 * @param latitude
	 * @param longitude
	 * @param raio
	 * @param vinteQuatroHoras
	 * @return
	 */
	@WebMethod
	public AddressLocationResult buscaFarmaciaProxima(
			@WebParam(name = "latitude") Double latitude,
			@WebParam(name = "longitude") Double longitude,
			@WebParam(name = "raio") Integer raio,
			@WebParam(name = "vinteQuatroHoras") Boolean vinteQuatroHoras);
		
	/**
	 * M�todo respons�vel por buscar bairros onde cont�m farmacias
	 * @param cidade
	 * @param uf
	 * @return
	 */
	@WebMethod
	public AddressLocationResult getBairros(
			@WebParam(name = "cidade") String cidade, 
			@WebParam(name = "uf") String uf);

	/**
	 * M�todo respons�vel por buscar cidades onde cont�m farmacias
	 * @param uf
	 * @return
	 */
	@WebMethod
	public AddressLocationResult getCidadesUf(
			@WebParam(name = "uf") String uf);	

	/**
	 * M�todo respons�vel por buscar farmacias dentro de um cep informado
	 * @param cep
	 * @param vinteQuatroHoras
	 * @return
	 */
	@WebMethod
	public AddressLocationResult buscaFarmaciaCep(
			@WebParam(name = "cep") String cep, 
			@WebParam(name = "vinteQuatroHoras") Boolean vinteQuatroHoras);
}
