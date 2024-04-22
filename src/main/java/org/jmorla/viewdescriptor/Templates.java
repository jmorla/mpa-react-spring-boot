package org.jmorla.viewdescriptor;

public class Templates {

    public static final String CONTROLLER_ADVICE_NAME = "PageAttributesController";

    public static final String CONTROLLER_ADVICE_TEMPLATE = """
        package %s;
        import org.springframework.core.io.ClassPathResource;
        import org.springframework.core.io.Resource;
        import org.springframework.web.bind.annotation.ControllerAdvice;
        import org.springframework.web.bind.annotation.ModelAttribute;
        import org.springframework.web.context.request.RequestAttributes;
        import org.springframework.web.context.request.RequestContextHolder;
        import org.springframework.web.context.request.ServletRequestAttributes;
        import org.springframework.web.method.HandlerMethod;
        import org.springframework.web.servlet.HandlerMapping;
        import org.springframework.web.servlet.ModelAndView;
        
        import com.fasterxml.jackson.core.type.TypeReference;
        import com.fasterxml.jackson.databind.ObjectMapper;
        
        import java.io.IOException;
        import java.util.Map;
        
        @ControllerAdvice
        public class PageAttributesController {
        
            private static final String DESCRIPTOR_PATH = "view_descriptor.json";
            protected static final String DEFAULT_VIEW = "index";
        
            @ModelAttribute
            private void setCommonModelAttributes(ModelAndView model) {
                var descriptors = loadViewDescriptors();
                
                var descriptor = descriptors.get(getDescriptorName());

                if(descriptor == null) {
                    return;
                }
                
                model.addObject("title", descriptor.getTitle());
                model.addObject("stylesheets", descriptor.getStylesheets());
                model.addObject("scripts", descriptor.getScripts());
                model.setViewName(DEFAULT_VIEW);
            }
        
            private Map<String, ViewDescriptor> loadViewDescriptors() {
                try {
                    Resource resource = new ClassPathResource(DESCRIPTOR_PATH);
                    ObjectMapper mapper = new ObjectMapper();
                    TypeReference<Map<String, ViewDescriptor>> typeRef = new TypeReference<Map<String,ViewDescriptor>>() {};
                    return mapper.readValue(resource.getContentAsByteArray(), typeRef);
                } catch (IOException ex) {
                    throw new RuntimeException(ex); // rethrow it for now
                }
            }
        
            public String getDescriptorName() {
                RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
                if (requestAttributes instanceof ServletRequestAttributes) {
                    ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
                    var handler = (HandlerMethod) servletRequestAttributes.getAttribute(
                            HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE,
                            RequestAttributes.SCOPE_REQUEST
                    );
                    return handler.getBeanType().getCanonicalName();
                }
                return null;
            }

            static class ViewDescriptor {
                private String title;        
                private String[] stylesheets;
                private String[] scripts;
                private String entrypoint;
            
                public void setTitle(String title) {
                    this.title = title;
                }
                public void setStylesheets(String[] stylesheets) {
                    this.stylesheets = stylesheets;
                }
                public void setScripts(String[] scripts) {
                    this.scripts = scripts;
                }
                public void setEntrypoint(String entrypoint) {
                    this.entrypoint = entrypoint;
                }
                public String getTitle() {
                    return title;
                }
                public String[] getStylesheets() {
                    return stylesheets;
                }
                public String[] getScripts() {
                    return scripts;
                }
                public String getEntrypoint() {
                    return entrypoint;
                }
            } 
        }   
        """;    
}
