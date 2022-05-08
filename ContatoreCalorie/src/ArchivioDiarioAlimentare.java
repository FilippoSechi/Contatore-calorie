import java.sql.*;
import javafx.collections.*;

public class ArchivioDiarioAlimentare{
    private final String IP;
    private final int porta;
    private final String username;
    private final String password;
    
    public ArchivioDiarioAlimentare(){
        ParametriDiConfigurazione param = ParametriDiConfigurazione.deserializzaXML("Parametri.xml");
        IP = param.getIPDBMS();
        porta = param.getPortaDBMS();
        username = param.getUsernameDBMS();
        password = param.getPasswordDBMS();
    }
    
    public void registraInserimento(String utente,String alimento,int peso, Timestamp data){
        try( Connection co = DriverManager.getConnection("jdbc:mysql://"+IP+":"+porta+"/diarioalimentare",username,password);
             PreparedStatement ps = co.prepareStatement("INSERT INTO alimentiinseriti VALUES (?,?,?,?)");)
            {
               ps.setString(1,utente); ps.setTimestamp(2,data); ps.setString(3,alimento); ps.setInt(4,peso);
               ps.executeUpdate();
            }catch(SQLException e){ System.err.println(e.getMessage());}
    }
    
    public void caricaDiarioUtente(String utente, ObservableList<DiarioAlimentare.Inserimento> ol){ //01
        try( Connection co = DriverManager.getConnection("jdbc:mysql://"+IP+":"+porta+"/diarioalimentare",username,password);
                Statement st = co.createStatement();)
        {
            ResultSet rs = st.executeQuery("Select alimento, peso,data FROM alimentiinseriti WHERE utente = '"+utente+"'");
            while(rs.next())
                ol.add(new DiarioAlimentare.Inserimento(rs.getString("alimento"),Integer.toString(rs.getInt("peso")),rs.getTimestamp("data")));
        }catch(SQLException e){ System.err.println(e.getMessage());}
    }
    
    public void eliminaInserimento(String utente, String alimento, int peso, Timestamp data){
        try( Connection co = DriverManager.getConnection("jdbc:mysql://"+IP+":"+porta+"/diarioalimentare",username,password);
                PreparedStatement ps = co.prepareStatement("DELETE FROM alimentiinseriti WHERE utente = ? AND alimento = ? AND data = ? AND peso = ?");)
        {
            ps.setString(1,utente); ps.setString(2, alimento); ps.setTimestamp(3, data); ps.setInt(4, peso);
            ps.executeUpdate();
        }catch(SQLException e){System.err.println(e.getMessage());}
    }
}

/* NOTE
01) Carica nell' ObservableList (passata come parametro) gli alimenti caricati nel diario alimentare dell'utente in sessioni precedenti
*/