package Test;

import DatabaseUtility.DatabaseUtilityRemote;
import Exceptions.ConnectionErrorException;
import Exceptions.RegistrazioneException;
import Exceptions.VincoliInputException;
import GestoreAccountLocale.GestoreAccountLocale;
import MultilanguageLabels.ErrorLabels;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RegistrazioneTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /* Test username nullo. */
    @Test
    public void test1() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.USER_NOT_SPECIFICIED_ITA);
        GestoreAccountLocale.registrazione(null, "password", "nome", "cognome", "email", new Date());
    }

    /* Test username troppo corto. */
    @Test
    public void test2() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.USER_LENGTH_ERROR_ITA);
        GestoreAccountLocale.registrazione("pr", "password", "nome", "cognome", "email", new Date());
    }

    /* Test username troppo lungo. */
    @Test
    public void test3() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.USER_LENGTH_ERROR_ITA);
        GestoreAccountLocale.registrazione("provaprovaprovaprovaprovaprovaprovaprovaprova", "password", "nome", "cognome", "email", new Date());
    }

    /* Test username nullo. */
    @Test
    public void test4() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.USER_IS_NOT_ALPHANUMERIC_ITA);
        GestoreAccountLocale.registrazione("?prova!", "password", "nome", "cognome", "email", new Date());
    }

    /* Test password nulla. */
    @Test
    public void test5() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.PASSWORD_NOT_SPECIFIED_ITA);
        GestoreAccountLocale.registrazione("prova", null, "nome", "cognome", "email", new Date());
    }

    /* Test password troppo corta. */
    @Test
    public void test6() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.PASSWORD_LENGTH_ERROR_ITA);
        GestoreAccountLocale.registrazione("prova", "pass", "nome", "cognome", "email", new Date());
    }

    /* Test password troppo lunga. */
    @Test
    public void test7() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.PASSWORD_LENGTH_ERROR_ITA);
        GestoreAccountLocale.registrazione("prova", "passpasspasspasspasspasspasspasspass", "nome", "cognome", "email", new Date());
    }

    /* Test password non alfanumerica. */
    @Test
    public void test8() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.PASSWORD_IS_NOT_ALPHANUMERIC_ITA);
        GestoreAccountLocale.registrazione("prova", "prova12$%", "nome", "cognome", "email", new Date());
    }

    /* Test nome nullo. */
    @Test
    public void test9() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.NOME_NOT_SPECIFICIED_ITA);
        GestoreAccountLocale.registrazione("prova", "password", null, "cognome", "email", new Date());
    }

    /* Test nome troppo lungo. */
    @Test
    public void test10() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.NOME_LENGTH_ERROR_ITA);
        GestoreAccountLocale.registrazione("prova", "password",
                "nomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenomenome",
                "cognome", "email", new Date());
    }

    /* Test nome vuoto. */
    @Test
    public void test11() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.NOME_LENGTH_ERROR_ITA);
        GestoreAccountLocale.registrazione("prova", "password", "", "cognome", "email", new Date());
    }

    /* Test nome non alfanumerico. */
    @Test
    public void test12() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.NOME_IS_NOT_ALPHANUMERIC_ITA);
        GestoreAccountLocale.registrazione("prova", "password", "francesco+@#", "cognome", "email", new Date());
    }

    /* Test cognome nullo. */
    @Test
    public void test13() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.COGNOME_NOT_SPECIFICIED_ITA);
        GestoreAccountLocale.registrazione("prova", "password", "nome", null, "email", new Date());
    }

    /* Test cognome troppo lungo. */
    @Test
    public void test14() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.COGNOME_LENGTH_ERROR_ITA);
        GestoreAccountLocale.registrazione("prova", "password", "nome",
                "cognomecognomecognomecognomecognomecognomecognomecognomecognomecognomecognomecognomecognomecognome",
                "email", new Date());
    }

    /* Test cognome vuoto. */
    @Test
    public void test15() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.COGNOME_LENGTH_ERROR_ITA);
        GestoreAccountLocale.registrazione("prova", "password", "nome", "", "email", new Date());
    }

    /* Test cognome non alfanumerico. */
    @Test
    public void test16() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.COGNOME_IS_NOT_ALPHANUMERIC_ITA);
        GestoreAccountLocale.registrazione("prova", "password", "nome", "!£$cognome", "email", new Date());
    }

    /* Test email nulla. */
    @Test
    public void test17() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.EMAIL_NOT_SPECIFICIED_ITA);
        GestoreAccountLocale.registrazione("prova", "password", "nome", "cognome", null, new Date());
    }

    /* Test email non valida. */
    @Test
    public void test18() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.EMAIL_NOT_VALID_ITA);
        GestoreAccountLocale.registrazione("prova", "password", "nome", "cognome", "francesco@bianchi@gmail.com", new Date());
    }

    /* Test data nulla. */
    @Test
    public void test19() throws VincoliInputException, RegistrazioneException, ParseException, IOException, ConnectionErrorException {
        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.DATA_NOT_SPECIFICIED_ITA);
        GestoreAccountLocale.registrazione("prova", "password", "nome", "cognome", "francescobianchi@gmail.com", null);
    }

    /* Test registrazione con stesso username. */
    @Test
    public void test21() throws VincoliInputException, RegistrazioneException, NamingException, ParseException, IOException, ConnectionErrorException {
        exception.expect(RegistrazioneException.class);
        exception.expectMessage(ErrorLabels.REGISTRATION_ERROR_ITA);
        GestoreAccountLocale.registrazione("conti123", "password", "Marco", "Conti", "marco@gmail.com", new Date());
        GestoreAccountLocale.registrazione("conti123", "password123", "Luca", "Conti", "luca@gmail.com", new Date());
    }

    /* Test scenario di successo. */
    @Test
    public void test20() throws VincoliInputException, RegistrazioneException, ParseException, NamingException, IOException, ConnectionErrorException {
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        String miaData = "28/03/1988";
        Date data = formatDate.parse(miaData);
        GestoreAccountLocale.registrazione("user123", "passw123", "John", "Doe", "johndoe@gmail.com", data);
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
