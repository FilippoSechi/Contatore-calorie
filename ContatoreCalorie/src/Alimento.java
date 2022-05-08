public class Alimento{
    public final String nome;
    private final double proteine;  //01
    private final double carboidrati;   //01
    private final double grassi;    //01

    public Alimento(String n, double p, double c, double g){
        nome = n;
        proteine = p;
        carboidrati = c;
        grassi = g;
    }
    
    public double Carboidrati(int peso){
        return (carboidrati/100)*peso;
    }
    
    public double Grassi(int peso){
        return (grassi/100)*peso;
    }
    
    public double Proteine(int peso){
       return (proteine/100)*peso;
    }
}


/* NOTE
01) I valori sono riferiti per 100 grammi di prodotto
*/