package com.sun.spittr.controller;

import com.sun.spittr.model.Spitter;
import com.sun.spittr.model.SpitterEditForm;
import com.sun.spittr.model.SpitterSignupForm;
import com.sun.spittr.repository.SpitterRepository;
import com.sun.spittr.service.AvatarStorageService;
import com.sun.spittr.service.SpitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/spitter")
public class SpitterController {


    Logger logger = LoggerFactory.getLogger(SpitterController.class);

    @Autowired
    SpitterRepository spitterRepository;

    @Autowired
    SpitterService spitterService;

    @Autowired
    AvatarStorageService avatarStorageService;

    public SpitterController() {
    }

    public void setSpitterRepository(SpitterRepository spitterRepository) {
        this.spitterRepository = spitterRepository;
    }

    public void setSpitterService(SpitterService spitterService) {
        this.spitterService = spitterService;
    }

    @GetMapping("/edit")
    public String editProfile(Model model, Principal principal) {
        Spitter curSpitter = spitterRepository.findByUsername(principal.getName());

        SpitterEditForm spitterEditForm = new SpitterEditForm();
        spitterEditForm.setUsername(curSpitter.getUsername());
        spitterEditForm.setAboutMe(curSpitter.getAboutMe());
        model.addAttribute("spitterEditForm", spitterEditForm);
        return "spitter_profile_edit";
    }

    @PostMapping("/edit")
    public String editProfilePost(@Valid SpitterEditForm spitterEditForm, Errors errors, Principal principal) {
        if(errors.hasErrors()) {
            logger.info("edit errors: " + errors);
            return "spitter_profile_edit";
        }

        Spitter curSpitter = spitterRepository.findByUsername(principal.getName());
        curSpitter.setUsername(spitterEditForm.getUsername());
        if(spitterEditForm.getPassword().length() != 0) {
            curSpitter.setPasswordHash(new BCryptPasswordEncoder().encode(spitterEditForm.getPassword()));
        }
        curSpitter.setAboutMe(spitterEditForm.getAboutMe());

        logger.info("spitter edit form: " + spitterEditForm);
        spitterRepository.save(curSpitter);

        return "redirect:/spitter/" + curSpitter.getId();
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(Model model) {
        model.addAttribute("spitterSignupForm", new SpitterSignupForm());
        return "spitter_signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signupPost(@Valid SpitterSignupForm spitterSignupForm, Errors errors) {
        if(errors.hasErrors()) {
            logger.info("sign up errors:" + errors);
            return "spitter_signup";
        }
        logger.info("signup spitterSignupForm: " + spitterSignupForm);
        spitterRepository.save(new Spitter(spitterSignupForm.getUsername(), spitterSignupForm.getPassword()));
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "spitter_login";
    }

    @GetMapping("/{spitterId}")
    public String showSpitterProfile(@PathVariable long spitterId, Model model, Principal principal) {
        Spitter curSpitter = spitterRepository.findByUsername(principal.getName());
        Spitter spitter = spitterRepository.findById(spitterId).get();
        model.addAttribute("curSpitter", curSpitter);
        model.addAttribute("spitter", spitter);
        return "spitter_profile";
    }

    @GetMapping("/follow/{spitterId}")
    public String follow(@PathVariable long spitterId, Principal principal) {
        Spitter curSpitter = spitterRepository.findByUsername(principal.getName());
        Spitter spitter = spitterRepository.findById(spitterId).get();
        curSpitter.follow(spitter);
        spitterRepository.save(curSpitter);
        return "redirect:/spitter/" + spitterId;
    }

    @GetMapping("/unfollow/{spitterId}")
    public String unfollow(@PathVariable long spitterId, Principal principal) {
        Spitter curSpitter = spitterRepository.findByUsername(principal.getName());
        Spitter spitter = spitterRepository.findById(spitterId).get();
        curSpitter.unfollow(spitter);
        spitterRepository.save(curSpitter);
        return "redirect:/spitter/" + spitterId;
    }

    @GetMapping("/editavatar")
    public String editAvatar() {
        // return avatar upload page
        return "editavatar";
    }

    @PostMapping("/editavatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file, Principal principal) {

        Spitter curSpitter = spitterRepository.findByUsername(principal.getName());
        curSpitter.setAvatarUrl("" + curSpitter.getId() + ".jpg");
        spitterRepository.save(curSpitter);
        String fileName = avatarStorageService.storeFile(file, curSpitter);

        return "redirect:/spitter/" + curSpitter.getId();
    }

}
