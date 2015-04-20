package com.bruinproductions.endpoint;

import com.bruinproductions.bean.TemplateBean;
import com.bruinproductions.bean.TemplateMatchInput;
import com.bruinproductions.dao.TemplateRepository;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class TemplateResource {
    private static final Logger log = LoggerFactory.getLogger(TemplateResource.class);
    private static final Pattern GROUP_NAMES = Pattern.compile("\\(\\?<(.*?)>.*?\\)");

    @Autowired
    TemplateRepository templateRepository;

    // TODO: this is ugly but java.util.Pattern does not expose parsed group names - look for another regex lib?
    private static List<String> getGroupNames(String regex) {
        List<String> result = new ArrayList<>();
        final Matcher matcher = GROUP_NAMES.matcher(regex);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    /**
     * Test with:
     * curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST -d '{"input": "appel appel pear appel", "templates": ["(?<fruit>pear)"]}' http://localhost:8080/templates/find
     */
    @RequestMapping(value = "/templates/find", method = POST)
    public LinkedMultiValueMap findTemplate(@RequestBody TemplateMatchInput input) {
        if (CollectionUtils.isEmpty(input.getTemplates()) || Strings.isNullOrEmpty(input.getInput())) {
            throw new MalformedInputException();
        }

        LinkedMultiValueMap<String, String> result = new LinkedMultiValueMap<>();

        for (String template : input.getTemplates()) {
            // TODO: would be much more performant to pre-compile & store regexes server-side
            final Pattern pattern = Pattern.compile(".*" + template + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            final List<String> groupNames = getGroupNames(template);
            final Matcher matcher = pattern.matcher(input.getInput());

            while (matcher.find()) {
                for (String groupName : groupNames) {
                    final String group = matcher.group(groupName);
                    if (Strings.isNullOrEmpty(group)) continue;

                    result.add(groupName, group);
                }
            }
        }

        return result;
    }

    // TODO: catch known exceptions, throw @ResponseStatus exceptions
    @RequestMapping(value = "/templates", method = POST)
    public TemplateBean createTemplate(@RequestBody TemplateBean input) {
        input.setId(null);
        templateRepository.save(input);
        return input;
    }

    // TODO: catch known exceptions, throw @ResponseStatus exceptions
    @RequestMapping(value = "/templates/{id}", method = PUT)
    public TemplateBean updateTemplate(@PathVariable String id, @RequestBody TemplateBean input) {
        input.setId(id);
        templateRepository.save(input);
        return input;
    }

    // TODO: catch known exceptions, throw @ResponseStatus exceptions
    @RequestMapping(value = "/templates/{id}", method = GET)
    public TemplateBean readTemplate(@PathVariable String id) {
        return templateRepository.findOne(id);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Malformed input")
    class MalformedInputException extends RuntimeException {}
}
