package br.com.cssis.webservice.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import br.com.cssis.webservice.result.AddressLocationResult;

@WebService
public interface FarmaciaConveniadaWs {
	
	public static final String SERVICE_NAME = "farmaciaConveniadaWs";
	
	/**
	 * Método responsável por buscar as farmacias informando apenas latitude e longitude. 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	@WebMethod
	public AddressLocationResult getLatLongAddress(
			@WebParam(name = "latitude") Double latitude, 
			@WebParam(name = "longitude") Double longitude);
	
	/**
	 * Método responsável por buscar as farmacias dentro da cidade informado.
	 * @param city
	 * @param uf
	 * @return
	 */
	@WebMethod
	public AddressLocationResult getNomeUfAddress(
			@WebParam(name = "city") String city, 
			@WebParam(name = "uf") String uf);

	/**
	 * Método responsável por buscar as farmacias dentro do bairro informado
	 * @param cidade
	 * @param uf
	 * @param bairro
	 * @param rede
	 * @param vinteQuatroHoras
	 * @return
	 */
	@WebMethod
	public AddressLocationResult buscaFarmaciaConveniadaCidadeEstadoBairro(
			@WebParam(name = "cidade") String cidade,
			@WebParam(name = "uf") String uf, 
			@WebParam(name = "bairro") String bairro,
			@WebParam(name = "rede") String rede, 
			@WebParam(name = "vinteQuatroHoras") Boolean vinteQuatroHoras);
		
	/**
	 * Método responsável por buscar as farmacias na proximidade do endereço informando
	 * @param cidade
	 * @param uf
	 * @param bairro
	 * @param logradouro
	 * @param numero
	 * @param raio
	 * @param rede
	 * @param vinteQuatroHoras
	 * @return
	 */
	@WebMethod
	public AddressLocationResult buscaFarmaciaConveniadaProximaEndereco(
			@WebParam(name = "cidade") String cidade,
			@WebParam(name = "uf") String uf, 
			@WebParam(name = "bairro") String bairro,
			@WebParam(name = "logradouro") String logradouro, 
			@WebParam(name = "numero") String numero,
			@WebParam(name = "raio") Integer raio, 
			@WebParam(name = "rede") String rede, 
			@WebParam(name = "vinteQuatroHoras") Boolean vinteQuatroHoras);
	
	/**
	 * Método responsável por buscar as farmacias dentro de um raio informando
	 * @param latitude
	 * @param longitude
	 * @param raio
	 * @param rede
	 * @param vinteQuatroHoras
	 * @return
	 */
	@WebMethod
	public AddressLocationResult buscaFarmaciaConveniadaProxima(
			@WebParam(name = "latitude") Double latitude,
			@WebParam(name = "longitude") Double longitude,
			@WebParam(name = "raio") Integer raio,
			@WebParam(name = "rede") String rede, 
			@WebParam(name = "vinteQuatroHoras") Boolean vinteQuatroHoras);
		
	/**
	 * Método responsável por buscar bairros onde contém farmacias
	 * @param cidade
	 * @param uf
	 * @return
	 */
	@WebMethod
	public AddressLocationResult getBairros(
			@WebParam(name = "cidade") String cidade, 
			@WebParam(name = "uf") String uf);

	/**
	 * Método responsável por buscar cidades onde contém farmacias
	 * @param uf
	 * @return
	 */
	@WebMethod
	public AddressLocationResult getCidadesUf(
			@WebParam(name = "uf") String uf);	

	/**
	 * Método responsável por buscar farmacias dentro de um cep informado
	 * @param cep
	 * @param rede
	 * @param vinteQuatroHoras
	 * @return
	 */
	@WebMethod
	public AddressLocationResult buscaFarmaciaConveniadaCep(
			@WebParam(name = "cep") String cep, 
			@WebParam(name = "rede") String rede, 
			@WebParam(name = "vinteQuatroHoras") Boolean vinteQuatroHoras);
}
