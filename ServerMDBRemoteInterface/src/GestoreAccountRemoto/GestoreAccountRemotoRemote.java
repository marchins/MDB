package GestoreAccountRemoto;

import LogicaDominio.Account;
import LogicaDominio.Autenticazione;
import java.util.HashMap;
import java.util.Vector;
import javax.ejb.Remote;

@Remote
public interface GestoreAccountRemotoRemote {

    Boolean verificaDatiAccount(String username, String password, String nome, String cognome, String email, String dataNascita);

    Account verificaDatiLogin(String username, String password);

    HashMap<String, Vector<Object>> sincronizzaDatiDaRemoto(Autenticazione autenticazione);

}
