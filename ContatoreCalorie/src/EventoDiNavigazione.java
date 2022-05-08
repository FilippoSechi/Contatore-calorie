
import com.thoughtworks.xstream.*;
import java.io.*;
import java.net.*;

public class EventoDiNavigazione{
    public static void inviaEvento(Evento e, int portaServer, String IPServer){
        XStream xs = new XStream();
        String x = xs.toXML(e);
        try( DataOutputStream dout = 
                new DataOutputStream( ( new Socket(IPServer,portaServer) ).getOutputStream()); )
        {
            dout.writeUTF(x);
        }catch(Exception ex){ ex.printStackTrace();}    
    }
}
