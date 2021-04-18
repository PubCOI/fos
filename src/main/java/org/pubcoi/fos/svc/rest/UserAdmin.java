package org.pubcoi.fos.svc.rest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.pubcoi.fos.svc.exceptions.FosBadRequestException;
import org.pubcoi.fos.svc.exceptions.FosException;
import org.pubcoi.fos.svc.repos.mdb.FosUserRepo;
import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.models.dao.UpdateProfileRequestDAO;
import org.pubcoi.fos.svc.models.dao.UserLoginDAO;
import org.pubcoi.fos.svc.models.dao.UserProfileDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
public class UserAdmin {
    private static final Logger logger = LoggerFactory.getLogger(UserAdmin.class);

    final FosUserRepo userRepo;

    public UserAdmin(FosUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/api/login")
    public void doLogin(@RequestBody UserLoginDAO loginDAO) {
        // shortcut .. if we already have the UID, we know we've created the user:
        // if no match, it could be because the UID refers to another provider (eg they initially
        // logged in via GitHub but are now using Google
        if (userRepo.existsByUid(loginDAO.getUid())) {
            userRepo.save(userRepo.getByUid(loginDAO.getUid()).setLastLogin(OffsetDateTime.now()));
            return;
        }

        // adds user meta if it doesn't exist
        try {
            UserRecord record = FirebaseAuth.getInstance().getUser(loginDAO.getUid());
            userRepo.save(new FosUser()
                    .setUid(loginDAO.getUid())
                    .setDisplayName(record.getDisplayName())
                    .setLastLogin(OffsetDateTime.now())
            );
        } catch (FirebaseAuthException e) {
            logger.error(e.getMessage(), e);
            throw new FosException();
        }
    }

    @PostMapping("/api/profile")
    public UserProfileDAO getUserProfile(
            @RequestHeader("authToken") String authToken
    ) {
        String uid = UI.checkAuth(authToken).getUid();
        return new UserProfileDAO(userRepo.getByUid(uid));
    }

    @PutMapping("/api/profile")
    public UserProfileDAO updateUserProfile(
            @RequestBody UpdateProfileRequestDAO updateProfileRequestDAO,
            @RequestHeader("authToken") String authToken
    ) {
        String uid = UI.checkAuth(authToken).getUid();
        FosUser user = userRepo.getByUid(uid);
        if (null == user) throw new FosBadRequestException("Unable to find user");
        return new UserProfileDAO(userRepo.save(user.setDisplayName(updateProfileRequestDAO.getDisplayName())));
    }
}
