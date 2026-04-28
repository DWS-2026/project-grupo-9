package es.codeurjc.web.service;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;

@Service
public class HtmlSanitizerService {
    private final PolicyFactory policy = new HtmlPolicyBuilder()
        .allowElements(
            "p", "strong", "b", "em", "i", "ul", "u", "a", "ol", "li", "h1", "h2", "h3", "br"  
        )
    .allowAttributes("href").onElements("a")
    .allowUrlProtocols("https")
    .toFactory();

    public String sanitize(String input){
        return policy.sanitize(input);
    }

}
