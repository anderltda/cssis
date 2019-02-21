package br.com.cssis.webservice.result;

public class StatusResult extends BaseDTO {

	private static final long serialVersionUID = -8467184755403619089L;
	private Status status = Status.SUCESSS;
	private String message;

	public enum Status {
		
		SUCESSS(200), ERROR(404), ALERT(500);
		
		private int value;
		
		private Status(int value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public int getValue() {
			return value;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(int value) {
			this.value = value;
		}		
	}
	
	public StatusResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StatusResult(String message, Status status) {
		super();
		this.status = status;
		this.message = message;
	}
	
	public StatusResult(Status status) {
		super();
		this.status = status;
		this.message = "Nenhum registro encontrado";
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
