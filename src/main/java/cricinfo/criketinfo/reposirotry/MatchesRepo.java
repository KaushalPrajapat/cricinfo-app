package cricinfo.criketinfo.reposirotry;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import cricinfo.criketinfo.entities.Matches;

public interface MatchesRepo extends MongoRepository<Matches, Integer>{

    Optional<Matches> findByTeamHeadingAndDate(String teamHeading, Date date);

    Optional<Matches> findByTeamHeading(String teamHeading);

    void deleteByTeamHeading(String teamHeading);
    
}
