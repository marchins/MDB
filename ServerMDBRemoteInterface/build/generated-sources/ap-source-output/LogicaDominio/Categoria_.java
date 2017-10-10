package LogicaDominio;

import LogicaDominio.Account;
import LogicaDominio.CopiaUtente;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.1.v20130918-rNA", date="2014-06-11T18:16:08")
@StaticMetamodel(Categoria.class)
public class Categoria_ { 

    public static volatile SingularAttribute<Categoria, String> id;
    public static volatile CollectionAttribute<Categoria, CopiaUtente> copieLibriAssociate;
    public static volatile SingularAttribute<Categoria, Account> account;
    public static volatile SingularAttribute<Categoria, String> nome;

}