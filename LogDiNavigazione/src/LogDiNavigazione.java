import java.io.*;
import java.net.*;
import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class LogDiNavigazione{
    private static String IndirizzoIP;
    private static int Porta;
    
    public static void main(String [] args){
        ParametriDiConfigurazione param = ParametriDiConfigurazione.deserializzaXML("../ContatoreCalorie/Parametri.xml");
        IndirizzoIP = param.getIPServerLog();
        Porta = param.getPortaServerLog();
        try(ServerSocket servs = new ServerSocket(Porta,10,InetAddress.getByName(IndirizzoIP))) //01
        {
            while(true){
                try(Socket s = servs.accept();
                    DataInputStream din = new DataInputStream(s.getInputStream()))
                {
                    String x = din.readUTF();
                    valida(x);
                    FileOutputStream fout = new FileOutputStream("LogNavigazione.txt",true);
                    fout.write(x.getBytes());
                    System.out.println(x);
                }
            }
        }catch(Exception e){ e.printStackTrace();}
    }
    
     private static void valida(String x){
        try{
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Document d = db.parse( new InputSource(new StringReader(x)) );
            Schema s = sf.newSchema(new StreamSource(new File("EventoLog.xsd")));
            s.newValidator().validate(new DOMSource(d));
        }catch(Exception e){ 
            if (e instanceof SAXException)
                System.out.println("Errore di validazione: " + e.getMessage());
            else
                System.out.println(e.getMessage());
        }
    }
}


/* NOTE
01) https://docs.oracle.com/javase/7/docs/api/java/net/ServerSocket.html
*/