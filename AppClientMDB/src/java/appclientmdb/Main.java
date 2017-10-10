package appclientmdb;

import Enumerations.StatoLettura;
import Enumerations.Valutazione;
import Exceptions.ConnectionErrorException;
import Exceptions.InputRicercaException;
import Exceptions.LoginException;
import Exceptions.ModificaInformazioniException;
import Exceptions.RegistrazioneException;
import Exceptions.RimozioneCopiaException;
import Exceptions.VincoliInputException;
import GestoreAccountLocale.GestoreAccountLocale;
import GestoreLibreriaLocale.GestoreLibreriaLocale;
import LogicaDominio.CopiaUtente;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class Main {

    public static EntityManagerFactory emf;
    public static EntityManager em;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException, VincoliInputException, RegistrazioneException, ConnectionErrorException, LoginException, InterruptedException, ExecutionException, IOException, MalformedURLException, InputRicercaException, ModificaInformazioniException {

        
    }

}
