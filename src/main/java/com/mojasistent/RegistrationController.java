package com.mojasistent;

import com.mojasistent.model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class RegistrationController {

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    NurseRepository nurseRepository;

    @Autowired
    FamilyMemberRepository familyMemberRepository;
    @Autowired
    private CoordinatorRepository coordinatorRepository;

    private final String LOZINKA_UPUTE = "Lozinka treba imati najmanje 8 znakova (minimalno 1 veliko slovo i 1 broj)";

    @GetMapping("/tologin")
    public String toLogin() {
        return "login.html";
    }

    @GetMapping("/register")
    public String register(Model model, @RequestParam("name") String name, @RequestParam("lastName") String lastName, @RequestParam("email") String email,
                           @RequestParam("password") String password, @RequestParam("mNumber") String number, @RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") Date date,
                           HttpSession session, String fMemberPassword, String fMemberEmail) {
        boolean containsDigit = false;
        boolean containsLowercase = false;
        boolean containsUpperCase = false;
        boolean nameVerification = false;
        boolean lastNameVerification = false;
        boolean nameAlph = false;
        boolean lastnameAlph = false;
        boolean emailExists = false;
        boolean fEmailExists = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                containsDigit = true;
                break;
            }
        }
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                containsLowercase = true;
                break;
            }
        }
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                containsUpperCase = true;
                break;
            }
        }
        for (char c : name.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                nameAlph = true;
                break;
            }
        }
        for (char c : lastName.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                lastnameAlph = true;
                break;
            }
        }
        if (Character.isUpperCase(name.charAt(0))) {
            nameVerification = true;
        }
        if (Character.isUpperCase(lastName.charAt(0))) {
            lastNameVerification = true;
        }
// Provjera koristi li neko pacijentov email
        Patient nPatient = patientRepository.findByEmail(email);
        FamilyMember nFamilyMember = familyMemberRepository.findByEmail(email);
        Nurse nNurse = nurseRepository.findByEmail(email);
        Coordinator nCoordinator = coordinatorRepository.findByEmail(email);

        if (nPatient == null && nFamilyMember == null && nNurse == null && nCoordinator == null) {
            emailExists = true;
        }

// Provjera koristi li neki email upisan u email člana obitelji
        Patient fPatient = patientRepository.findByEmail(email);
        FamilyMember fFamilyMember = familyMemberRepository.findByEmail(email);
        Nurse fNurse = nurseRepository.findByEmail(email);
        Coordinator fCoordinator = coordinatorRepository.findByEmail(email);

        if (fPatient == null && fFamilyMember == null && fNurse == null && fCoordinator == null) {
            fEmailExists = true;
        }


        if (password.length() >= 8 && containsDigit && containsUpperCase && containsLowercase && nameVerification && lastNameVerification && nameAlph
                && lastnameAlph && emailExists && fEmailExists) {
            Nurse nurse = nurseRepository.findById(1);
            Patient newPatient;
            if (fMemberEmail != null && fMemberPassword != null) {
                newPatient = new Patient(name, lastName, email, password, number, date, coordinatorRepository.findById(1L));
                patientRepository.save(newPatient);
                FamilyMember familyMember = new FamilyMember(fMemberEmail, fMemberPassword, newPatient);
                familyMemberRepository.save(familyMember);
                session.setAttribute("loggedPatient", newPatient);
                return "redirect:/form-01";
            } else {
                newPatient = new Patient(name, lastName, email, password, number, date, coordinatorRepository.findById(1L));
                patientRepository.save(newPatient);
                session.setAttribute("loggedPatient", newPatient);
                return "redirect:/form-01";
            }
        } else {
            if (!containsDigit || !containsUpperCase || !containsLowercase || password.length() < 8) {
                model.addAttribute("warningPassMessage", LOZINKA_UPUTE);
                return "registration.html";
            }
            if (!nameVerification) {
                model.addAttribute("warningNameMessage", "Ime treba sadržavati veliko početno slovo!");
                return "registration.html";
            }
            if (!lastNameVerification) {
                model.addAttribute("warningLastMessage", "Prezime treba sadržavati veliko početno slovo!");
                return "registration.html";
            }
            if (!nameAlph) {
                model.addAttribute("warningNameMessage", "Ime treba sadržavati samo slova!");
                return "registration.html";
            }
            if (!lastnameAlph) {
                model.addAttribute("warningNameMessage", "Ime treba sadržavati samo slova!");
                return "registration.html";
            }
            if (!emailExists) {
                model.addAttribute("warningMailMessage", "Email je već zauzet, koristi ga neki drugi korisnik!");
                return "registration.html";
            }
            if (!fEmailExists) {
                model.addAttribute("warningFMailMessage", "Email je već zauzet, koristi ga neki drugi korisnik!");
                return "registration.html";
            } else {
                model.addAttribute("warningMessage", "Došlo je do pogreške! Pokušajte ponovno! ");
                return "registration.html";
            }

        }


    }
}
