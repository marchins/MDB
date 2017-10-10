package Test;

import DatabaseUtility.DatabaseUtilityRemote;
import Exceptions.ConnectionErrorException;
import Exceptions.LoginException;
import Exceptions.RegistrazioneException;
import Exceptions.VincoliInputException;
import GestoreAccountLocale.GestoreAccountLocale;
import LogicaDominio.Account;
import MultilanguageLabels.ErrorLabels;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LoginTest {

    public static EntityManagerFactory emf;
    public static EntityManager em;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() throws VincoliInputException, RegistrazioneException, ParseException, LoginException, ConnectionErrorException {
        emf = javax.persistence.Persistence.createEntityManagerFactory("ClientMDBPU");
        em = emf.createEntityManager();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        String miaData = "28/03/1988";
        Date data = formatDate.parse(miaData);

        // Registrazione di un account.
        GestoreAccountLocale.registrazione("userjohn", "passwordjohn", "John", "Doe", "johndoe@gmail.com", data);
    }

    /* Login: scenario di successo. */
    @Test
    public void test1() throws VincoliInputException, RegistrazioneException, NamingException, LoginException, ParseException, ConnectionErrorException {
        GestoreAccountLocale.login("userjohn", "passwordjohn");

        List<Account> accounts = (List<Account>) em.createQuery("SELECT a FROM Account a "
                + "WHERE a.username = 'userjohn'", Account.class).getResultList();
        assertEquals(1, accounts.size());
        Account account = accounts.get(0);
        assertEquals("passwordjohn", account.getPassword());
        assertEquals("John", account.getNome());
        assertEquals("Doe", account.getCognome());
        assertEquals("johndoe@gmail.com", account.getEmail());
        assertEquals("1988/03/28", account.getDataNascita().toString());

        // Simulo un logout cancellando l'account memorizzato in locale. 
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Account a").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    /* Login con credenziali sbagliate. */
    @Test
    public void test2() throws VincoliInputException, RegistrazioneException, NamingException, ParseException, LoginException, ConnectionErrorException {
        exception.expect(LoginException.class);
        exception.expectMessage(ErrorLabels.LOGIN_FAILED_ITA);
        GestoreAccountLocale.login("user", "password");
    }

    /* Test per username nullo. */
    @Test
    public void test3() throws VincoliInputException, ParseException, LoginException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.USER_NOT_SPECIFICIED_ITA);
        GestoreAccountLocale.login(null, "password");
    }

    /* Test per username troppo corto. */
    @Test
    public void test4() throws VincoliInputException, ParseException, LoginException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.USER_LENGTH_ERROR_ITA);
        GestoreAccountLocale.login("pr", "password");
    }

    /* Test per username troppo lungo. */
    @Test
    public void test5() throws VincoliInputException, ParseException, LoginException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.USER_LENGTH_ERROR_ITA);
        GestoreAccountLocale.login("provaprovaprovaprovaprovaprovaprovaprovaprova", "password");
    }

    /* Test per username non alfanumerico. */
    @Test
    public void test6() throws VincoliInputException, ParseException, LoginException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.USER_IS_NOT_ALPHANUMERIC_ITA);
        GestoreAccountLocale.login("?prova!", "password");
    }

    /* Test per password nulla. */
    @Test
    public void test7() throws VincoliInputException, ParseException, LoginException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.PASSWORD_NOT_SPECIFIED_ITA);
        GestoreAccountLocale.login("prova", null);
    }

    /* Test per password troppo corta. */
    @Test
    public void test8() throws VincoliInputException, ParseException, LoginException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.PASSWORD_LENGTH_ERROR_ITA);
        GestoreAccountLocale.login("prova", "pass");
    }

    /* Test per password troppo lunga. */
    @Test
    public void test9() throws VincoliInputException, ParseException, LoginException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.PASSWORD_LENGTH_ERROR_ITA);
        GestoreAccountLocale.login("prova", "passpasspasspasspasspasspasspasspass");
    }

    /* Test per password non alfanumerica. */
    @Test
    public void test10() throws VincoliInputException, ParseException, LoginException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.PASSWORD_IS_NOT_ALPHANUMERIC_ITA);
        GestoreAccountLocale.login("prova", "prova12$%");
    }

    /* Elimina la registrazione cancellando l'oggetto account nel DB remoto. */
    @AfterClass
    public static void tearDownClass() {
        DatabaseUtilityRemote dbUtility = lookupDatabaseUtilityRemote();
        dbUtility.svuotaTabellaAccount();
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
