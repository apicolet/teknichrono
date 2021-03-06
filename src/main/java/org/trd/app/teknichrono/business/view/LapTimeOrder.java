package org.trd.app.teknichrono.business.view;

import org.trd.app.teknichrono.model.compare.LapTimeDTOComparator;
import org.trd.app.teknichrono.model.compare.LapTimeDTOLastSeenComparator;
import org.trd.app.teknichrono.model.compare.LapTimeDTORaceComparator;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;

import java.time.Duration;
import java.util.List;

public class LapTimeOrder {

  public void orderByDuration(List<LapTimeDTO> results) {
    results.sort(new LapTimeDTOComparator());
    fillGapsInfoWhenOrderedByDuration(results);
  }


  /**
   * @param results List of laps ordered by duration. Laps with no duration at the end. Modified in place.
   */
  void fillGapsInfoWhenOrderedByDuration(List<LapTimeDTO> results) {
    Duration initialDuration = Duration.ofMillis(-1);
    Duration best = initialDuration;
    Duration previous = initialDuration;
    for (LapTimeDTO lapTimeDTO : results) {
      if (best.equals(initialDuration)) {
        best = lapTimeDTO.getDuration();
        previous = lapTimeDTO.getDuration();
        lapTimeDTO.setGapWithBest(Duration.ZERO);
        lapTimeDTO.setGapWithPrevious(Duration.ZERO);
      } else {
        Duration lapDuration = lapTimeDTO.getDuration();
        if (lapDuration != null && lapDuration.compareTo(Duration.ZERO) > 0) {
          lapTimeDTO.setGapWithBest(lapDuration.minus(best).abs());
          lapTimeDTO.setGapWithPrevious(lapDuration.minus(previous).abs());
        }
        previous = lapDuration;
      }
    }
  }

  public void orderForRace(List<LapTimeDTO> results) {
    LapTimeDTORaceComparator comparator = new LapTimeDTORaceComparator();
    results.sort(comparator);
    fillRaceGaps(results, comparator);
  }

  void fillRaceGaps(List<LapTimeDTO> results, LapTimeDTORaceComparator comparator) {
    LapTimeDTO best = null;
    LapTimeDTO previous = null;
    for (LapTimeDTO lapTimeDTO : results) {
      if (best == null) {
        best = lapTimeDTO;
        previous = lapTimeDTO;
        lapTimeDTO.setGapWithBest(Duration.ZERO);
        lapTimeDTO.setGapWithPrevious(Duration.ZERO);
      } else {
        lapTimeDTO.setGapWithBest(comparator.distance(best, lapTimeDTO));
        lapTimeDTO.setGapWithPrevious(comparator.distance(previous, lapTimeDTO));
        previous = lapTimeDTO;
      }
    }
  }

  public void orderbyLastSeen(List<LapTimeDTO> results) {
    results.sort(new LapTimeDTOLastSeenComparator());
  }

}
