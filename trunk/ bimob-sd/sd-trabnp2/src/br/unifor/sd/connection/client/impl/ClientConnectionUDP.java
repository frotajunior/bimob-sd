package br.unifor.sd.connection.client.impl;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import br.unifor.sd.connection.UtilConnection;
import br.unifor.sd.connection.client.ClientConnection;
import br.unifor.sd.connection.listener.ClientConnectionListener;
import br.unifor.sd.connection.listener.ConnectionEvent;

// TODO Alterar para utilizar DatagramSocket e DatagramPacket
public class ClientConnectionUDP implements ClientConnection{

	private static ClientConnectionUDP instance;
	
	private static final String HOST = "localhost";
	private static final int PORT = 555;
	private InetAddress address;
	private DatagramSocket serverSocket;
	
	private ClientConnectionUDP() {
		super();
		try {
			address =  InetAddress.getByName(HOST);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public static ClientConnectionUDP getInstance() {
		if (instance == null) {
			instance = new ClientConnectionUDP();
		}
		return new ClientConnectionUDP();
	}
	
	@Override
	public boolean connect(ClientConnectionListener listener) {
		boolean connected = false;
		DatagramSocket socket = null;
		int port = startServer();
		System.out.println("Porta do cliente: "+ port);
		
		try {
			socket = new DatagramSocket();
			
			// envia pedido de conex�o e a porta do cliente para receber mensagens do servidor
			byte[] dados = String.valueOf(UtilConnection.CONEXAO).getBytes();
			DatagramPacket pacote = new DatagramPacket(dados, dados.length, address, PORT);
			socket.send(pacote);
			
			// envia o valor da porta do servidor do cliente
			dados = String.valueOf(port).getBytes();
			pacote = new DatagramPacket(dados, dados.length, address, PORT);
			socket.send(pacote);
			
			byte[] buffer = new byte[1000]; // Cria um buffer local
			pacote = new DatagramPacket(buffer, buffer.length);
			
			// le o retorno para verificar se a conex�o foi aceita
			serverSocket.receive(pacote);
			
			// a��o que pode ser um pedido de conex�o ou uma mensagem
			Integer callback = (Integer) UtilConnection.byteArrayToObject(pacote.getData());
			
			if (callback == UtilConnection.CONEXAO_OK) {
				connected = true;
				
			}
			
			waitMessages(listener);
			
			System.out.println("connected: "+ connected);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connected;
	}

	private int startServer() {
		int port = 0;
		try {
			serverSocket = new DatagramSocket(port);
			port = serverSocket.getLocalPort();
			
			System.out.println("tulio "+port);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return port;
	}

	private void waitMessages(final ClientConnectionListener listener) {
		new Thread(){
			@Override
			public void run() {
				
				try {
					// aguarda o recebimento de mensagens do servidor
					while (true) {
						// TODO Tulio
//						Socket socket = socket.accept();
//						//				
//						final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
//						// recebe o objeto enviado pelo servidor
//						final Object object = inputStream.readObject();
//						System.out.println("Cliente recebeu: "+object);
//						
//						inputStream.close();
//						socket.close();
						byte[] buffer = new byte[1000]; // Cria um buffer local
						DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
						
						// le o retorno para verificar se a conex�o foi aceita
						serverSocket.receive(pacote);
						
						// recebe o objeto enviado pelo servidor
						final Object object = UtilConnection.byteArrayToObject(pacote.getData());
						
						final ConnectionEvent event = new ConnectionEvent();
						event.setObject(object);
						
						listener.receive(event);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
		}.start();
	}

	@Override
	public void send(Object object) {
		try {
			final Socket socket = new Socket(HOST, PORT);
			
			final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			
			outputStream.writeObject(object);
			outputStream.flush();
			
			outputStream.close();
			
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
