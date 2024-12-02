package sig.agm.users_service.Model.Utils;

public class AppUserNotFoundException  extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AppUserNotFoundException(String message) {
        super(message);
    }
}
