package br.com.cssis.webservice.result;

import java.util.List;

import br.com.cssis.dominio.cadastro.dto.AddressLocationDTO;

/**
 * @author anderson.nascimento
 *
 */
public class AddressLocationResult extends StatusResult {

	private static final long serialVersionUID = 1L;

	private AddressLocationDTO addressLocation;
	private List<AddressLocationDTO> addressLocations;
	
	public AddressLocationDTO getAddressLocation() {
		return addressLocation;
	}
	
	public void setAddressLocation(AddressLocationDTO addressLocation) {
		this.addressLocation = addressLocation;
	}
	
	public List<AddressLocationDTO> getAddressLocations() {
		return addressLocations;
	}
	
	public void setAddressLocations(List<AddressLocationDTO> addressLocations) {
		this.addressLocations = addressLocations;
	}	
}