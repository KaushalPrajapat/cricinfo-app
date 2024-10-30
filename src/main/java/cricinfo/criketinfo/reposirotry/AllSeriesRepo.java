package cricinfo.criketinfo.reposirotry;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cricinfo.criketinfo.entities.AllSeries;

public interface AllSeriesRepo extends  JpaRepository<AllSeries, Integer > {

    Optional<AllSeries> findBySeriesTitle(String seriesTitle);

    
}
