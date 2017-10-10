package GestoreAccountLocale;

import Util.Connection;
import Util.EmailValidator;
import Exceptions.ConnectionErrorException;
import Exceptions.LoginException;
import Exceptions.RegistrazioneException;
import Exceptions.VincoliInputException;
import GestoreAccountRemoto.GestoreAccountRemotoRemote;
import LogicaDominio.Account;
import LogicaDominio.Amico;
import LogicaDominio.Autenticazione;
import LogicaDominio.Categoria;
import LogicaDominio.CopiaUtente;
import LogicaDominio.Libro;
import MultilanguageLabels.ErrorLabels;
import MultilanguageLabels.SuccessLabels;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.apache.commons.lang.StringUtils;

public class GestoreAccountLocale {

    private static final EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ClientMDBPU");
    private static final EntityManager em = emf.createEntityManager();

    /* RF-ACC-01: Registrazione dell'utente al sistema MDB. */
    public static void registrazione(String username, String password, String nome, String cognome, String email, Date dataNascita) throws VincoliInputException, RegistrazioneException, ParseException, ConnectionErrorException {

        if (Connection.isConnectionAvailable()) {
            // Verifica vincoli sugli input.
            checkUsername(username);
            checkPassword(password);
            checkNome(nome);
            checkCognome(cognome);
            checkEmail(email);
            checkData(dataNascita);

            // Utilizza GestoreAccountRemoto per verificare i dati e registrare l'utente.
            GestoreAccountRemotoRemote gestore = lookupGestoreAccountRemotoRemote();
            if (!gestore.verificaDatiAccount(username, password, nome, cognome, email, convertiFormatoData(dataNascita))) {
                throw new RegistrazioneException();
            }

            // Stampa conferma.
            System.out.println(SuccessLabels.REGISTRATION_SUCCESS_ITA);

        } else {
            throw new ConnectionErrorException();
        }
    }

    /* RF-ACC-02: Login all'applicazione MDB. */
    public static void login(String username, String password) throws VincoliInputException, LoginException, ConnectionErrorException {

        if (Connection.isConnectionAvailable()) {
            // Verifica vincoli sugli input.
            checkUsername(username);
            checkPassword(password);

            /* Utilizza il GestoreAccountRemoto per verificare i dati del login.
             Nel caso siano corretti, viene memorizzato nel DB una copia
             dell'oggetto Account ricevuto dal componente remoto.
             */
            GestoreAccountRemotoRemote gestore = lookupGestoreAccountRemotoRemote();
            Account account = gestore.verificaDatiLogin(username, password);
            if (account != null) {
                memorizzaAccount(account);
                sincronizzaDatiUtente(account);
            } else {
                throw new LoginException();
            }
        } else {
            throw new ConnectionErrorException();
        }
    }

    /* =============== INIZIO METODI PRIVATI ======================*/
    /* Metodo privato utilizzato per memorizzare nel DB locale un oggetto di tipo Account. */
    private static void memorizzaAccount(Account account) {
        em.getTransaction().begin();
        try {
            em.persist(account);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    /* Metodo privato che si occupa di scaricare in locale tutti i dati di un utente presenti nel database remoto */
    private static void sincronizzaDatiUtente(Account account) {

        Autenticazione autenticazione = new Autenticazione(account.getUsername(), account.getPassword());
        GestoreAccountRemotoRemote gestore = lookupGestoreAccountRemotoRemote();
        HashMap<String, Vector<Object>> datiUtente = gestore.sincronizzaDatiDaRemoto(autenticazione);

        em.getTransaction().begin();

        setCategorie(datiUtente.get("categorie"));
        setLibri(datiUtente.get("libri"));
        setCopieUtente(datiUtente.get("copieutente"));
        setRubrica(datiUtente.get("amici"));

        em.getTransaction().commit();

    }

    /* Metodo privato che persiste localmente i libri */
    private static void setLibri(Vector<Object> libri) {
        for (Object o : (Vector<Object>) libri) {
            Libro libro = (Libro) o;
            em.persist(libro);
        }
    }

    /* Metodo privato che persiste localmente le copie utente */
    private static void setCopieUtente(Vector<Object> copieutente) {
        for (Object o : (Vector<Object>) copieutente) {
            em.persist((CopiaUtente) o);
        }
    }

    /* Metodo privato che persiste localmente le categorie */
    private static void setCategorie(Vector<Object> categorie) {
        for (Object o : (Vector<Object>) categorie) {
            em.persist((Categoria) o);
        }
    }

    /* Metodo privato che persiste localmente la rubrica di amici */
    private static void setRubrica(Vector<Object> amici) {
        for (Object o : (Vector<Object>) amici) {
            em.persist((Amico) o);
        }
    }

    /* Metodo privato per verificare i vincoli sull'input username. */
    private static void checkUsername(String username) throws VincoliInputException {
        if (username == null) {
            throw new VincoliInputException(ErrorLabels.USER_NOT_SPECIFICIED_ITA);
        }
        if (!(username.length() >= 4 && username.length() <= 20)) {
            throw new VincoliInputException(ErrorLabels.USER_LENGTH_ERROR_ITA);
        }
        if (!StringUtils.isAlphanumeric(username)) {
            throw new VincoliInputException(ErrorLabels.USER_IS_NOT_ALPHANUMERIC_ITA);
        }
    }

    /* Metodo privato per verificare i vincoli sull'input password. */
    private static void checkPassword(String password) throws VincoliInputException {
        if (password == null) {
            throw new VincoliInputException(ErrorLabels.PASSWORD_NOT_SPECIFIED_ITA);
        }
        if (!(password.length() >= 8 && password.length() <= 20)) {
            throw new VincoliInputException(ErrorLabels.PASSWORD_LENGTH_ERROR_ITA);
        }
        if (!StringUtils.isAlphanumeric(password)) {
            throw new VincoliInputException(ErrorLabels.PASSWORD_IS_NOT_ALPHANUMERIC_ITA);
        }
    }

    /* Metodo privato per verificare i vincoli sull'input nome. */
    private static void checkNome(String nome) throws VincoliInputException {
        if (nome == null) {
            throw new VincoliInputException(ErrorLabels.NOME_NOT_SPECIFICIED_ITA);
        }
        if (!(nome.length() >= 1 && nome.length() <= 50)) {
            throw new VincoliInputException(ErrorLabels.NOME_LENGTH_ERROR_ITA);
        }
        if (!StringUtils.isAlphanumeric(nome)) {
            throw new VincoliInputException(ErrorLabels.NOME_IS_NOT_ALPHANUMERIC_ITA);
        }
    }

    /* Metodo privato per verificare i vincoli sull'input cognome. */
    private static void checkCognome(String cognome) throws VincoliInputException {
        if (cognome == null) {
            throw new VincoliInputException(ErrorLabels.COGNOME_NOT_SPECIFICIED_ITA);
        }
        if (!(cognome.length() >= 1 && cognome.length() <= 50)) {
            throw new VincoliInputException(ErrorLabels.COGNOME_LENGTH_ERROR_ITA);
        }
        if (!StringUtils.isAlphanumeric(cognome)) {
            throw new VincoliInputException(ErrorLabels.COGNOME_IS_NOT_ALPHANUMERIC_ITA);
        }
    }

    /* Metodo privato per verificare i vincoli sull'input email. */
    private static void checkEmail(String email) throws VincoliInputException {
        if (email == null) {
            throw new VincoliInputException(ErrorLabels.EMAIL_NOT_SPECIFICIED_ITA);
        }
        EmailValidator ev = new EmailValidator();
        if (!ev.validate(email)) {
            throw new VincoliInputException(ErrorLabels.EMAIL_NOT_VALID_ITA);
        }
    }

    /* Metodo privato per verificare i vincoli sull'input data nascita e convertirla nel formato corretto. */
    private static void checkData(Date dataNascita) throws VincoliInputException, ParseException {
        if (dataNascita == null) {
            throw new VincoliInputException(ErrorLabels.DATA_NOT_SPECIFICIED_ITA);
        }
    }

    /* Metodo privato per convertire la data in una stringa contenente la data in formato americano. */
    private static String convertiFormatoData(Date dataNascita) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        return df.format(dataNascita);
    }

    /* Metodo privato per accedere al componente remoto GestoreAccountRemoto. */
    private static GestoreAccountRemotoRemote lookupGestoreAccountRemotoRemote() {
        try {
            Properties p = new Properties();
            p.put("java.naming.factory.initial", "com.sun.enterprise.naming.impl.SerialInitContextFactory");
            p.put("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
            p.put("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
            Context c = new InitialContext(p);
            return (GestoreAccountRemotoRemote) c.lookup("java:global/ServerMDB/ServerMDB-ejb/GestoreAccountRemoto!GestoreAccountRemoto.GestoreAccountRemotoRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

}
