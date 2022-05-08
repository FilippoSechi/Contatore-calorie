
import java.io.*;

public class DatiUtente implements Serializable{
    private String NomeUtente;
    private int Peso;
    private int Altezza;
    private int AttivitàFisica; //01
    private int Età;
    
    public DatiUtente(String nome, int peso, int altezza, int attività, int età){
        NomeUtente = nome;
        Peso = peso;
        Altezza = altezza;
        AttivitàFisica = attività;
        Età = età;
    }
    
    public String restituisciNomeUtente(){
        return NomeUtente;
    }
    
    public int restituisciPeso(){
        return Peso;
    }
    
    public int restituisciAltezza(){
        return Altezza;
    }
    
    public int restituisciEtà(){
        return Età;
    }
    
    public int restituisciAttività(){
        return AttivitàFisica;
    }
    
    public int calcoloFabbisogno(){ //02
        double BMR = 10*Peso + 6.25*Altezza - 5*Età + 5;
        double indiceAttività = 0;
        switch(AttivitàFisica){
            case 0: indiceAttività = 1.35; break;
            case 1: indiceAttività = 1.55; break;
            case 2: indiceAttività = 1.75; break;
        }
        return (int) (BMR*indiceAttività);
    }
}

/* NOTE
01) 0 -> "Leggera (1-2 volte a settimana)"
    1 -> "Moderata (3-4 volte a settimana)"
    2 -> "Intensa (5-6 volte a settimana)"
02) Il calcolo del fabbisogno calorico giornaliero segue quanto riportato su questo sito: https://www.omnicalculator.com/health/bmr-harris-benedict-equation
*/