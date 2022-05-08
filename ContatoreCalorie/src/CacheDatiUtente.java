
import java.io.*;

public class CacheDatiUtente{
    
    public static void salvaDatiUtente(DatiUtente utente){
        try( FileOutputStream fout = new FileOutputStream("DatiUtente.bin");
                ObjectOutputStream oout = new ObjectOutputStream(fout);){
            oout.writeObject(utente);
        }catch (IOException e){
            System.out.println("Errore: impossibile salvare i dati utente");
        }
    }
    
    public static DatiUtente prelevaDatiUtente(){
        try( FileInputStream fin = new FileInputStream("DatiUtente.bin");
                ObjectInputStream oin = new ObjectInputStream(fin);){
            DatiUtente utente = (DatiUtente) oin.readObject();
            return utente;
        }catch (Exception e){
            System.out.println("Errore: impossibile prelevare i dati utente");
        }
        return null;
    }
}
