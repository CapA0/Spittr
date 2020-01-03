package com.sun.spittr.controller;

import com.sun.spittr.model.Contact;
import com.sun.spittr.repository.ContactRepository;
import com.sun.spittr.service.MarkdownService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/contact")
public class ContactController {

    Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    ContactRepository contactRepository;
    @Autowired
    MarkdownService markdownService;

    @Autowired
    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String home(Model model) {

        List<Contact> contacts = contactRepository.findAll();
        logger.info("当前所有的 contact ：" + contacts);
        model.addAttribute("contacts", contacts);
//        model.addAttribute("md", markdownService.parseMarkdownString(contacts.get(1).getMessage()));
        return "contact";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submit(Contact contact) {
        contactRepository.save(contact);
        logger.info("添加了一个用户：" + contact);
        return "redirect:/";
    }

}
