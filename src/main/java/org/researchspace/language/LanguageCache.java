package org.researchspace.language;

import com.google.common.collect.Iterables;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.repository.Repository;
import org.researchspace.cache.CacheManager;
import org.researchspace.cache.ResourcePropertyCache;
import org.researchspace.config.NamespaceRegistry;
import org.researchspace.config.PropertyPattern;
import org.researchspace.thumbnails.ThumbnailServiceRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class LanguageCache {

    public static final String DEFAULT_LANGUAGE_SERVICE_CACHE_ID = "repository.LanguageCache";

    private NamespaceRegistry namespaceRegistry;
    private final CacheManager cacheManager;

    private ResourcePropertyCache<IRI, Literal> cache = new ResourcePropertyCache<IRI, Literal>(
            DEFAULT_LANGUAGE_SERVICE_CACHE_ID) {
        @Override
        protected IRI keyToIri(IRI iri) {
            return iri;
        }

        @Override
        protected java.util.Optional<CacheManager> cacheManager() {
            return Optional.of(cacheManager);
        };

        @Override
        protected Map<IRI, Optional<Literal>> queryAll(Repository repository, Iterable<? extends IRI> iris) {
            if (Iterables.isEmpty(iris)) {
                return Collections.emptyMap();
            }

            List<String> preferredLanguages = List.of("<http://www.researchspace.org/resource/hasLanguage>");
            try {
                List<PropertyPattern> languagePatterns = preferredLanguages.stream()
                        .map(pattern -> PropertyPattern.parse(pattern, namespaceRegistry)).collect(Collectors.toList());

                String query = constructPropertyQuery(iris, languagePatterns);

                Map<IRI, List<List<Literal>>> iriToListList = queryAndExtractProperties(repository, query,
                        languagePatterns.size(), value -> value instanceof Literal ? Optional.of((Literal) value) : Optional.empty());

                Map<IRI, Optional<Literal>> languages = new HashMap<>();
                for (IRI iri : iris) {
                    Optional<Literal> language = flattenProperties(iriToListList.get(iri)).stream()
                            .findFirst();
                    languages.put(iri, language);
                }

                return languages;
            } catch (Exception ex) {
                throw new RuntimeException("Failed to query for thumbnails of IRI(s).", ex);
            }
        }
    };

    @Inject
    public LanguageCache(NamespaceRegistry namespaceRegistry,
                         ThumbnailServiceRegistry thumbnailServiceRegistry, CacheManager cacheManager) {
        this.namespaceRegistry = namespaceRegistry;
        this.cacheManager = cacheManager;
        cacheManager.register(cache);
    }

    public Map<IRI, Optional<Literal>> getLanguage(Iterable<? extends IRI> resourceIris, Repository repository) {

        Map<IRI, Optional<Literal>> result = new HashMap<>();
        cache.getAll(repository, resourceIris).forEach((key, literal) -> result.put(key, literal));
        return result;
    }

    public void invalidate() {
        cache.invalidate();
    }
}
