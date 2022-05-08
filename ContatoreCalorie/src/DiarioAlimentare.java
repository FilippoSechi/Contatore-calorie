
import java.sql.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.*;
import javafx.scene.control.cell.*;

public class DiarioAlimentare{
    private final TableView DiarioVisuale;
    private final ObservableList<Inserimento> DatiTabella;
    private String utente;  //01
    private final Alimento[] ListaAlimenti;   //02
    private final TextField CampoCalorieConsumate;
    private final ArchivioDiarioAlimentare DBMS;
    private final GraficoMacronutrienti GraficoTorta; //03
    
    public DiarioAlimentare(TextField campoCalorie,GraficoMacronutrienti grafico){
        DiarioVisuale = new TableView();
        CampoCalorieConsumate = campoCalorie;
        GraficoTorta = grafico;
        DiarioVisuale.setEditable(true);
        
        TableColumn<Inserimento,String> nomeCol = new TableColumn<>("Alimento");
        TableColumn<Inserimento,String> pesoCol = new TableColumn<>("Peso");
  
        DatiTabella = FXCollections.observableArrayList(new Inserimento("","",null));
        nomeCol.setCellValueFactory( new PropertyValueFactory<>("alimento") );
        pesoCol.setCellValueFactory( new PropertyValueFactory<>("peso") );
        
        nomeCol.setCellFactory(TextFieldTableCell.forTableColumn());    //04
        nomeCol.setOnEditCommit( (CellEditEvent<Inserimento,String> e) -> {inserisciAlimento(e);});
        pesoCol.setCellFactory(TextFieldTableCell.forTableColumn());
        pesoCol.setOnEditCommit( (CellEditEvent<Inserimento,String> e) -> {inserisciPeso(e);});
        
        DiarioVisuale.getColumns().addAll(nomeCol,pesoCol);
        DiarioVisuale.setItems(DatiTabella);
        DBMS = new ArchivioDiarioAlimentare();
        ListaAlimenti = ParametriDiConfigurazione.deserializzaXML("Parametri.xml").ListaAlimenti;   //05
    }
    
    public TableView getTabellaVisuale(){
        return DiarioVisuale;
    }
    
    public void aggiornaUtente(String nome){   
        utente = nome;
    }
    
    private void inserisciAlimento(CellEditEvent<Inserimento,String> event){
        DiarioVisuale.setStyle("fx-border-color: black;");
        Inserimento ins = (Inserimento)DatiTabella.get(event.getTablePosition().getRow());
        if(ins.getPeso().equals("")){   //06
            ins.setAlimento(event.getNewValue());
            DiarioVisuale.requestFocus();
            return;
        }
        if(event.getNewValue().equals("") && !event.getOldValue().equals("")){ //07
            if(event.getTablePosition().getRow() == DatiTabella.size()-1)   //08
                DatiTabella.add(new Inserimento("","",null));
            DBMS.eliminaInserimento(utente, ins.getAlimento(), Integer.parseInt(ins.getPeso()), ins.getData()); //09
            DatiTabella.remove(event.getTablePosition().getRow());
        }
        else if(!event.getOldValue().equals("")){   //10
            DBMS.eliminaInserimento(utente, ins.getAlimento(), Integer.parseInt(ins.getPeso()), ins.getData()); //09
            ins.setAlimento(event.getNewValue());   //12
            ins.impostaData();
            if( !validaAlimento(ins) ) return;  //11
            DBMS.registraInserimento(utente, ins.getAlimento(), Integer.parseInt(ins.getPeso()),ins.getData() );
            if(event.getTablePosition().getRow() == DatiTabella.size()-1)   //08
                DatiTabella.add(new Inserimento("","",null));
        }
        else if(!event.getNewValue().equals("")){
            ins.setAlimento(event.getNewValue());
            ins.impostaData();
            if( !validaAlimento(ins) ) return;  //11
            DBMS.registraInserimento(utente, ins.getAlimento(), Integer.parseInt(ins.getPeso()), ins.getData() );
            DatiTabella.add(new Inserimento("","",null));   //13
        }
        DiarioVisuale.requestFocus();
        contaCalorieConsumate();
    }
    
    private void inserisciPeso(CellEditEvent<Inserimento,String> event){
        DiarioVisuale.setStyle("fx-border-color: black;");
        Inserimento ins = (Inserimento)DatiTabella.get(event.getTablePosition().getRow());
        if(ins.getAlimento().equals("")){   //06
            ins.setPeso(event.getNewValue());
            DiarioVisuale.requestFocus();
            return;
        }
        if(event.getNewValue().equals("") && !event.getOldValue().equals("")){ //07
            if(event.getTablePosition().getRow() == DatiTabella.size()-1)   //08
                DatiTabella.add(new Inserimento("","",null));
            DBMS.eliminaInserimento(utente, ins.getAlimento(), Integer.parseInt(ins.getPeso()), ins.getData()); //09
            DatiTabella.remove(event.getTablePosition().getRow());
        }
        else if(!event.getOldValue().equals("")){   //10
            DBMS.eliminaInserimento(utente, ins.getAlimento(), Integer.parseInt(ins.getPeso()), ins.getData()); //09
            ins.impostaData();
            ins.setPeso(event.getNewValue());   //12
            if( !validaAlimento(ins) ) return;  //11
            DBMS.registraInserimento(utente, ins.getAlimento(), Integer.parseInt(ins.getPeso()),ins.getData() );
            
        }
        else if(!event.getNewValue().equals("")){
            ins.setPeso(event.getNewValue());
            ins.impostaData();
            if( !validaAlimento(ins) ) return;  //11
            DBMS.registraInserimento(utente, ins.getAlimento(), Integer.parseInt(ins.getPeso()), ins.getData() );
            DatiTabella.add(new Inserimento("","",null));   //13
        }
        DiarioVisuale.requestFocus();
        contaCalorieConsumate();   
    }
    
    public void caricaDiario(){ //14
        DatiTabella.clear();
        DBMS.caricaDiarioUtente(utente,DatiTabella);
        contaCalorieConsumate();
        DatiTabella.add(new Inserimento("","",null));
        DiarioVisuale.setStyle("fx-border-color: black;");
    }
    
    private boolean validaAlimento(Inserimento ins){    //11
        boolean verifica = false;
        String nome = ins.getAlimento();
        for (Alimento alim : ListaAlimenti) {
            if(alim.nome.equals(nome)){
                verifica = true;
                break;
            }
        }
        if(!verifica){  
            DiarioVisuale.setStyle("-fx-border-color: red;");   //15
            DiarioVisuale.requestFocus();
        }
        return verifica;
    }
   
    private Alimento cercaAlimento(String nome){    //16
        for(Alimento alim : ListaAlimenti){
            if(alim.nome.equals(nome))
                return alim;
        }
        return null;
    }
    
    private void contaCalorieConsumate(){   //17
        Alimento alim = null;
        double CalorieConsumate = 0;
        double TotCarbo = 0;
        double TotProt = 0;
        double TotGrassi = 0;
        
        for(Inserimento ins : DatiTabella){
            alim = cercaAlimento(ins.getAlimento());
            if(alim == null) break;
            
            TotCarbo += alim.Carboidrati(Integer.parseInt(ins.getPeso()));
            TotProt += alim.Proteine(Integer.parseInt(ins.getPeso()));
            TotGrassi += alim.Grassi(Integer.parseInt(ins.getPeso()));
        }
        CalorieConsumate += 4*TotCarbo + 4*TotProt + 9*TotGrassi;   //18
        
        CampoCalorieConsumate.setText(Integer.toString((int)CalorieConsumate));
        GraficoTorta.aggiornaGraficoTorta(TotCarbo, TotProt, TotGrassi);
    }
    
    public static class Inserimento{    //19
        private final SimpleStringProperty alimento;
        private final SimpleStringProperty peso;
        private Timestamp data;
        
        public Inserimento(String nome, String peso, Timestamp data){
            alimento = new SimpleStringProperty(nome);
            this.peso = new SimpleStringProperty(peso);
            this.data = data;
        }
        
        public String getAlimento(){
            return alimento.get();
        }
        
        public void setAlimento(String nomeAlim){
            alimento.set(nomeAlim);
        }
        
        public String getPeso(){
            return peso.get();
        }
        
        public void setPeso(String pesoAlim){
            peso.set(pesoAlim);
        }
        
        public void impostaData(){
            data = new Timestamp(new java.util.Date().getTime());
        }
        
        public Timestamp getData(){
            return data;
        }
    }
}

/* NOTE
01) Nome utente, usato per l'inserimento dei record nel DBMS
02) Lista degli alimenti presenti nel file di configurazione. Sono gli unici validi per essere inseriti nel Diario
03) Pie chart, usata per invocare il calcolo dei macronutrienti
04) Metodi per editare la tabella presi da https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
05) Recupero dal file di configurazione gli alimenti da inserire in ListaAlimenti
06) Inserimento non ancora completato -> manca uno dei due parametri (Alimento / Peso)
07) L'utente ha ELIMINATO l'inserimento aggiunto precedentemente
08) Se l'inserimento eliminato era l'ultimo della tabella, devo inserire una nuova riga vuota per permettere di continuare ad usare il diario
09) Elimino l'inserimento dal database
10) L'utente ha MODIFICATO l'inserimento aggiunto precedentemente
11) Controllo se l'alimento inserito è fra quelli validi (presente nella ListaAlimenti)
12) Imposto il nuovo valore del campo inserito
13) Aggiungo una nuova riga vuota alla tabella
14) Azzera i dati contenuti nel diario (se presenti) e invoca la ricerca del Diario del nuovo utente
15) I bordi della tabella diventano rossi se l'alimento inserito non è valido
16) Ritorna l'Alimento corrispondente al nome passato come parametro
17) Effettua il conteggio delle calorie consumate e invoca il calcolo dei macronutrienti
18) https://www.msdmanuals.com/it-it/casa/disturbi-alimentari/panoramica-sulla-nutrizione/carboidrati-proteine-e-grassi
19) Classe che rappresenta un inserimento nel Diario Alimentare ( Nome alimento, Peso, Data Inserimento)
*/