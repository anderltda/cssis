package br.com.cssis.foundation.util;

public class LatLong {
	public static final double RAIO_BASE_TERRA = 6378137d;
	private double latitude;
	private double longitude;

	private double latitudeRadianos;
	private double longitudeRadianos;	

	public LatLong(double latitude, double longitude) {
		super();
		setLatitude(latitude);
		setLongitude(longitude);
	}
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
		latitudeRadianos = Math.toRadians(latitude);
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
		longitudeRadianos = Math.toRadians(longitude);
	}
	
	public double getLatitudeRadianos() {
		return latitudeRadianos;
	}

	public void setLatitudeRadianos(double latitudeRadianos) {
		this.latitudeRadianos = latitudeRadianos;
		latitude = Math.toDegrees(latitudeRadianos);
	}

	public double getLongitudeRadianos() {
		return longitudeRadianos;
	}

	public void setLongitudeRadianos(double longitudeRadianos) {
		this.longitudeRadianos = longitudeRadianos;
		longitude = Math.toDegrees(longitudeRadianos);
	}

	@Override
	public String toString() {
		return "(" + this.latitude + ", " + this.longitude + ")";
	}

	public LatLong[] getQuadrado(int squareSizeInMeter) {
		return getPoligono(4, squareSizeInMeter);
	}
	
		
	public LatLong[] getQuadradoNormalizado(int squareSizeInMeter) {
		LatLong[] poligono4 = getPoligono(4, squareSizeInMeter);
		LatLong p1 = new LatLong(poligono4[3].getLatitude(), poligono4[0].getLongitude());
		LatLong p2 = new LatLong(poligono4[1].getLatitude(), poligono4[0].getLongitude());
		LatLong p3 = new LatLong(poligono4[1].getLatitude(), poligono4[2].getLongitude());
		LatLong p4 = new LatLong(poligono4[3].getLatitude(), poligono4[2].getLongitude());
		return new LatLong[] { p1, p2, p3, p4 };
	}
	
	public LatLong[] getPoligono(int lados, int tamanho) {
		LatLong[] poligono = new LatLong[lados];

		double distanciaEmRadianos;
		double raio, latEmRadianos, distanciaLongRadianos, longitudeEmRadianos, deltaAngulo;

		distanciaEmRadianos = (tamanho/2) / RAIO_BASE_TERRA;

		deltaAngulo = 360 / (double) lados;
		for (int i = 1; i <= lados; i++) {
			raio = Math.toRadians((double) i * deltaAngulo);

			latEmRadianos = Math.asin(Math.sin(latitudeRadianos) * Math.cos(distanciaEmRadianos) + Math.cos(latitudeRadianos) * Math.sin(distanciaEmRadianos) * Math.cos(raio));
			distanciaLongRadianos = Math.atan2(Math.sin(raio) * Math.sin(distanciaEmRadianos) * Math.cos(latitudeRadianos), Math.cos(distanciaEmRadianos) - Math.sin(latitudeRadianos) * Math.sin(latEmRadianos));
			longitudeEmRadianos = ((longitudeRadianos + distanciaLongRadianos + Math.PI) % (2 * Math.PI)) - Math.PI;

			LatLong aresta = new LatLong(Math.toDegrees(latEmRadianos), Math.toDegrees(longitudeEmRadianos));
			poligono[i-1] = aresta;
		}
		return poligono;
	}

}
