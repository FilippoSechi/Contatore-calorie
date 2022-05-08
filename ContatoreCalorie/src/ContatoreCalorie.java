
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class ContatoreCalorie extends Application {
    
    private GraficoMacronutrienti GraficoTorta;
    private int DimensioneTesto;
    private String Font;
    private String IndirizzoIP;
    private int PortaServerLog;
    private String IndirizzoIPLog;
    private DiarioAlimentare Diario;
    private DatiUtente Utente;
    private TextField CampoNome;
    private TextField CampoEtà;
    private TextField CampoAltezza;
    private TextField CampoPeso;
    private TextField CampoFabbisogno;
    private TextField CampoCalorie;
    private ToggleGroup toggle;
    
    public void start(Stage stage){
        
        Group group = new Group();
        intestazioni(group);
        recuperaParametriDiConfigurazione();
        
        CampoNome = nuovoCampoDati("Nome Utente",30,100,group);
        CampoEtà = nuovoCampoDati("Età",30,150,group);
        CampoAltezza = nuovoCampoDati("Altezza (cm)",30,200,group);
        CampoPeso = nuovoCampoDati("Peso (kg)",30,250,group);
        
        nuovoBottone("CERCA",330,100,group);
        nuovoBottone("CALCOLA",130,395,group);
        toggle = nuoviRadioButton(group);
            
        CampoFabbisogno = nuovoCampoSoloLettura("Fabbisogno Giornaliero (kcal)", 85, 440,group);
        CampoCalorie = nuovoCampoSoloLettura("Calorie Consumate (kcal)", 590,440,group);
        disegnaLinea(group);
        
        GraficoTorta = new GraficoMacronutrienti();
        impostaLayoutGrafico(GraficoTorta.getPieChart(),500,500,group); //06
        Diario = new DiarioAlimentare(CampoCalorie,GraficoTorta);
        impostaLayoutTabella(Diario.getTabellaVisuale(),520,100,group); //06
        
        Scene scene = new Scene(group);
        stage.setTitle("Contatore Calorie");
        stage.setWidth(900); stage.setHeight(780);
        stage.setScene(scene); stage.show();
        
        ripristinaDatiUtente();
        EventoDiNavigazione.inviaEvento( new Evento("Contatore Calorie", "AVVIO", IndirizzoIP), PortaServerLog, IndirizzoIPLog );
        stage.setOnCloseRequest( (WindowEvent e)-> { CacheDatiUtente.salvaDatiUtente(Utente);   //09
                                                    EventoDiNavigazione.inviaEvento(new Evento("Contatore Calorie", "TERMINE", IndirizzoIP), PortaServerLog, IndirizzoIPLog);} );
    }
    
    private TextField nuovoCampoDati(String etichetta, double x, double y, Group group){    //02
        Label label = new Label(etichetta);
        label.setLayoutX(x); label.setLayoutY(y);
        label.setFont(new Font(Font,DimensioneTesto));
        
        TextField text = new TextField();
        text.setLayoutX(x + 100); text.setLayoutY(y);
        
        group.getChildren().add(label);
        group.getChildren().add(text);
        
        return text;
    }
    
    private void intestazioni(Group group){ //01
        Label titolo = new Label("CONTATORE CALORIE");
        titolo.setLayoutX(310); titolo.setLayoutY(10);
        titolo.setStyle("-fx-font-weight: bold;");
        titolo.setFont(new Font(Font,DimensioneTesto+20));
        group.getChildren().add(titolo);
        
        Label titoloDatiUtente = new Label("Dati Utente");
        titoloDatiUtente.setLayoutX(140); titoloDatiUtente.setLayoutY(60);
        titoloDatiUtente.setStyle("-fx-font-weight: bold;");
        titoloDatiUtente.setFont(new Font(Font,DimensioneTesto));
        group.getChildren().add(titoloDatiUtente);
        
        Label titoloDiario = new Label("Diario Alimentare");
        titoloDiario.setLayoutX(600); titoloDiario.setLayoutY(60);
        titoloDiario.setStyle("-fx-font-weight: bold;");
        titoloDiario.setFont(new Font(Font,DimensioneTesto));
        group.getChildren().add(titoloDiario);
    }
    
    private void nuovoBottone(String etichetta, double x, double y,Group group){    //03
        Button bottone = new Button(etichetta);
        bottone.setLayoutX(x); bottone.setLayoutY(y);
        bottone.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        group.getChildren().add(bottone);
        
        switch (etichetta) {
            case "CERCA":
                bottone.setOnAction( (ActionEvent e) -> {cercaDiarioUtente();
                                                         EventoDiNavigazione.inviaEvento(new Evento("Contatore Calorie", "CERCA", IndirizzoIP), PortaServerLog, IndirizzoIPLog);} );
                break;
            case "CALCOLA":
                bottone.setOnAction( (ActionEvent e) -> {fabbisogno();
                                                         EventoDiNavigazione.inviaEvento(new Evento("Contatore Calorie", "CALCOLA", IndirizzoIP), PortaServerLog, IndirizzoIPLog);} );
                break;
        }
    }

    private ToggleGroup nuoviRadioButton(Group group){  //04
        ToggleGroup toggle = new ToggleGroup();
        VBox vbox = new VBox();
        vbox.setSpacing(4);
        vbox.setLayoutX(130); vbox.setLayoutY(295);
        
        RadioButton rb1 = new RadioButton("Leggera (1-2 volte a settimana)");   //08
        RadioButton rb2 = new RadioButton("Moderata (3-4 volte a settimana)");
        RadioButton rb3 = new RadioButton("Intensa (5-6 volte a settimana)");
        
        rb1.setToggleGroup(toggle);
        rb2.setToggleGroup(toggle);
        rb3.setToggleGroup(toggle);
        
        vbox.getChildren().add(rb1);
        vbox.getChildren().add(rb2);
        vbox.getChildren().add(rb3);
        group.getChildren().add(vbox);
        
        Label label = new Label("Attività Fisica");
        label.setLayoutX(30); label.setLayoutY(320);
        label.setFont(new Font(Font,DimensioneTesto));
        group.getChildren().add(label);
        
        return toggle;
    }
    
    private TextField nuovoCampoSoloLettura(String etichetta, double x, double y, Group group){ //05
        Label label = new Label(etichetta);
        label.setLayoutX(x); label.setLayoutY(y);
        label.setStyle("-fx-font-weight: bold;");
        label.setFont(new Font(Font,DimensioneTesto));
        group.getChildren().add(label);
        
        TextField text = new TextField();
        text.setLayoutX(x + 5); text.setLayoutY(y + 30);
        text.setEditable(false);
        text.setAlignment(Pos.CENTER);
        group.getChildren().add(text);
        return text;
    }
    
    private void disegnaLinea(Group group){
        Line linea = new Line();
        linea.setStartX(470); linea.setStartY(70);
        linea.setEndX(470); linea.setEndY(500);
        group.getChildren().add(linea);
    }
    
    private void impostaLayoutTabella(TableView tabella, int x, int y, Group group){
        tabella.setPrefSize(322,320);
        ( (TableColumn)tabella.getColumns().get(0) ).setPrefWidth(159);
        ( (TableColumn)tabella.getColumns().get(1) ).setPrefWidth(159);
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setLayoutX(x); vbox.setLayoutY(y);
        vbox.getChildren().addAll(Diario.getTabellaVisuale());
        group.getChildren().add(vbox);
    }
    
    private void impostaLayoutGrafico(PieChart grafico, int x, int y,Group group){
        grafico.setLayoutX(x); grafico.setLayoutY(y);
        grafico.setStyle("-fx-font-weight: bold; -fx-font-size: "+ DimensioneTesto +"; -fx-font-family: "+Font+";");
        group.getChildren().add(grafico);
    }
    
    private void fabbisogno(){  //10
        int attività = 1;
        if(toggle.getSelectedToggle() != null){
            String testo = ((RadioButton) toggle.getSelectedToggle()).getText();
            if(testo.contains("Leggera"))
                attività = 0;
            else if(testo.contains("Moderata"))
                attività = 1;
            else if(testo.contains("Intensa"))
                attività = 2;
        }
        Utente = new DatiUtente(CampoNome.getText(),Integer.parseInt(CampoPeso.getText()),
                                Integer.parseInt(CampoAltezza.getText()),attività,Integer.parseInt(CampoEtà.getText())); //14
        
        CampoFabbisogno.setText( Integer.toString(Utente.calcoloFabbisogno()) );
        Diario.aggiornaUtente(Utente.restituisciNomeUtente());
    }
    
    private void ripristinaDatiUtente(){    //07
        Utente = CacheDatiUtente.prelevaDatiUtente();
        if(Utente == null) return;
        
        CampoNome.setText(Utente.restituisciNomeUtente());
        CampoPeso.setText(Integer.toString(Utente.restituisciPeso()));
        CampoAltezza.setText(Integer.toString(Utente.restituisciAltezza()));
        CampoEtà.setText(Integer.toString(Utente.restituisciEtà()));
        

        ((RadioButton)toggle.getToggles().get(Utente.restituisciAttività())).setSelected(true); //08
        
        CampoFabbisogno.setText( Integer.toString(Utente.calcoloFabbisogno()) );  
        
        Diario.aggiornaUtente(Utente.restituisciNomeUtente());
    }
    
    private void cercaDiarioUtente(){   //11
        Diario.aggiornaUtente(CampoNome.getText());
        if(Utente == null)  //12
            Utente = new DatiUtente(CampoNome.getText(),0,0,0,0);   //13
        
        Diario.caricaDiario();
    }
    
    private void recuperaParametriDiConfigurazione(){   
        ParametriDiConfigurazione param = ParametriDiConfigurazione.deserializzaXML("Parametri.xml");
        Font = param.getFont();
        DimensioneTesto = param.getDimensioneTesto();
        IndirizzoIP = param.getIPClient();
        PortaServerLog = param.getPortaServerLog();
        IndirizzoIPLog = param.getIPServerLog();
    }
}

/* NOTE
01) Inizializza i titoli delle sezioni di cui si compone l'applicazione
02) Inizializza un' etichetta e relativo TextField per l'inserimento dei dati utente
03) Crea un bottone e assegna le azioni legate alla sua pressione
04) Inizializza il campo per la selezione dell' Attività fisica, composta da 3 RadioButton
05) Inizializza un'etichetta e relativo TextField di SOLA LETTURA (es: Calorie Consumate non deve essere modificato dall'utente)
06) Posiziono gli elementi grafici (tabella e pie chart) nella schermata
07) Carica i dati utente salvati in cache locale, se disponibili. Inizializza i TextField con i dati prelevati
08) https://docs.oracle.com/javafx/2/ui_controls/radio-button.htm
09) Alla chiusura dell'applicazione salvo i dati utente su file binario
10) Crea un nuovo utente e invoca il calcolo del fabbisogno giornaliero sulla base dei dati utente
11) Invoca la ricerca nel DBMS di record precedentemente inseriti nel diario dell'utente
12) Caso in cui l'utente inserisca il nome utente senza premere prima "CALCOLA" 
13) Creo un utente provvisorio in attesa che l'utente completi l'inserimento dei dati
14) Creo un nuovo utente con i dati inseriti al momento della pressione del tasto "CALCOLA"
*/
