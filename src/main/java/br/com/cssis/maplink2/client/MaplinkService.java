package br.com.cssis.maplink2.client;

import java.io.Serializable;

import br.com.cssis.foundation.Constants;
import br.com.cssis.foundation.config.FrameworkConfiguration;
import br.com.cssis.foundation.util.StringHelper;

public class MaplinkService implements Serializable {
	private static final long serialVersionUID = 1L;
	private static MaplinkService instance = new MaplinkService();
	private String maplinkAddressFinderURL;
	private String maplinkAddressFinderToken;

	private MaplinkService() {
		restart();
	}

	private void restart() {
		maplinkAddressFinderURL = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_MAPLINK_ADDRESSFINDER_URL);
		maplinkAddressFinderToken = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_MAPLINK_ADDRESSFINDER_TOKEN);
	}

	public static MaplinkService getInstance() {
		return instance;
	}

	public String getMaplinkAddressFinderURL() {
		return maplinkAddressFinderURL;
	}

	public void setMaplinkAddressFinderURL(String maplinkAddressFinderURL) {
		if (StringHelper.nonEmpty(maplinkAddressFinderURL)) {
			this.maplinkAddressFinderURL = maplinkAddressFinderURL;
		}
	}

	public String getMaplinkAddressFinderToken() {
		return maplinkAddressFinderToken;
	}

	public String getToken() {
		return maplinkAddressFinderToken;
	}

	public void setMaplinkAddressFinderToken(String maplinkAddressFinderToken) {
		this.maplinkAddressFinderToken = maplinkAddressFinderToken;
	}

	public static void main(String[] args) {
		System.out.println(MaplinkService.getInstance().getMaplinkAddressFinderToken());
		System.out.println(MaplinkService.getInstance().getMaplinkAddressFinderURL());
	}

	public AddressFinderSoap getAddressService() {
		AddressFinderClient client = new AddressFinderClient(getMaplinkAddressFinderURL());
		AddressFinderSoap service = client.getAddressFinderSoap();
		return service;
	}

}
