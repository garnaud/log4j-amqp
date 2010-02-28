import java.io.IOException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConnectionParameters;
import com.rabbitmq.client.GetResponse;

public class AMQAppender extends AppenderSkeleton {

	@Override
	protected void append(LoggingEvent event) {

		ConnectionParameters params = new ConnectionParameters();
		// params.setUsername("");
		// params.setPassword("");
		// params.setVirtualHost("");
		// params.setRequestedHeartbeat(0);
		ConnectionFactory factory = new ConnectionFactory(params);
		Connection conn = null;
		try {
			conn = factory.newConnection("localhost", 5672);
			Channel channel = conn.createChannel();

			channel.exchangeDeclare("myexchange", "direct");
			channel.queueDeclare("log.myappli");
			channel.queueBind("log.myappli", "myexchange", "log.key");

			channel.basicPublish("exchange", "log.key", null, ((String) event
					.getMessage()).getBytes());
			System.out.println(event.getMessage());

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static final void main(String[] args) {

		ConnectionParameters params = new ConnectionParameters();
		// params.setUsername("");
		// params.setPassword("");
		// params.setVirtualHost("");
		// params.setRequestedHeartbeat(0);
		ConnectionFactory factory = new ConnectionFactory(params);
		try {
			Connection conn = factory.newConnection("localhost", 5672);
			Channel channel = conn.createChannel();

			boolean noAck = false;
			GetResponse response = channel.basicGet("log.myappli", noAck);
			if (response == null) {
				System.out.println("rien trouvé");
			} else {
				AMQP.BasicProperties props = response.getProps();
				byte[] body = response.getBody();
				long deliveryTag = response.getEnvelope().getDeliveryTag();
				System.out.println(body);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

}
