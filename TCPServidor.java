import java.net.*;
import java.io.*;
public class TCPServidor {
    public static void main(String args[]) {
        try {
            int porta = 6789;
            if (args.length > 0) porta = Integer.parseInt(args[0]);
            
            @SuppressWarnings("resource")
			ServerSocket escuta = new ServerSocket(porta); //Criar o Servesocket
            System.out.println("*** Servidor ***"); //printar informando o inicio
            System.out.println("*** Inicio - porta de escuta (listening): " + porta);//printar informando a porta que ele tá escutando
            while (true) { // Vai escutar a requisição
                Socket cliente = escuta.accept(); //armazenar a referencia de requisição
                System.out.println("*** Socket de escuta (listen): " + escuta.getLocalSocketAddress().toString()); 
                System.out.println("*** Conexao aceita de (remoto): " + cliente.getRemoteSocketAddress().toString()); //printa recebimento de conexão
                new Conexao(cliente); //instância um novo objeto de conexão e passar pra nova thread a referência desse cliente
            }
        } catch (IOException e) {
            System.out.println("Erro na escuta: " + e.getMessage());
        }
    }
    
}

class Conexao extends Thread {
    protected BufferedReader arq = null;
    DataInputStream ent;
    DataOutputStream sai;
    Socket cliente;
    String idCliente = null;
    
    public Conexao(Socket s) { //Vai esperar a referencia e armazenar na variavel cliente
        try {
            cliente = s;
            ent = new DataInputStream(cliente.getInputStream());
            sai = new DataOutputStream(cliente.getOutputStream());
            idCliente = ent.readUTF(); //espera que o cliente mande uma mensagem e atribui ao IDcliente
            this.start(); // chama a função start e executar a função run
        } catch (IOException e) {
            System.out.println("Erro IO Conexao: " + e.getMessage());
        }
    }
    
    public void enviarMsg(String msg) {
        try {
            sai.writeUTF(msg);
        } catch (IOException e1) {
            System.out.println("Erro de escrita no buffer da conexao ("+idCliente+")");
        }
    }
    
    
    public void run() { // vai executar todo o codigo com uma Tread 
        try {
            String arquivo = ent.readUTF(); //espera que o cliente mande o nome do arquivo
            arq = new BufferedReader(new FileReader(arquivo)); //Vai tentar abrir o arquivo
        } catch (FileNotFoundException e) { //se ele n conseguir abrir ele vai apresentar a falha
            System.err.println("Arquivo nao econtrado: \""+e.getMessage()+"\""); //printa arquivo n encontrado
            enviarMsg("!!! Erro ao tentar abrir arquivo \""+e.getMessage()+"\""); //envia a mensagem de erro
        } catch (IOException e) {
            System.err.println("Arquivo nao econtrado: \""+e.getMessage()+"\"");
            enviarMsg("!!! Erro ao tentar abrir arquivo \""+e.getMessage()+"\"");
        }
        
        if (arq != null) { //se ele conseguir abrir o arquivo
            try {
                String l = arq.readLine(); //armazena na variavel l, cada linha do arquivo
                while (l != null) {
                    enviarMsg(l);
                    l = arq.readLine();
                }
            } catch (IOException e) { //tenta ler linha do arquivo e se nao conseguir apresenta erro
                System.err.println("Erro ao ler linha do arquivo \""+ e.getMessage() +"\" ("+idCliente+")");
                enviarMsg("!!! Erro ao ler arquivo " + e.getMessage());
            }
            try {
                arq.close(); //tenta fechar o arquivo e se nao conseguir apresenta erro
            } catch (IOException e) {
                System.out.println("Erro fechamento do arquivo \""+ e.getMessage() +"\" ("+idCliente+")");
            }
            try {
                cliente.close(); //fecha a conexão com o cliente
            } catch (IOException e) {
                System.out.println("Erro fechamento do socket cliente ("+idCliente+")");
            }
            System.out.println("*** Conexao encerrada com "+idCliente + "\n");
        }
    }
}