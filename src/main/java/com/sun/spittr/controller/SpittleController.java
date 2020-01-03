package com.sun.spittr.controller;

import com.sun.spittr.model.Spitter;
import com.sun.spittr.model.Spittle;
import com.sun.spittr.model.SpittleCreateForm;
import com.sun.spittr.repository.SpitterRepository;
import com.sun.spittr.repository.SpittleRepository;
import com.sun.spittr.service.MarkdownService;
import com.sun.spittr.service.SpittleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/spittle")
public class SpittleController {
    Logger logger = LoggerFactory.getLogger(SpittleController.class);

    @Autowired
    SpittleRepository spittleRepository;

    @Autowired
    SpittleService spittleService;

    @Autowired
    SpitterRepository spitterRepository;

    public void setSpitterRepository(SpitterRepository spitterRepository) {
        this.spitterRepository = spitterRepository;
    }

    public void setSpittleRepository(SpittleRepository spittleRepository) {
        this.spittleRepository = spittleRepository;
    }

    public void setSpittleService(SpittleService spittleService) {
        this.spittleService = spittleService;
    }

    @GetMapping("/all")
    public String getAllSpittles(
            @PageableDefault(value = 3, sort = { "createdAt" }, direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        Page<Spittle> spittlePage = spittleRepository.findAll(pageable);
        List<Spittle> spittles = spittlePage.getContent();
        logger.info("spittle page: " + spittlePage);
        model.addAttribute("spittles", spittles);
        model.addAttribute("spittlePage", spittlePage);
        return "spittle_all";
    }

    @GetMapping("/me")
    public String getMySpittles(
            @PageableDefault(value = 3, sort = { "createdAt" }, direction = Sort.Direction.DESC) Pageable pageable,
            Model model,
            Principal principal
    ) {
        Spitter spitter = spitterRepository.findByUsername(principal.getName());

        List<Spittle> spittles = spittleRepository.findBySpitter(spitter);
        Collections.sort(spittles);
        Page<Spittle> spittlePage = convertListToPage(spittles, pageable);
        logger.info("spittle me: " + spittles);
        logger.info("spittle page content: " + spittlePage.getContent());
        logger.info("spittle page index: " + spittlePage.getNumber());
        model.addAttribute("spittlePage", spittlePage);
        model.addAttribute("spittles", spittlePage.getContent());
        return "spittle_me";
    }

    @GetMapping(value = "/{spittleId}")
    public String showSpittleDetail(@PathVariable long spittleId, Model model) {
        Spittle spittle = spittleRepository.findById(spittleId).get();
        Spitter author = spittle.getSpitter();

        model.addAttribute("author", author);
        model.addAttribute("spittle", spittle);
        model.addAttribute("md", new MarkdownService().parseMarkdownString(spittle.getContent()));
        return "spittle_detail";
    }

    @GetMapping("/create")
    public String createSpittleForm(Model model) {
        SpittleCreateForm spittleCreateForm = new SpittleCreateForm();
        model.addAttribute("spittleCreateForm", spittleCreateForm);
        return "spittle_create";
    }

    @PostMapping("/create")
    public String createSpittle(@Valid SpittleCreateForm spittleForm, Errors errors, Principal principal) {
        if(errors.hasErrors()) {
            logger.info("create spittle errors:" + errors);
            return "spittle_create";
        }
        logger.info("spittle create, spitter name: " + principal.getName());
        Spitter spitter = spitterRepository.findByUsername(principal.getName());
        Spittle spittle = new Spittle(spittleForm.getTitle(), spittleForm.getContent(), spitter);
        logger.info("spittle create: " + spittle);
        spittleRepository.save(spittle);
        List<Spittle> spittles = spittleRepository.findBySpitter(spitter);
        long spittleId = spittles.get(spittles.size() - 1).getId();
        return "redirect:/spittle/" + spittleId;
    }



    @GetMapping("/edit/{spittleId}")
    public String editSpittleForm(@PathVariable long spittleId, Model model) {
        Spittle spittle = spittleRepository.findById(spittleId).get();

        SpittleCreateForm spittleCreateForm = new SpittleCreateForm();
        spittleCreateForm.setTitle(spittle.getTitle());
        spittleCreateForm.setContent(spittle.getContent());

        model.addAttribute("spittleCreateForm", spittleCreateForm);
        return "spittle_create";
    }

    @PostMapping("/edit/{spittleId}")
    public String editSpittle(
            @Valid SpittleCreateForm spittleForm,
            Errors errors,
            Principal principal,
            @PathVariable long spittleId
    ) {
        if(errors.hasErrors()) {
            logger.info("edit spittle errors:" + errors);
            return "spittle_create";
        }
        Spitter spitter = spitterRepository.findByUsername(principal.getName());
        Spittle spittle = spittleRepository.findById(spittleId).get();
        spittle.setTitle(spittleForm.getTitle());
        spittle.setContent(spittleForm.getContent());
        spittleRepository.save(spittle);
        return "redirect:/spittle/" + spittle.getId();
    }

    @GetMapping("/delete/{spittleId}")
    public String deleteSpittle(@PathVariable long spittleId) {
        spittleRepository.deleteById(spittleId);
        return "redirect:/";
    }

    @GetMapping("/follow")
    public String followedSpittles(
            @PageableDefault(value = 3, sort = { "createdAt" }, direction = Sort.Direction.DESC) Pageable pageable,
            Model model,
            Principal principal
    ) {

        List<Spittle> spittles = new ArrayList<>();
        Spitter curSpitter = spitterRepository.findByUsername(principal.getName());
        spittles.addAll(spittleRepository.findBySpitter(curSpitter));

        List<Spitter> followedSpitters = new ArrayList<>(curSpitter.getFollows());
        for(int i = 0; i < followedSpitters.size(); ++i) {
            Spitter spitter = followedSpitters.get(i);
            spittles.addAll(spittleRepository.findBySpitter(spitter));
        }
        Collections.sort(spittles);

        Page<Spittle> spittlePage = convertListToPage(spittles, pageable);

        logger.info("spittle all: " + spittles);
        logger.info("spittle page: " + spittlePage);
        model.addAttribute("spittlePage", spittlePage);
        model.addAttribute("spittles", spittlePage.getContent());
        return "spittle_follow";
    }

    public <T> Page<T> convertListToPage(List<T> list, Pageable pageable) {
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : ( start + pageable.getPageSize());
        return new PageImpl<T>(list.subList(start, end), pageable, list.size());
    }
}
