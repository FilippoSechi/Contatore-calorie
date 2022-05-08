
import java.time.*;
import java.time.format.*;

public class Evento {   
    public String Applicazione;
    public String etichetta;
    public String data;
    public String IPclient;
    
    public Evento(String nome,String e,String IP){
        Applicazione = nome;
        etichetta = e;
        IPclient = IP;
        LocalDateTime tempo = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");   //01
        data = tempo.format(formatter);
    }
}

/* NOTE
01) https://mkyong.com/java8/java-8-how-to-format-localdatetime/
*/