package house.thelittlemountaindev.afii.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Charlie One on 9/24/2017.
 */

public class AuthVerification {
    private FirebaseAuth firebaseAuth;

    public AuthVerification() {
    }

    public boolean logged() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            return true;
        }
        return false;
    }
}
