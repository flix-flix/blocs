package environment;

public interface Client {

	public void send(Object obj);

	public void receive(Object obj);

	public default void exception(Exception e) {
		e.printStackTrace();
	}
}
