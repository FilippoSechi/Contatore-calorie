
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.chart.*;

public class GraficoMacronutrienti{
    private ObservableList<PieChart.Data> DatiTorta;
    private PieChart TortaVisuale;
    
    public GraficoMacronutrienti(){ //01
        DatiTorta =
                FXCollections.observableArrayList(
                new PieChart.Data("Carboidrati", 0),
                new PieChart.Data("Proteine", 0),
                new PieChart.Data("Grassi", 0));
        TortaVisuale = new PieChart(DatiTorta);
        TortaVisuale.setTitle("Macronutrienti");
        TortaVisuale.setAnimated(true);
        TortaVisuale.setTitleSide(Side.TOP);
        TortaVisuale.setLegendSide(Side.RIGHT);
        TortaVisuale.setLabelsVisible(false);
        TortaVisuale.setMaxWidth(340); TortaVisuale.setMaxHeight(230);
    }
    
    public PieChart getPieChart(){
        return TortaVisuale;
    }
    
    public void aggiornaGraficoTorta(double carb, double prot, double grassi){
        DatiTorta.get(0).setPieValue(carb); //02
        DatiTorta.get(1).setPieValue(prot);
        DatiTorta.get(2).setPieValue(grassi);
    }
}

/* NOTE
01) Tutti i metodi per la gestione della legenda, animazioni, titoli -> https://docs.oracle.com/javafx/2/charts/pie-chart.htm
02) https://docs.oracle.com/javase/8/javafx/api/javafx/scene/chart/PieChart.Data.html
*/