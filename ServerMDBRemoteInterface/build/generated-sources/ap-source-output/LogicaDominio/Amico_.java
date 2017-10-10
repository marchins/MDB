package LogicaDominio;

import LogicaDominio.Account;
import LogicaDominio.CopiaUtente;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.1.v20130918-rNA", date="2014-06-11T18:16:08")
@StaticMetamodel(Amico.class)
public class Amico_ { 

    public static volatile SingularAttribute<Amico, String> id;
    public static volatile SingularAttribute<Amico, String> email;
    public static volatile CollectionAttribute<Amico, CopiaUtente> libroRicevutoinPrestito;
    public static volatile CollectionAttribute<Amico, CopiaUtente> libroPrestato;
    public static volatile SingularAttribute<Amico, Account> account;
    public static volatile SingularAttribute<Amico, String> nome;

}