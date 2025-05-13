package com.example.backend.Config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Handle React static resources
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/") // React build goes here
                .resourceChain(false)
                .addResolver(new PushStateResourceResolver());

        // Explicitly serve sitemap.xml, robots.txt, and Google verification
        registry.addResourceHandler(
                "/sitemap.xml",
                "/robots.txt",
                "/google*.html",
                "/yandex*.html" // ✅ Yandex verification support
        ).addResourceLocations("classpath:/static/");

    }

    private static class PushStateResourceResolver implements ResourceResolver {
        private final Resource index = new ClassPathResource("/static/index.html");

        private final List<String> handledExtensions = Arrays.asList(
                "html", "js", "json", "csv", "css", "png", "svg", "eot", "ttf", "otf",
                "woff", "appcache", "jpg", "jpeg", "gif", "ico", "mp3", "mp4");

        private final List<String> ignoredPaths = List.of("api");

        @Override
        public Resource resolveResource(HttpServletRequest request, String requestPath,
                                        List<? extends Resource> locations, ResourceResolverChain chain) {
            return resolve(requestPath, locations);
        }

        @Override
        public String resolveUrlPath(String resourcePath,
                                     List<? extends Resource> locations, ResourceResolverChain chain) {
            Resource resolvedResource = resolve(resourcePath, locations);
            if (resolvedResource == null) return null;
            try {
                return resolvedResource.getURL().toString();
            } catch (IOException e) {
                return resolvedResource.getFilename();
            }
        }

        private Resource resolve(String requestPath, List<? extends Resource> locations) {
            if (isIgnored(requestPath)) return null;

            if (isHandled(requestPath)) {
                return locations.stream()
                        .map(loc -> createRelative(loc, requestPath))
                        .filter(res -> res != null && res.exists())
                        .findFirst()
                        .orElse(null);
            }

            // fallback to index.html (SPA routing)
            return index;
        }

        private Resource createRelative(Resource resource, String relativePath) {
            try {
                return resource.createRelative(relativePath);
            } catch (IOException e) {
                return null;
            }
        }

        private boolean isIgnored(String path) {
            return ignoredPaths.stream().anyMatch(path::startsWith);
        }

        private boolean isHandled(String path) {
            String extension = StringUtils.getFilenameExtension(path);
            return handledExtensions.contains(extension);
        }
    }
}
