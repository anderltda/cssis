package br.com.cssis.maplink2.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import br.com.cssis.maplink2.webservice.Address;
import br.com.cssis.maplink2.webservice.AddressInfo;
import br.com.cssis.maplink2.webservice.AddressLocation;
import br.com.cssis.maplink2.webservice.AddressOptions;
import br.com.cssis.maplink2.webservice.City;
import br.com.cssis.maplink2.webservice.CityLocationInfo;
import br.com.cssis.maplink2.webservice.District;
import br.com.cssis.maplink2.webservice.DistrictInfo;
import br.com.cssis.maplink2.webservice.MapInfo;
import br.com.cssis.maplink2.webservice.MapOptions;
import br.com.cssis.maplink2.webservice.POIInfo;
import br.com.cssis.maplink2.webservice.Point;
import br.com.cssis.maplink2.webservice.ResultRange;
import br.com.cssis.maplink2.webservice.Road;
import br.com.cssis.maplink2.webservice.RoadInfo;

@WebService(name = "AddressFinderSoap", targetNamespace = "http://webservices.maplink2.com.br")
@SOAPBinding(use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface AddressFinderSoap {

	@WebMethod(operationName = "findAddress", action = "http://webservices.maplink2.com.br/findAddress")
	@WebResult(name = "findAddressResult", targetNamespace = "http://webservices.maplink2.com.br")
	public AddressInfo findAddress(@WebParam(name = "address", targetNamespace = "http://webservices.maplink2.com.br")
	Address address, @WebParam(name = "ao", targetNamespace = "http://webservices.maplink2.com.br")
	AddressOptions ao, @WebParam(name = "token", targetNamespace = "http://webservices.maplink2.com.br")
	String token);

	@WebMethod(operationName = "findRoad", action = "http://webservices.maplink2.com.br/findRoad")
	@WebResult(name = "findRoadResult", targetNamespace = "http://webservices.maplink2.com.br")
	public RoadInfo findRoad(@WebParam(name = "road", targetNamespace = "http://webservices.maplink2.com.br")
	Road road, @WebParam(name = "token", targetNamespace = "http://webservices.maplink2.com.br")
	String token);

	@WebMethod(operationName = "getRoadXY", action = "http://webservices.maplink2.com.br/getRoadXY")
	@WebResult(name = "getRoadXYResult", targetNamespace = "http://webservices.maplink2.com.br")
	public Point getRoadXY(@WebParam(name = "road", targetNamespace = "http://webservices.maplink2.com.br")
	Road road, @WebParam(name = "token", targetNamespace = "http://webservices.maplink2.com.br")
	String token);

	@WebMethod(operationName = "getXYRadiusWithMap", action = "http://webservices.maplink2.com.br/getXYRadiusWithMap")
	@WebResult(name = "getXYRadiusWithMapResult", targetNamespace = "http://webservices.maplink2.com.br")
	public MapInfo getXYRadiusWithMap(@WebParam(name = "address", targetNamespace = "http://webservices.maplink2.com.br")
	Address address, @WebParam(name = "mo", targetNamespace = "http://webservices.maplink2.com.br")
	MapOptions mo, @WebParam(name = "radius", targetNamespace = "http://webservices.maplink2.com.br")
	int radius, @WebParam(name = "token", targetNamespace = "http://webservices.maplink2.com.br")
	String token);

	@WebMethod(operationName = "getXY", action = "http://webservices.maplink2.com.br/getXY")
	@WebResult(name = "getXYResult", targetNamespace = "http://webservices.maplink2.com.br")
	public Point getXY(@WebParam(name = "address", targetNamespace = "http://webservices.maplink2.com.br")
	Address address, @WebParam(name = "token", targetNamespace = "http://webservices.maplink2.com.br")
	String token);

	@WebMethod(operationName = "findCity", action = "http://webservices.maplink2.com.br/findCity")
	@WebResult(name = "findCityResult", targetNamespace = "http://webservices.maplink2.com.br")
	public CityLocationInfo findCity(@WebParam(name = "cidade", targetNamespace = "http://webservices.maplink2.com.br")
	City cidade, @WebParam(name = "ao", targetNamespace = "http://webservices.maplink2.com.br")
	AddressOptions ao, @WebParam(name = "token", targetNamespace = "http://webservices.maplink2.com.br")
	String token);

	@WebMethod(operationName = "findPOI", action = "http://webservices.maplink2.com.br/findPOI")
	@WebResult(name = "findPOIResult", targetNamespace = "http://webservices.maplink2.com.br")
	public POIInfo findPOI(@WebParam(name = "name", targetNamespace = "http://webservices.maplink2.com.br")
	String name, @WebParam(name = "city", targetNamespace = "http://webservices.maplink2.com.br")
	City city, @WebParam(name = "resultRange", targetNamespace = "http://webservices.maplink2.com.br")
	ResultRange resultRange, @WebParam(name = "token", targetNamespace = "http://webservices.maplink2.com.br")
	String token);

	@WebMethod(operationName = "findDistrict", action = "http://webservices.maplink2.com.br/findDistrict")
	@WebResult(name = "findDistrictResult", targetNamespace = "http://webservices.maplink2.com.br")
	public DistrictInfo findDistrict(@WebParam(name = "district", targetNamespace = "http://webservices.maplink2.com.br")
	District district, @WebParam(name = "rr", targetNamespace = "http://webservices.maplink2.com.br")
	ResultRange rr, @WebParam(name = "token", targetNamespace = "http://webservices.maplink2.com.br")
	String token);

	@WebMethod(operationName = "getAddress", action = "http://webservices.maplink2.com.br/getAddress")
	@WebResult(name = "getAddressResult", targetNamespace = "http://webservices.maplink2.com.br")
	public AddressLocation getAddress(@WebParam(name = "point", targetNamespace = "http://webservices.maplink2.com.br")
	Point point, @WebParam(name = "token", targetNamespace = "http://webservices.maplink2.com.br")
	String token, @WebParam(name = "tolerance", targetNamespace = "http://webservices.maplink2.com.br")
	double tolerance);

}
