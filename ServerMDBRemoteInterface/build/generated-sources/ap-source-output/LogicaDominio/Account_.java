package LogicaDominio;

import LogicaDominio.Amico;
import LogicaDominio.Categoria;
import LogicaDominio.CopiaUtente;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.1.v20130918-rNA", date="2014-06-11T18:16:08")
@StaticMetamodel(Account.class)
public class Account_ { 

    public static volatile SingularAttribute<Account, Integer> id;
    public static volatile SingularAttribute<Account, String> username;
    public static volatile CollectionAttribute<Account, CopiaUtente> copieUtente;
    public static volatile SingularAttribute<Account, String> email;
    public static volatile CollectionAttribute<Account, Categoria> categorie;
    public static volatile SingularAttribute<Account, String> dataNascita;
    public static volatile SingularAttribute<Account, String> nome;
    public static volatile SingularAttribute<Account, String> password;
    public static volatile SingularAttribute<Account, String> cognome;
    public static volatile CollectionAttribute<Account, Amico> amici;

}