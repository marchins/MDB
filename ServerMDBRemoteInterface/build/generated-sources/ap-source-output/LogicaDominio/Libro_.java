package LogicaDominio;

import LogicaDominio.CopiaUtente;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.1.v20130918-rNA", date="2014-06-18T17:46:21")
@StaticMetamodel(Libro.class)
public class Libro_ { 

    public static volatile SingularAttribute<Libro, String> dataPubblicazione;
    public static volatile ListAttribute<Libro, CopiaUtente> copieUtente;
    public static volatile SingularAttribute<Libro, String> isbn;
    public static volatile SingularAttribute<Libro, String> autore;
    public static volatile SingularAttribute<Libro, String> titolo;
    public static volatile SingularAttribute<Libro, String> casaEditrice;
    public static volatile SingularAttribute<Libro, String> copertina;

}