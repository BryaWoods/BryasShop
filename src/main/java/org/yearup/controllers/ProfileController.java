/*package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.ProfileDao;
import org.yearup.models.Profile;

@RestController
@CrossOrigin
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileDao profileDao;

    @Autowired
    public ProfileController(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    // GET /profile
    @GetMapping
    public ResponseEntity<Profile> getProfile(Authentication authentication) {
        // Assuming you have a User object associated with Authentication
        // You need to fetch profile based on user id
        // Replace User.getUserId() with actual method to fetch user id
        int userId = getUserIdFromAuthentication(authentication);

        Profile profile = profileDao.getByUserId(userId);
        if (profile != null) {
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /profile
    @PutMapping
    public ResponseEntity<Void> updateProfile(Authentication authentication, @RequestBody Profile updatedProfile) {
        int userId = getUserIdFromAuthentication(authentication);

        Profile existingProfile = profileDao.getByUserId(userId);
        if (existingProfile != null) {
            // Update existing profile with new data
            existingProfile.setFirstName(updatedProfile.getFirstName());
            existingProfile.setLastName(updatedProfile.getLastName());
            existingProfile.setEmail(updatedProfile.getEmail());
            existingProfile.setPhone(updatedProfile.getPhone());

            profileDao.update(existingProfile);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Utility method to extract user ID from Authentication
    private int getUserIdFromAuthentication(Authentication authentication) {
        // Replace with your actual method to fetch user ID from authentication
        // Example: return userService.getUserId(authentication.getName());
        return 1; // Replace with actual logic
    }
}

*/

