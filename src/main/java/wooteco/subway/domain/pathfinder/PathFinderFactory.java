package wooteco.subway.domain.pathfinder;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;
import wooteco.subway.repository.SectionRepository;

@Component
public class PathFinderFactory implements FactoryBean<PathFinder> {

    private final SectionRepository repository;

    public PathFinderFactory(SectionRepository repository) {
        this.repository = repository;
    }

    @Override
    public PathFinder getObject() throws Exception {
        return ShortestPathFinder.of(repository.findAll());
    }

    @Override
    public Class<?> getObjectType() {
        return ShortestPathFinder.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
