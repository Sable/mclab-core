package mclab;

import com.googlecode.objectify.*;
import com.googlecode.objectify.util.DAOBase;

public class DAO extends DAOBase
{
    static {
        ObjectifyService.register(Bench.class);
        ObjectifyService.register(SavedQuery.class);
    }

    /** Your DAO can have your own useful methods */
    
}