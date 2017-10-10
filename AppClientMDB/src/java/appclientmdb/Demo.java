/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appclientmdb;

import DatabaseUtility.DatabaseUtilityRemote;
import Enumerations.Formato;
import Enumerations.StatoLettura;
import Enumerations.Valutazione;
import Exceptions.ConnectionErrorException;
import Exceptions.InputRicercaException;
import Exceptions.LoginException;
import Exceptions.ModificaInformazioniException;
import Exceptions.RegistrazioneException;
import Exceptions.VincoliInputException;
import GestoreAccountLocale.GestoreAccountLocale;
import GestoreLibreriaLocale.GestoreLibreriaLocale;
import LogicaDominio.CopiaUtente;
import LogicaDominio.Libro;
import Util.ParametroRicerca;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author marchins
 */
public class Demo {

    public static EntityManagerFactory emf;
    public static EntityManager em;

    public static void main(String[] args) throws ParseException, VincoliInputException, LoginException, RegistrazioneException, ConnectionErrorException, InterruptedException, ExecutionException, IOException, MalformedURLException, InputRicercaException, ModificaInformazioniException {

        emf = javax.persistence.Persistence.createEntityManagerFactory("ClientMDBPU");
        em = emf.createEntityManager();
        Scanner keyboard = new Scanner(System.in);
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("-------------------- INIZIO DEMO --------------------");
        System.out.println("Verrà registrato un utente con i seguenti dati:");
        System.out.println("username: user124");
        System.out.println("password: passw124");
        System.out.println("nome: John");
        System.out.println("cognome: Doe");
        System.out.println("email: johndoe@gmail.com");
        System.out.println("nato il: 28/03/1988");

        String continua = keyboard.nextLine();
        try {
            //registrazione
            SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
            String miaData = "28/03/1988";
            Date data = formatDate.parse(miaData);
            GestoreAccountLocale.registrazione("user125", "passw125", "John", "Doe", "johndoe5@gmail.com", data);
            System.out.println();
            System.out.println("Verrà ora eseguito il login dell'utente registrato");
            continua = keyboard.nextLine();

            //login
            GestoreAccountLocale.login("user125", "passw125");
            System.out.println("Verrà ora utilizzata la lettura del barcode per aggiungere un libro");
            continua = keyboard.nextLine();
            do {
                GestoreLibreriaLocale.aggiungiLibroTramiteLetturaBarcode();
                System.out.println("Aggiungere un altro libro? (s/n)");
            } while (!(continua = keyboard.nextLine()).contains("n"));

            System.out.println("Al libro \"True. La mia storia\" verrà ora assegnata una valutazione di 5 stelle e verrà segnato come \"letto\" ");
            continua = keyboard.nextLine();

            List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria("True", null, null, null, null, null);
            GestoreLibreriaLocale.modificaInformazioniSoggettive(copie.get(0), null, StatoLettura.LETTO, Valutazione.CINQUE, null, null);

            System.out.println("Verranno ora ricercati i libri contenuti nella libreria che hanno valutazione 5 stelle");
            continua = keyboard.nextLine();

            copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, null, Valutazione.CINQUE, null, null, null);
            for (CopiaUtente copia : copie) {
                GestoreLibreriaLocale.visualizzaSchedaLibro(copia);
            }

            System.out.println();
            System.out.println("Verrà ora simulato il logout");
            continua = keyboard.nextLine();

            em.getTransaction().begin();
            em.createQuery("DELETE FROM CopiaUtente cu").executeUpdate();
            em.createQuery("DELETE FROM Libro l").executeUpdate();
            em.createQuery("DELETE FROM Account a").executeUpdate();
            em.getTransaction().commit();
            //em.close();

            System.out.println("Verrà ora effettuato il login per verificare la sincronizzazione ");
            continua = keyboard.nextLine();

            GestoreAccountLocale.login("user124", "passw124");

            System.out.println("Premere un tasto per ripristinare lo stato iniziale del sistema");
            continua = keyboard.nextLine();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM CopiaUtente cu").executeUpdate();
            em.createQuery("DELETE FROM Libro l").executeUpdate();
            em.createQuery("DELETE FROM Account a").executeUpdate();
            em.getTransaction().commit();

            DatabaseUtilityRemote dbUtility = lookupDatabaseUtilityRemote();
            dbUtility.svuotaTabellaCopieUtenti();
            dbUtility.svuotaTabellaLibro();
            dbUtility.svuotaTabellaAccount();

            System.exit(0);
        }

    }

    private static DatabaseUtilityRemote lookupDatabaseUtilityRemote() {
        try {
            Properties p = new Properties();
            p.put("java.naming.factory.initial", "com.sun.enterprise.naming.impl.SerialInitContextFactory");
            p.put("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
            p.put("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
            Context c = new InitialContext();
            return (DatabaseUtilityRemote) c.lookup("java:global/ServerMDB/ServerMDB-ejb/DatabaseUtility!DatabaseUtility.DatabaseUtilityRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

}
