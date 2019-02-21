package br.com.cssis.foundation.util;

import java.util.HashMap;
import java.util.Map;

import br.com.cssis.commons.service.CommonsService;
import br.com.cssis.dominio.cadastro.entity.FarmConvEndereco;
import br.com.cssis.dominio.cadastro.entity.Localidade;
import br.com.cssis.foundation.ExceptionHandler;
import br.com.cssis.foundation.service.ServiceException;
import br.com.cssis.maplink2.client.AddressFinderSoap;
import br.com.cssis.maplink2.client.MaplinkService;
import br.com.cssis.maplink2.webservice.Address;
import br.com.cssis.maplink2.webservice.AddressLocation;
import br.com.cssis.maplink2.webservice.AddressOptions;
import br.com.cssis.maplink2.webservice.City;
import br.com.cssis.maplink2.webservice.CityLocation;
import br.com.cssis.maplink2.webservice.CityLocationInfo;
import br.com.cssis.maplink2.webservice.Point;

public class MapLinkHelper {
	private static AddressOptions addressOptions;
	private static String mapLinkToken;
	private static AddressFinderSoap mapLinkService;
	static {
		addressOptions = new AddressOptions();
		addressOptions.setUsePhonetic(true);
		addressOptions.setSearchType(1);
		mapLinkToken = MaplinkService.getInstance().getToken();
		mapLinkService = MaplinkService.getInstance().getAddressService();
	}

	public static final LatLong getXYLogradouro(CommonsService service, Long idLocalidade, String logradouro,
			String numero, String cep) throws ServiceException {
		Address address = new Address();
		City city = new City();

		Localidade localidade = service.findById(Localidade.class, idLocalidade);

		city.setName(localidade.getNome());
		city.setState(localidade.getUf());
		address.setCity(city);
		address.setHouseNumber(numero);
		address.setStreet(logradouro);
		if (StringHelper.nonEmptyTrim(cep))
			address.setZip(cep);

		try {
			Point xy = MaplinkService.getInstance().getAddressService().getXY(address, mapLinkToken);
			return new LatLong(xy.getY(), xy.getX());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Tenta Localizar tirando a cidade que esta entre parenteses
		try {
			final String cidade = localidade.getNome().substring(localidade.getNome().indexOf("(") + 1,
					localidade.getNome().indexOf(")"));
			City c = new City();
			c.setName(cidade);
			c.setState(localidade.getUf());
			try {
				CityLocationInfo cityL = mapLinkService.findCity(c, addressOptions, mapLinkToken);
				if (cityL.getRecordCount() > 0) {
					CityLocation cl = cityL.getCityLocation().getCityLocation().get(0);
					return new LatLong(cl.getPoint().getY(), cl.getPoint().getX());
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Tenta localizar pela cidade
		try {
			return getXYLocalidade(localidade.getNome(), localidade.getUf(), localidade.getNomeSub());
		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}

	}

	public static final LatLong getXYLocalidade(String nomeLocalidade, String uf, String nomeSub)
			throws ServiceException {
		LatLong returnValue = null;
		if (StringHelper.nonEmpty(nomeSub)) {
			returnValue = getCity(nomeSub, uf);
		}
		if (returnValue != null)
			return returnValue;
		else
			return getCity(nomeLocalidade, uf);
	}

	public static final LatLong getCity(String nomeCidade, String uf) {
		City c = new City();
		c.setName(nomeCidade.replaceAll("'", "`"));
		c.setState(uf);
		try {
			CityLocationInfo cityL = mapLinkService.findCity(c, addressOptions, mapLinkToken);
			if (cityL.getRecordCount() > 0) {
				CityLocation cl = cityL.getCityLocation().getCityLocation().get(0);
				return new LatLong(cl.getPoint().getY(), cl.getPoint().getX());
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static final AddressLocation getAddress(Double latitude, Double longitude) throws ServiceException {

		AddressLocation addressLocation = null;

		try {

			Point point = new Point();
			point.setY(latitude);
			point.setX(longitude);

			addressLocation = MaplinkService.getInstance().getAddressService().getAddress(point, mapLinkToken, 0);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, ex);
		}

		return addressLocation;
	}

	public static final Point getAddress(FarmConvEndereco farmConvEndereco) throws ServiceException {

		Point point = null;

		try {

			String logradouro_ = qualificarLogradouro(farmConvEndereco.getLogradouro());

			String logradouro = logradouro_.substring(0, logradouro_.indexOf(",")).replaceAll("[-)\\\\/,?!%$¨@&>:#(_+=^*]|\\d", "").replace("R. ", "RUA").replace("AV. ", "AVENIDA");

			String numero = qualificarNumero(logradouro_.substring(logradouro_.indexOf(",")));

			String numero_ = qualificarBuscarNumero(numero);

			String complemento = logradouro_.substring(logradouro_.indexOf(",")).replaceAll("[^A-Z]\\s|\\d", "");

			farmConvEndereco.setLogradouro(logradouro.replaceAll("x?AVENIDA\\s+|x?RUA\\s+|x?PRACA\\s+|x?RODOVIA\\s+|x?ESTRADA\\s+|x?TRAVESSA\\s+|x?LARGO\\s+", ""));

			farmConvEndereco.setNumero(numero);

			if (StringHelper.nonEmpty(complemento)) {
				farmConvEndereco.setComplemento(StringHelper.removeCharacter(complemento));
			}

			City city = new City();
			city.setName(farmConvEndereco.getLocalidade().getNomePesquisa());
			city.setState(farmConvEndereco.getLocalidade().getUf());

			Address address = new Address();
			address.setCity(city);
			address.setZip(farmConvEndereco.getCep());
			address.setStreet(logradouro);
			address.setDistrict(farmConvEndereco.getBairro_());
			address.setHouseNumber(numero_);

			point = MaplinkService.getInstance().getAddressService().getXY(address, mapLinkToken);

		} catch (Exception ex) {
			point = null;
			// ex.printStackTrace();
			// throw
			// ExceptionHandler.getInstance().generateException(ServiceException.class,
			// ex);
		}

		return point;
	}

	public static final String qualificarLogradouro(String value) {

		Map<String, String> maps = new HashMap<String, String>();
		maps.put("NS.", "NOSSA SENHORA ");
		maps.put("R.", "");
		maps.put("AV.", "");
		maps.put("DR.", "DOUTOR ");
		maps.put("DR", "DOUTOR ");
		maps.put("GAL.", "GENERAL ");
		maps.put("GOV", "GOVERNADOR ");
		maps.put("GOV.", "GOVERNADOR ");
		maps.put("MAL", "MARECHAL ");
		maps.put("MAL.", "MARECHAL ");
		maps.put("CEL.", "CORONEL ");
		maps.put("CEL", "CORONEL ");
		maps.put("PRES.", "PRESIDENTE ");
		maps.put("PRES", "PRESIDENTE ");
		maps.put("BRIG.", "BRIGADEIRO ");
		maps.put("BRIG", "BRIGADEIRO ");
		maps.put("SCO", "SAO ");

		String retorno[] = value.split(" ");

		String concat = "";

		for (String word : retorno) {
			concat += maps.get(word) != null ? maps.get(word) : word + " ";
		}

		String regex = "[)\\\\?.!%$:#(+=^*]*";

		return concat.replaceAll(regex, "");
	}

	public static final String qualificarNumero(String value) {

		String concat = StringHelper.fullTrim(value.substring(1)).replaceAll("[^-, E/0-9]", "").replaceAll("[-, E/]", "#").replaceAll("\\W+", "#").replace("#", "/");

		concat = (concat.indexOf("/") == 0 ? concat.substring(1, concat.length()) : concat);

		concat = (concat.lastIndexOf("/") == concat.length()-1 ? concat.substring(0, concat.length()-1) : concat);

		return concat;
	}

	public static final String qualificarBuscarNumero(String value) {

		String numero_ = null;

		if (StringHelper.nonEmpty(value)) {

			String split[] = value.split("/");

			if (SetHelper.isEmpty(split)) {
				numero_ = "S/N";
				value = numero_;
			} else {
				numero_ = split[0];
			}

		} else {
			numero_ = "S/N";
			value = numero_;
		}			

		return numero_;
	}

	public static final Point getAddress(Address address) throws ServiceException {
		
		Point point = null;

		try {

			point = MaplinkService.getInstance().getAddressService().getXY(address, mapLinkToken);

		} catch (Exception ex) {
			point = null;
			ex.printStackTrace();
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, ex);
		}
		
		return point;
	}
	
	public static final LatLong getCep(String cep) throws ServiceException {
	
		LatLong latLong = null;
		Address address = new Address();
		address.setZip(cep);

		try {
			
			Point xy = MaplinkService.getInstance().getAddressService().getXY(address, mapLinkToken);
			
			latLong = new LatLong(xy.getY(), xy.getX());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return latLong;
	}

	public static void main(String[] args) throws ServiceException {
		// TODO Auto-generated method stub

		/*
		 * String logradouro_ =
		 * qualificarLogradouro("RUA BERNARDINO DE CAMPOS, 510 524");
		 * System.out.println(qualificarNumero(logradouro_.substring(logradouro_
		 * .indexOf(","))));
		 */
		
		System.out.println(getCep("02474130"));

/*		String logradouro_ = qualificarLogradouro("AVENIDA GUARULHOS, 3810 :3882; - PONTE GRANDE");

		String logradouro = logradouro_.substring(0, logradouro_.indexOf(",")).replaceAll("[-)\\\\/,?!%$¨@&>:#(_+=^*]|\\d", "").replace("R. ", "RUA").replace("AV. ", "AVENIDA");

		String numero = qualificarNumero(logradouro_.substring(logradouro_.indexOf(",")));

		String numero_ = qualificarBuscarNumero(numero);

		String complemento = logradouro_.substring(logradouro_.indexOf(",")).replaceAll("[^A-Z]\\s|\\d", "");

		System.out.println("Logradouro " + logradouro + " Nº " + numero);
		System.out.println("FOR_Nº " + numero_);
		System.out.println("Complemento " + StringHelper.removeCharacters(complemento));*/

	}
}
