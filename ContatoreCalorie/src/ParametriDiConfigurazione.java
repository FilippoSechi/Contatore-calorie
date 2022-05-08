
import com.thoughtworks.xstream.*;
import java.io.*;
import java.nio.file.*;
import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;
import org.w3c.dom.*;
import org.xml.sax.*;


public class ParametriDiConfigurazione{
    public Log log;
    public Alimento[] ListaAlimenti;
    public Stile stile;
    public DatiDBMS DBMS;
    
    private static void valida(String file){
        try{
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Document d = db.parse(new File(file));
            file = file.replace("xml", "xsd");
            Schema s = sf.newSchema(new StreamSource(new File(file)));
            s.newValidator().validate(new DOMSource(d));
        }catch(Exception e){ 
            if (e instanceof SAXException)
                System.out.println("Errore di validazione: " + e.getMessage());
            else
                System.out.println(e.getMessage());
        }
    }
    
    public static ParametriDiConfigurazione deserializzaXML(String file){
        valida(file);
        ParametriDiConfigurazione param = null;
        XStream xs = new XStream();
        String x = null;
        try{
            x = new String(Files.readAllBytes(Paths.get(file)));
        }catch(Exception e){ System.out.println(e.getMessage());}
        
        param = (ParametriDiConfigurazione)xs.fromXML(x);
        return param;
    }
    
    public String getPasswordDBMS(){
        return DBMS.PasswordDBMS;
    }
    public String getUsernameDBMS(){
        return DBMS.UsernameDBMS;
    }
    public int getPortaDBMS(){
        return DBMS.PortaDBMS;
    }
    public String getIPDBMS(){
        return DBMS.IPDBMS;
    }
    
    public String getFont(){
        return stile.Font;
    }
    
    public int getDimensioneTesto(){
        return stile.Dimensione;
    }
    
    public int getPortaServerLog(){
        return log.PortaServerLog;
    }
    
    public String getIPServerLog(){
        return log.IndirizzoIPLog;
    }
    
    public String getIPClient(){
        return log.IndirizzoIPclient;
    }
    
    private class DatiDBMS{
        public String IPDBMS;
        public int PortaDBMS;
        public String UsernameDBMS;
        public String PasswordDBMS;
        
        public DatiDBMS(String ip, int porta, String username, String password){
            IPDBMS = ip;
            PortaDBMS = porta;
            UsernameDBMS = username;
            PasswordDBMS = password;
        }
    }
    
    private class Stile{
        public String Font;
        public int Dimensione;
        
        public Stile(String font, int dim){
            Font = font;
            Dimensione = dim;
        }
    }
    
    private class Log{
        public int PortaServerLog;
        public String IndirizzoIPLog;
        public String IndirizzoIPclient;
        
        public Log(String IPLog, String IPClient, int porta){
            PortaServerLog = porta;
            IndirizzoIPLog = IPLog;
            IndirizzoIPclient = IPClient;
        }
    }
}