import java.net.*;
import java.util.Scanner;
import java.io.*;
public class TCPCliente {
    public static void main(String args[]) {
        Socket s = null;
        try {
            s = new Socket("localhost", 6789);  // Criar o socket e fazer conexão com a porta do servidor
            DataInputStream  ent = new DataInputStream(s.getInputStream());
            DataOutputStream sai = new DataOutputStream(s.getOutputStream());
            sai.writeUTF("TESTANDO"); //Enviar o nome do ID para o servidor
            System.out.print("Por favor, digite o nome do arquivo: "); //Retorno do cliente para digitar o nome do arquivo
            Scanner in = new Scanner(System.in); 
            sai.writeUTF(in.nextLine()); // Enviar o nome do arquivo para o servidor
            in.close();
            String recebido = ent.readUTF(); // Receber a primeira linha do arquivo por parte do servidor
            while (recebido != null) { //Vai escrever na tela cada linha do arquivo solicitado até chegar no fim da transferência
                System.out.println(recebido);
                recebido = ent.readUTF();
            }
        } catch (UnknownHostException e) {
            System.out.println(" Desculpe, servidor desconhecido: " + e.getMessage()); 
        } catch (EOFException e) {
            System.out.println("--- FIM DA TRANSFERENCIA ---");
        } catch (IOException e) {
            System.out.println("E/S: " + e.getMessage());
        } finally {
            if (s!=null)
                try {
                    s.close();
                } catch (IOException e){
                    System.out.println("Desculpe o encerramento do socket falhou: " + e.getMessage());
                }
        }
    }
    
}
