package com.sun.spittr.service;

import com.sun.spittr.controller.ContactController;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.DataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;


@Service
public class MarkdownService {

    Logger logger = LoggerFactory.getLogger(MarkdownService.class);

    public String parseMarkdownString(String markdown) {
        Parser parser = Parser.builder(new DataSet()).build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Document document = parser.parse(markdown);
        String html = renderer.render(document);
//        logger.info("markdown html: " + html);
        return html;
    }

}
