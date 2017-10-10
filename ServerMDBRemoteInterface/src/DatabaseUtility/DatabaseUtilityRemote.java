package DatabaseUtility;

import javax.ejb.Remote;

@Remote
public interface DatabaseUtilityRemote {

    void svuotaTabellaAccount();

    void svuotaTabellaCategoria();

    void svuotaTabellaLibro();

    void svuotaTabellaCopieUtenti();

}
