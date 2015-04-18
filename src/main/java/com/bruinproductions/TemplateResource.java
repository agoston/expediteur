package com.bruinproductions;

import com.bruinproductions.bean.TemplateMatchInput;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class TemplateResource {
    private static final Logger log = LoggerFactory.getLogger(TemplateResource.class);
    private static final Pattern GROUP_NAMES = Pattern.compile("\\(\\?<(.*?)>.*?\\)");

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
            final Matcher matcher = pattern.matcher(input.getInput());
            if (!matcher.matches()) continue;

            for (String groupName : getGroupNames(template)) {
                final String group = matcher.group(groupName);
                if (Strings.isNullOrEmpty(group)) continue;

                result.add(groupName, group);
            }
        }

        return result;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Malformed input")
    class MalformedInputException extends RuntimeException {
    }
}
