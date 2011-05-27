package br.unifor.sd.service.server;

import java.util.Arrays;
import java.util.List;

import br.unifor.sd.connection.factory.ConnectionFactory;
import br.unifor.sd.connection.server.ServerConnection;
import br.unifor.sd.entity.ColorPlayer;
import br.unifor.sd.entity.Player;
import br.unifor.sd.service.Method;

public class ServerOutputService {
	
	private ServerConnection serverConnection = ConnectionFactory.getServerConnection();
	
	private static ServerOutputService instance;
	public static final int MAX_JOGADORES = 4;
	
	private final List<ColorPlayer> cores;
	private int ultimaCor;
	
	private ServerOutputService() {
		super();
		cores = Arrays.asList(new ColorPlayer[]{ColorPlayer.VERMELHO, ColorPlayer.VERDE, ColorPlayer.AZUL, ColorPlayer.BRANCO});
	}
	
	public static ServerOutputService getInstance(){
		if (instance == null) {
			instance = new ServerOutputService();
		}
		return instance;
	}
	
	/**
	 * Informa ao jogador que � a sua vez de jogar e possibilita a sua jogada.
	 * @param jogador
	 */
	public void liberarVez(Player jogador) {
		System.out.println("Aguardando o jogador "+jogador.getClientID());
		serverConnection.send(jogador.getClientID(), new Method(Method.LIBERAR_VEZ));
	}
	
	/**
	 * Informa uma mensagem para todos os jogadores
	 */
	public void exibirMsg(final String msg) {
		final Method method = new Method(Method.EXIBIR_MSG, msg);
		serverConnection.sendAll(method);
	}
	
	public ColorPlayer nextColor() {
		if (cores != null && !cores.isEmpty()) {
			ColorPlayer cor = cores.get(ultimaCor++);
			return cor;
		} else {
			return null;
		}
	}
}
