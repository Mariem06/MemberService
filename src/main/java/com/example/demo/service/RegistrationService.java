package com.example.demo.service;

import com.example.demo.dao.MemberRepository;
import com.example.demo.entities.Membre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    @Autowired
    private MemberRepository registrationMembreRepository;
    private PasswordEncoder passwordEncoder;
    public Membre saveUser (Membre user){
        if(user.getPassword() !=null) {
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            String encodedapassword = bcrypt.encode(user.getPassword());
            user.setPassword(encodedapassword);
        }
        return   registrationMembreRepository.save(user);
    }

    public Membre fetchUserByEmail(String email){
        return registrationMembreRepository.findByEmail(email);
    }
/*
    public Membre fetchUserByEmailAndPassword ( String email , String password){
        return registrationMembreRepository.findByEmailAndPassword(email , password);


    }

 */
}