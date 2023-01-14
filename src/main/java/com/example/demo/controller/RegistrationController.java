package com.example.demo.controller;

import com.example.demo.entities.Membre;
import com.example.demo.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/auth")

@RestController
@CrossOrigin
public class RegistrationController {
    @Autowired
    private RegistrationService service;


    @PostMapping("/registermembre")
    public Membre registrationUser (@RequestBody Membre membre) throws Exception {
        String tempEmailId = membre.getEmail();

        if(tempEmailId != null && !"".equals(tempEmailId)){
            Membre userObj = service.fetchUserByEmail(tempEmailId);
            if(userObj != null){
                throw new Exception("user with email is already exist"+ tempEmailId  );
            }
        }
        Membre userObj = service.saveUser(membre);
        return userObj;
    }

    @PostMapping("/login")
    public Membre loginUser (@RequestBody Membre user) throws Exception {
        String tempEmail = user.getEmail();
        String tempPassword = user.getPassword();

        Membre userObj =null;
        if ( tempEmail != null && tempPassword !=null){
            userObj = service.fetchUserByEmail(tempEmail);
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            Boolean isPasswordMatches = bcrypt.matches(tempPassword, userObj.getPassword());

            if (!isPasswordMatches) {
                userObj = null;
            }
        }
        if(userObj == null){
            throw new Exception("the user not exist");
        }
        return userObj;
    }

}
